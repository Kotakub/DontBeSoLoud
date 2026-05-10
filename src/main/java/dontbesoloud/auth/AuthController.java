package dontbesoloud.auth;

import dontbesoloud.Main;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class AuthController {

    // Connecting elements FXML with their fx:id
    @FXML
    private Label titleLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button mainButton;
    @FXML
    private Label authError;
    @FXML
    private Hyperlink switchLink;

    private boolean isRegistrationMode = false;
    private boolean isValid = false;
    private AuthApiService apiService = new AuthApiService();

    @FXML
    public void initialize() {
        updatePage();
    }

    @FXML
    private void submitAction() {
        String email = emailField.getText(); // Get email
        String password = passwordField.getText(); // Get password
        String token;

        if (email.isEmpty() || password.isEmpty()) {
            showError("Ошибка. Отсутствует почта или пароль");
        } else {
            isValid = true;
        }
        if (isValid) {
            isValid = false;
            if (isRegistrationMode) {
                String name = nameField.getText();
                token = apiService.registration(name, email, password);
            } else {
                token = apiService.authorization(email, password);
            }


            if (!token.isEmpty()) {
                authError.setVisible(false);
                authError.setManaged(false);
                try {
                    Main.saveToken(token);
                    Main.loadPage("/dontbesoloud/general/general.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showError("");
            }
        }
        
        try {
            Main.loadPage("/dontbesoloud/general/general.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleAuth() {
        isRegistrationMode = !isRegistrationMode;
        updatePage();
    }

    private void showError(String errorText) {
        if (isRegistrationMode && errorText.isEmpty()) {
            authError.setText("Ошибка. Пользователь с такой почтой уже существует.");
        } else if (!isRegistrationMode && errorText.isEmpty()) {
            authError.setText("Ошибка. Неверный логин или пароль.");
        } else {
            authError.setText(errorText);
        }
        authError.setVisible(true);
        authError.setManaged(true);

    }

    private void clearPage() {
        // Clearing all fields
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        authError.setVisible(false);
        authError.setManaged(false);
    }

    private void updatePage() {
        // Switch name field
        nameLabel.setVisible(isRegistrationMode);
        nameLabel.setManaged(isRegistrationMode);

        nameField.setVisible(isRegistrationMode);
        nameField.setManaged(isRegistrationMode);

        // Clearing fields
        clearPage();

        if (isRegistrationMode) {
            titleLabel.setText("РЕГИСТРАЦИЯ");
            mainButton.setText("СОЗДАТЬ АККАУНТ");
            switchLink.setText("Уже есть аккаунт? Войти");
        } else {
            titleLabel.setText("АВТОРИЗАЦИЯ");
            mainButton.setText("ВОЙТИ");
            switchLink.setText("Нет аккаунта? Зарегистрироваться");
        }
    }
}