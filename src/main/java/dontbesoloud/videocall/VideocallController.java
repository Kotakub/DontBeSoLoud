package dontbesoloud.videocall;

import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.application.Platform;
import javafx.scene.image.Image;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Mat;
import dontbesoloud.utils.camera;

public class VideocallController {
    // Эти ID должны СТРОГО совпадать с fx:id в вашем FXML файле
    @FXML
    private VBox searchLayer;
    @FXML
    private HBox callLayer; // Исправлено на HBox (как в верстке)
    @FXML
    private Label remotePlaceholder;

    @FXML
    private ImageView previewCamera; // Камера на экране поиска
    @FXML
    private ImageView localCamera;   // Ваша камера справа при звонке
    @FXML
    private ImageView remoteCamera;  // Камера собеседника слева

    @FXML
    private TextField chatInput;
    @FXML
    private Label remoteMessageLabel;
    @FXML
    private Label myLastMessage;

    private VideoCapture capture;
    private boolean isCameraActive = false;

    @FXML
    public void initialize() {
        // Начальное состояние: поиск виден, звонок скрыт
        searchLayer.setVisible(true);
        searchLayer.setManaged(true);
        callLayer.setVisible(false);
        callLayer.setManaged(false);

        // Запускаем превью вашей камеры
        startWebcam(previewCamera);
    }

    @FXML
    private void startSearch() {
        // Переключаем слои
        searchLayer.setVisible(false);
        searchLayer.setManaged(false);
        callLayer.setVisible(true);
        callLayer.setManaged(true);

        // Переносим показ вашей камеры в правое окно звонка
        // Для этого сначала "сбрасываем" старое окно и передаем новое
        previewCamera.setImage(null);
        startWebcam(localCamera);
        
        // Показываем заглушку для собеседника
        remotePlaceholder.setVisible(true);
        remoteCamera.setVisible(false);


        // В реальном проекте здесь будет вызов API для поиска партнера
        System.out.println("Поиск начат...");
    }

    // Метод, который вы вызовете, когда сервер найдет вам человека
    private void onPartnerFound() {
        Platform.runLater(() -> {
            remotePlaceholder.setVisible(false);
            remoteCamera.setVisible(true);
            remoteMessageLabel.setText("Собеседник подключился!");
        });
    }

    @FXML
    private void sendChatMessage() {
        String message = chatInput.getText();
        if (message != null && !message.isEmpty()) {
            myLastMessage.setText("Вы: " + message);
            chatInput.clear();
            // Здесь логика отправки сообщения через WebSocket
        }
    }

    private void startWebcam(ImageView targetView) {
        if (this.capture == null) {
            this.capture = new VideoCapture(0);
        }
        this.isCameraActive = true;

        Runnable frameGrabber = () -> {
            while (isCameraActive) {
                Mat frame = new Mat();
                if (capture.read(frame)) {
                    Image imageToShow = camera.mat2Image(frame);
                    // Важно: рисуем именно в тот ImageView, который передали в метод
                    Platform.runLater(() -> targetView.setImage(imageToShow));
                }
            }
        };

        Thread thread = new Thread(frameGrabber);
        thread.setDaemon(true);
        thread.start();
    }

    public void stopWebcam() {
        this.isCameraActive = false;
        if (capture != null && capture.isOpened()) {
            capture.release();
        }
    }

    @FXML
    private void endCall() { /* Логика возврата к поиску */ }

    @FXML
    private void nextPeer() { /* Логика поиска нового */ }

    @FXML
    private void toggleCamera() { /* Вкл/Выкл */ }
}