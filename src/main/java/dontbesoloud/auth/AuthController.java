package dontbesoloud.auth;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AuthController {

    // Связываем элементы из FXML по их fx:id
    @FXML private Label titleLabel;
    @FXML private Label nameLabel;
    @FXML private Label passwordSubmitLabel;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField passwordSubmitField;
    @FXML private Button mainButton;
    @FXML private Hyperlink switchLink;

    private boolean isRegistrationMode = false;

    @FXML
    public void initialize() {
        updatePage();
    }

    @FXML
    private void submitAction() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (isRegistrationMode) {
            String name = nameField.getText();
            System.out.println("Регистрация: " + name + ", " + email);
            // Здесь будет логика сохранения пользователя
        } else {
            System.out.println("Вход: " + email);
            // Здесь будет логика проверки пароля
        }
    }

    @FXML
    private void toggleAuth() {
        isRegistrationMode = !isRegistrationMode;
        updatePage();
    }

    private void updatePage() {
        // Включаем/Выключаем поле имени
        nameLabel.setVisible(isRegistrationMode);
        nameLabel.setManaged(isRegistrationMode);

        nameField.setVisible(isRegistrationMode);
        nameField.setManaged(isRegistrationMode);

        // Включаем/Выключаем поле с подтверждением пароля
        passwordSubmitLabel.setVisible(isRegistrationMode);
        passwordSubmitLabel.setManaged(isRegistrationMode);

        passwordSubmitField.setVisible(isRegistrationMode);
        passwordSubmitField.setManaged(isRegistrationMode);

        if (isRegistrationMode) {
            titleLabel.setText("Регистрация");
            mainButton.setText("Создать аккаунт");
            switchLink.setText("Уже есть аккаунт? Войти");
        } else {
            titleLabel.setText("Авторизация");
            mainButton.setText("Войти");
            switchLink.setText("Нет аккаунта? Зарегистрироваться");
        }
    }
}