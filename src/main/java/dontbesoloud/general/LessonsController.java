package dontbesoloud.general;

import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.util.*;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.opencv.videoio.VideoCapture; // Требуется библиотека OpenCV
import org.opencv.core.Mat;
import dontbesoloud.utils.camera;

public class LessonsController {
    @FXML
    private GridPane selectionLayer;
    @FXML
    private VBox theoryLayer, cameraLayer;
    @FXML
    private BorderPane editorContainer;
    @FXML
    private TextArea theoryText;
    @FXML
    private ImageView cameraView;

    private boolean isTheory;
    private List<Lesson> lessonList = new ArrayList<>();
    private int currentLessonIndex = 0;
    private VideoCapture capture;
    private boolean isCameraActive = false;

    // Page start
    @FXML
    public void initialize() {
        loadLesson(currentLessonIndex);
    }

    @FXML
    private void showSelectionLayer() {
    }

    // Переключение на Теорию
    @FXML
    private void showTheoryLayer() {
        isTheory = true;
        activateEditor();
        System.out.println("Ive clicked");
    }

    // Переключение на Камеру
    @FXML
    private void showCameraLayer() {
        isTheory = false;
        activateEditor();
        System.out.println("Ive clicked");
    }

    private void activateEditor() {
        selectionLayer.setVisible(false);
        selectionLayer.setManaged(false);


        theoryLayer.setVisible(isTheory);
        theoryLayer.setManaged(isTheory);

        cameraLayer.setVisible(!isTheory);
        cameraLayer.setManaged(!isTheory);

        if (!isTheory) {
            startWebcam();
        } else {
            stopWebcam();
        }

    }

    @FXML
    private void nextLesson() {
        currentLessonIndex++;
        if (isCameraActive) {
            stopWebcam();
        }
        System.out.println("Переход к следующему уроку");
        loadLesson(currentLessonIndex);
    }

    @FXML
    private void prevLesson() {
        currentLessonIndex--;
        if (isCameraActive) {
            stopWebcam();
        }
        System.out.println("Переход к предыдущему уроку");
        loadLesson(currentLessonIndex);
    }

    @FXML
    private void saveLesson() {
        // Сохранение текущего состояния в базу через FastAPI
    }

    @FXML
    private void deleteLesson() {
        // Удаление текущего урока
    }

    @FXML
    private void loadPhoto() {

    }

    @FXML
    private void editText() {

    }


    private void loadLesson(int index) {
        if (index < 0 || index >= lessonList.size()) return;

        Lesson lesson = lessonList.get(index);

        // Автоматически включаем нужный слой в зависимости от данных
        boolean isTheory = lesson.type.equals("theory");
        activateEditor();

        if (isTheory) {
            theoryText.setText(lesson.description);
            // theoryImage.setImage(new Image(lesson.photoUrl)); // Раскомментируй, когда будут ссылки
        } else {
            startWebcam();
        }
    }

    private void startWebcam() {
        this.capture = new VideoCapture(0); // 0 - id стандартной камеры
        this.isCameraActive = true;

        Runnable frameGrabber = () -> {
            while (isCameraActive) {
                Mat frame = new Mat();
                if (capture.read(frame)) {
                    // Преобразуем кадр OpenCV (Mat) в Image для JavaFX
                    Image imageToShow = camera.mat2Image(frame);

                    // Обновляем UI в основном потоке JavaFX
                    Platform.runLater(() -> cameraView.setImage(imageToShow));
                }
            }
        };

        Thread thread = new Thread(frameGrabber);
        thread.setDaemon(true); // Поток закроется при выходе из приложения
        thread.start();
    }

    private void stopWebcam() {
        this.isCameraActive = false;
        if (this.capture != null && this.capture.isOpened()) {
            this.capture.release();
        }
    }

}

