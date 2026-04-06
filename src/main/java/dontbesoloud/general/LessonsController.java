package dontbesoloud.general;

import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

import java.util.*;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Mat;
import dontbesoloud.utils.camera;

public class LessonsController {
    @FXML
    private GridPane selectionLayer;
    @FXML
    private VBox theoryLayer, practiceLayer;
    @FXML
    private BorderPane editorContainer;
    @FXML
    private TextArea theoryText;
    @FXML
    private ImageView cameraView, theoryImage;
    @FXML
    private Button deleteButton, saveButton, nextButton, prevButton;

    private boolean isCameraActive = false;

    private List<Lesson> lessonList = new ArrayList<>();
    private int currentLessonIndex = -1;

    private GeneralApiService generalApiService = new GeneralApiService();
    private int groupId;

    // Page start
    @FXML
    public void initialize() {
        lessonList = generalApiService.loadLessons(groupId);
        if (!lessonList.isEmpty() && lessonList != null) currentLessonIndex = 1;
        loadPage(currentLessonIndex);
    }


    @FXML
    private void addTheoryLayer() {
        lessonList.add(new Lesson(
                lessonList.size() + 1,
                groupId,
                "theory",
                "",
                0,
                "",
                ""
        ));
        loadPage(currentLessonIndex);
    }

    @FXML
    private void addPracticeLayer() {
        lessonList.add(new Lesson(
                lessonList.size() + 1,
                groupId,
                "practice",
                "",
                0,
                "",
                ""
        ));
        loadPage(currentLessonIndex);
    }

    private void changeLayer(String type) {
        prevButton.setDisable(currentLessonIndex < 1);
        nextButton.setDisable(type.equals("selection"));
        deleteButton.setDisable(type.equals("selection"));
        saveButton.setDisable(type.equals("selection"));

        selectionLayer.setVisible(type.equals("selection"));
        selectionLayer.setManaged(type.equals("selection"));

        theoryLayer.setVisible(type.equals("theory"));
        theoryLayer.setManaged(type.equals("theory"));

        practiceLayer.setVisible(type.equals("practice"));
        practiceLayer.setManaged(type.equals("practice"));
    }


    private void showSelectionLayer() {
        changeLayer("selection");

    }

    // Переключение на Теорию
    private void showTheoryLayer(Lesson lesson) {
        changeLayer("theory");


        theoryText.setText(lesson.getDescription());
        theoryImage.setImage(new Image(lesson.getPhotoUrl()));
    }

    // Переключение на Камеру
    private void showPracticeLayer(Lesson lesson) {
        changeLayer("practice");

        startWebcam();
    }

    private void loadPage(int index) {
        System.out.println(lessonList);
        System.out.println(currentLessonIndex);

        if (index < 0 || index >= lessonList.size() || lessonList.isEmpty() || lessonList == null) {
            currentLessonIndex = 0;
            showSelectionLayer();
        } else {
            Lesson lesson = lessonList.get(index);

            if (lesson.getType().equals("theory")) showTheoryLayer(lesson);
            if (lesson.getType().equals("practice")) showPracticeLayer(lesson);
        }
    }

    @FXML
    private void nextLesson() {
        currentLessonIndex++;
        if (isCameraActive) stopWebcam();

        loadPage(currentLessonIndex);
    }

    @FXML
    private void prevLesson() {
        currentLessonIndex--;
        if (isCameraActive) stopWebcam();

        loadPage(currentLessonIndex);
    }

    @FXML
    private void saveLesson() {

    }

    @FXML
    private void deleteLesson() {
        if (isCameraActive) stopWebcam();
        lessonList.remove(currentLessonIndex);
        currentLessonIndex--;
        loadPage(currentLessonIndex);
    }

    @FXML
    private void loadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите изображение для урока");


        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(dontbesoloud.Main.getStage());

        if (selectedFile != null) {
            try {
                String imageUrl = selectedFile.toURI().toURL().toString();
                Image image = new Image(imageUrl);
                theoryImage.setImage(image);

                if (currentLessonIndex >= 0 && currentLessonIndex < lessonList.size()) {
                    lessonList.get(currentLessonIndex).setPhotoUrl(imageUrl);
                }

                System.out.println("Фото загружено: " + imageUrl);
            } catch (Exception e) {
                System.err.println("Ошибка при загрузке изображения: " + e.getMessage());
            }
        }
    }

    @FXML
    private void editText() {

    }


    // Захват видео
    private VideoCapture capture;

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

    public void stopWebcam() {
        this.isCameraActive = false;
        if (this.capture != null && this.capture.isOpened()) {
            this.capture.release();
        }
    }

}

