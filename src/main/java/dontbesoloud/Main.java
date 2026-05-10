package dontbesoloud;

import dontbesoloud.auth.AuthApiService;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nu.pattern.OpenCV;

import java.util.prefs.Preferences;
import java.io.IOException;

public class Main extends Application {
    private static Stage stage;
    private static Preferences prefs = Preferences.userNodeForPackage(Main.class);
    private static AuthApiService apiService = new AuthApiService();
    private static Object currentController; // Храним текущий контроллер здесь

    @Override
    public void start(Stage stg) throws IOException {
        OpenCV.loadShared();
        stage = stg;

        String token = prefs.get("jwt_token", null);
        if (token != null && apiService.isTokenValid(token)) {
            loadPage("/dontbesoloud/general/general.fxml");
        } else {
            loadPage("/dontbesoloud/auth/auth.fxml");
        }
    }

    public static FXMLLoader loadPage(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        currentController = fxmlLoader.getController();

        stage.setTitle("DontBeSoLoud");
        stage.setScene(scene);
        stage.show();

        return fxmlLoader;
    }

    public static Object getCurrentController() {
        return currentController;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void saveToken(String token) {
        prefs.put("jwt_token", token);
    }

    public static void deleteToken() {
        prefs.put("jwt_token", "");
    }

    public static String getToken() {
        return prefs.get("jwt_token", null);
    }

    public static boolean isAdmin() {
        return true;
        // return apiService.isAdmin(prefs.get("jwt_token", null));
    }

    public static void main(String[] args) {
        launch();
    }
}