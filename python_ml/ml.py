import cv2
import mediapipe as mp
import numpy as np
import onnxruntime as ort
import json
import os
import zmq
import time
import base64

# Настройка путей
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_PATH = os.path.join(BASE_DIR, "model.onnx")
LABELS_PATH = os.path.join(BASE_DIR, "labels.json")
TASK_PATH = os.path.join(BASE_DIR, "hand_landmarker.task")

# ZMQ Server
context = zmq.Context()
socket = context.socket(zmq.PUB)
socket.bind("tcp://127.0.0.1:5555")

with open(LABELS_PATH, "r", encoding="utf-8") as f:
    labels = json.load(f)

# Сессия (CPU для Mac)
session = ort.InferenceSession(MODEL_PATH, providers=["CPUExecutionProvider"])
input_name = session.get_inputs()[0].name

# MediaPipe
BaseOptions = mp.tasks.BaseOptions
HandLandmarker = mp.tasks.vision.HandLandmarker
HandLandmarkerOptions = mp.tasks.vision.HandLandmarkerOptions
VisionRunningMode = mp.tasks.vision.RunningMode

options = HandLandmarkerOptions(
    base_options=BaseOptions(model_asset_path=TASK_PATH),
    running_mode=VisionRunningMode.VIDEO,
    num_hands=2,
)


def softmax(x):
    e_x = np.exp(x - np.max(x))
    return e_x / e_x.sum()


SKIP_FRAMES = 10
frame_count = 0
last_sign = "None"

sequence = []
SEQ_LENGTH = 50
detector = HandLandmarker.create_from_options(options)
cap = cv2.VideoCapture(0)

print("ZMQ Server started. Listening for gestures...")

while cap.isOpened():
    success, frame = cap.read()
    if not success:
        break

    frame_count += 1

    # 1. Распознавание жеста (только каждый N-й кадр)
    if frame_count % SKIP_FRAMES == 0:
        rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        mp_image = mp.Image(image_format=mp.ImageFormat.SRGB, data=rgb_frame)
        result = detector.detect_for_video(mp_image, int(time.time() * 1000))

        hand_points = []
        if result.hand_landmarks:
            for i in range(2):
                if i < len(result.hand_landmarks):
                    landmarks = result.hand_landmarks[i]
                    w = landmarks[0]  # Запястье для нормализации
                    for lm in landmarks:
                        hand_points.extend([lm.x - w.x, lm.y - w.y, lm.z - w.z])
                else:
                    hand_points.extend([0.0] * 63)

            sequence.append(hand_points)

        sequence = sequence[-SEQ_LENGTH:]

        if len(sequence) == SEQ_LENGTH:
            input_data = np.array([sequence], dtype=np.float32)
            outputs = session.run(None, {input_name: input_data})

            probs = softmax(outputs[0][0])
            res = np.argmax(probs)
            conf = probs[res]

            if conf > 0.35:
                last_sign = labels[res]

    _, buffer = cv2.imencode(".jpg", frame, [int(cv2.IMWRITE_JPEG_QUALITY), 70])
    jpg_as_text = base64.b64encode(buffer).decode("utf-8")
    socket.send_string(f"{last_sign}|{jpg_as_text}")
    time.sleep(0.03)

cap.release()
detector.close()
