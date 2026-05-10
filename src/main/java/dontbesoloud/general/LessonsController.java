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
    private StackPane photoUploadBox;
    @FXML
    private Button deleteButton, saveButton, nextButton, prevButton;
    @FXML
    private TextField equalSign;
    private boolean isCameraActive = false;

    private List<Lesson> lessonList = new ArrayList<>();
    private int currentLessonIndex = -1;

    private GeneralApiService generalApiService = new GeneralApiService();
    private int groupId;


    private Process pythonProcess;
    private final ZmqService zmqService = new ZmqService();
    private String lastRecognizedSign = "None";

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
                -1,
                groupId,
                "theory",
                "",
                0,
                "",
                "Это жест, обозначающий букву А. Чтобы его показать просто сожмите руку в кулак."
        ));
        loadPage(currentLessonIndex);
    }

    @FXML
    private void addPracticeLayer() {
        lessonList.add(new Lesson(
                -1,
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
        prevButton.setDisable(false);
        nextButton.setDisable(type.equals("selection"));
        deleteButton.setDisable(type.equals("selection"));
        saveButton.setDisable(type.equals("selection"));
        deleteButton.setVisible(false);
        saveButton.setVisible(false);

        selectionLayer.setVisible(type.equals("selection"));
        selectionLayer.setManaged(type.equals("selection"));

        theoryLayer.setVisible(type.equals("theory"));
        theoryLayer.setManaged(type.equals("theory"));

        practiceLayer.setVisible(type.equals("practice"));
        practiceLayer.setManaged(type.equals("practice"));
    }

    // Переключение на Выбор типа занятия
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
        startPythonEngine();
//        stopPythonEngine();
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


    // Навигация
    @FXML
    private void nextLesson() {
        currentLessonIndex++;
//        if (isCameraActive) stopWebcam();

        loadPage(currentLessonIndex);
    }

    @FXML
    private void prevLesson() {
        currentLessonIndex--;
//        if (isCameraActive) stopWebcam();

        loadPage(currentLessonIndex);
    }

    @FXML
    private void saveLesson() {

    }

    @FXML
    private void deleteLesson() {
//        if (isCameraActive) stopWebcam();
        lessonList.remove(currentLessonIndex);
        currentLessonIndex--;
        loadPage(currentLessonIndex);
    }


    // Теория
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

                ((Label) photoUploadBox.getChildren().get(0)).setVisible(false);
                photoUploadBox.getStyleClass().add("has-image");

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


    // Практика


    private void handleData(String sign, String base64Image) {
        // Обновляем текст, только если жест изменился
        if (!sign.equals(lastRecognizedSign)) {
            lastRecognizedSign = sign;
            Platform.runLater(() -> {
                System.out.println("Новый жест: " + sign);

            });
            equalSign.setText("А");
        }

        // Отрисовка кадра
        try {
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
            Image img = new Image(new java.io.ByteArrayInputStream(imageBytes));
            Platform.runLater(() -> cameraView.setImage(img));
        } catch (Exception e) {
        }
    }

    private void startPythonEngine() {
        if (pythonProcess != null && pythonProcess.isAlive()) return;

        try {
            String pythonPath = "/usr/local/bin/python3.13";
            String scriptPath = "/Users/kotakub/Documents/DontBeSoLoud/python_ml/ml.py";

            ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptPath);
            pb.redirectErrorStream(true);
            pythonProcess = pb.start();

            // Передаем данные в handleData
            zmqService.startListening((sign, base64Image) -> {
                handleData(sign, base64Image); // Используем твой готовый метод
            });
        } catch (Exception e) {
            System.err.println("Ошибка запуска Python: " + e.getMessage());
        }
    }

    public void stopPythonEngine() {
        zmqService.stop();
        if (pythonProcess != null && pythonProcess.isAlive()) {
            pythonProcess.destroy(); // Завершаем процесс
            System.out.println("Java: Python-движок остановлен.");
        }
    }
}
