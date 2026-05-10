package dontbesoloud.videocall;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dontbesoloud.utils.camera;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Base64;

public class VideocallController {
    @FXML
    private VBox searchLayer;
    @FXML
    private HBox callLayer;
    @FXML
    private Label remotePlaceholder;
    @FXML
    private ImageView previewCamera, localCamera, remoteCamera;
    @FXML
    private TextField chatInput;
    @FXML
    private Label remoteMessageLabel, myLastMessage;

    private VideoCapture capture;
    private boolean isCameraActive = false;
    private VideocallApiService apiService;
    private String currentSign = "None";
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        showSearchLayer();
        startWebcam(previewCamera, true);
    }

    @FXML
    private void startSearch() {
        try {
            apiService = new VideocallApiService(new URI("ws://127.0.0.1:8000/videocall/ws"),
                    this::handleServerMessage);
            apiService.connect();
            remotePlaceholder.setText("ПОИСК СОБЕСЕДНИКА...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleServerMessage(String message) {
        try {
            JsonNode json = mapper.readTree(message);
            String type = json.get("type").asText();

            Platform.runLater(() -> {
                switch (type) {
                    case "waiting":
                        showCallLayer();
                        remotePlaceholder.setText("В ОЧЕРЕДИ...");
                        break;
                    case "peer_found":
                        break;
                    case "video":
                        if (json.has("data"))
                            updateRemoteImage(json.get("data").asText());
                        break;
                    case "chat":
                        if (json.has("text"))
                            remoteMessageLabel.setText("Собеседник: " + json.get("text").asText());
                        break;
                    case "peer_left":
                        remoteMessageLabel.setText("Собеседник отключился");
                        break;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleCamera() {
        if (isCameraActive) {
            isCameraActive = false;
            if (capture != null)
                capture.release();
            localCamera.setImage(null);
            previewCamera.setImage(null);
            System.out.println("Камера выключена");
        } else {
            startWebcam(callLayer.isVisible() ? localCamera : previewCamera, true);
            System.out.println("Камера включена");
        }
    }

    @FXML
    private void sendChatMessage() {
        String text = chatInput.getText();
        if (text != null && !text.isEmpty() && apiService != null && apiService.isOpen()) {
            try {
                ObjectNode node = mapper.createObjectNode();
                node.put("type", "chat");
                node.put("text", text);

                apiService.send(mapper.writeValueAsString(node));
                myLastMessage.setText("Вы: " + text);
                chatInput.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateRemoteImage(String base64) {
        try {
            byte[] data = Base64.getDecoder().decode(base64);
            remoteCamera.setImage(new Image(new ByteArrayInputStream(data)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSearchLayer() {
        searchLayer.setVisible(true);
        searchLayer.setManaged(true);
        callLayer.setVisible(false);
        callLayer.setManaged(false);
    }

    private void showCallLayer() {
        searchLayer.setVisible(false);
        searchLayer.setManaged(false);
        callLayer.setVisible(true);
        callLayer.setManaged(true);
    }

    @FXML
    private void nextPeer() {
        if (apiService != null)
            apiService.skip();
    }

    private void startWebcam(ImageView targetView, boolean streamToServer) {
        if (this.capture == null || !this.capture.isOpened())
            this.capture = new VideoCapture(0);
        this.isCameraActive = true;

        new Thread(() -> {
            while (isCameraActive) {
                Mat frame = new Mat();
                if (capture.read(frame)) {
                    Image img = camera.mat2Image(frame);
                    Platform.runLater(() -> {
                        if (searchLayer.isVisible())
                            previewCamera.setImage(img);
                        else
                            localCamera.setImage(img);
                    });

                    if (streamToServer && apiService != null && apiService.isOpen() && callLayer.isVisible()) {
                        apiService.sendVideoData(matToBase64(frame), currentSign);
                    }
                }
            }
        }).start();
    }

    private String matToBase64(Mat frame) {
        MatOfByte mem = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, mem);
        return Base64.getEncoder().encodeToString(mem.toArray());
    }

    @FXML
    private void endCall() {
        if (apiService != null)
            apiService.close();
        showSearchLayer();
    }
}