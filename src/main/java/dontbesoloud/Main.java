package dontbesoloud;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Указываем путь к FXML относительно папки resources
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/dontbesoloud/auth/auth.fxml"));

        // Создаем сцену (ширина и высота подберутся автоматически, если не указывать)
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);

        stage.setTitle("Авторизация");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}