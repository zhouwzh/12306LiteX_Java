package com.wxy.javafxfrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginController {

    @FXML
    private VBox loginPanel;
    @FXML
    private VBox registerPanel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField registerUsernameField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private PasswordField registerConfirmPasswordField;
    @FXML
    private Label registerWarnText;
    @FXML
    private Label loginWarnText;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String key = AESUtils.generateKey("12306LiteX");

    public LoginController() throws Exception {}

    @FXML
    private void onLogin(ActionEvent event) throws Exception {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            System.out.println("Username and password cannot be empty! Try again.");
            return;
        }

        if ("admin".equals(usernameField.getText()) && "admin".equals(passwordField.getText())) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
            Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            return;
        }

        String encryptedPassword = AESUtils.encrypt(passwordField.getText(), key);

        String requestBody = "username=" + usernameField.getText() + "&password=" + encryptedPassword;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8088/api/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseBody = objectMapper.readValue(response.body(), HashMap.class);

            String status = (String) responseBody.get("status");
            if ("success".equals(status)) {
                int userId = (int) responseBody.get("userId");
                loginWarnText.setText("Login Successful, welcome! Your User ID is: " + userId);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
                Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
                HomeController controller = loader.getController();
                controller.setParameters(userId, usernameField.getText());

                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
        } else {
            loginWarnText.setText("Login Failed, Invalid username or password.");
        }
    }

    @FXML
    private void onRegister() throws Exception {
        if (!Objects.equals(registerPasswordField.getText(), registerConfirmPasswordField.getText())) {
            registerWarnText.setText("Passwords do not match! Try again.");
            return;
        }
        if (registerUsernameField.getText().isEmpty() || registerPasswordField.getText().isEmpty() || registerConfirmPasswordField.getText().isEmpty()) {
            registerWarnText.setText("Username and password cannot be empty! Try again.");
            return;
        }

        String encryptedPassword = AESUtils.encrypt(registerPasswordField.getText(), key);

        String requestBody = "username=" + registerUsernameField.getText() + "&password=" + encryptedPassword;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8088/api/register"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.body().equals("Register Success")) {
            loginWarnText.setText("Register success! Please login.");
            switchToLogin();
        } else {
            registerWarnText.setText("Register failed! Try again.");
        }
    }

    @FXML
    private void switchToRegister() {
        loginPanel.setVisible(false);
        loginPanel.setManaged(false);
        registerPanel.setVisible(true);
        registerPanel.setManaged(true);
    }

    @FXML
    private void switchToLogin() {
        registerPanel.setVisible(false);
        registerPanel.setManaged(false);
        loginPanel.setVisible(true);
        loginPanel.setManaged(true);
    }
}
