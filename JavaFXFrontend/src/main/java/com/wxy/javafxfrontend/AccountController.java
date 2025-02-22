package com.wxy.javafxfrontend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AccountController implements Initializable {

    @FXML
    private StackPane contentStack;

    // UserInfo Pane elements
    @FXML
    private ScrollPane userInfoPane;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label infoIncompleteLabel;
    @FXML
    private Label fnameLabel;
    @FXML
    private Label lnameLabel;
    @FXML
    private Label birthDateLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label nationalityLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneLabel;

    // ChangePassword Pane elements
    @FXML
    private VBox changePasswordPane;
    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label passwordChangeMessage;

    // EditProfile Pane elements
    @FXML
    private ScrollPane editProfilePane;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField fnameField;
    @FXML
    private TextField lnameField;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private TextField genderField;
    @FXML
    private TextField nationalityField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private Label profileSaveMessage;
    @FXML
    private TextField passwordConfirmField;

    private int userId;
    private String username;
    private UserInfo storedUserInfo;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String key = AESUtils.generateKey("12306LiteX");

    public AccountController() throws Exception {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showUserInfoPane();
    }

    public void setParameters(int userId, String username) throws IOException, InterruptedException {
        this.userId = userId;
        this.username = username;

        UserInfo userInfo = getUserInfoFromServer(userId);
        updateUserInfoPane(userInfo);
        updateEditProfileFields(userInfo);
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        Scene scene = new Scene(loader.load(),Settings.get_x(), Settings.get_y());
        HomeController controller = loader.getController();
        controller.setParameters(userId, username);

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void showUserInfo() {
        showUserInfoPane();
    }

    @FXML
    private void showChangePassword() {
        userInfoPane.setVisible(false);
        changePasswordPane.setVisible(true);
        editProfilePane.setVisible(false);
    }

    @FXML
    private void showEditProfile() {
        userInfoPane.setVisible(false);
        changePasswordPane.setVisible(false);
        editProfilePane.setVisible(true);
    }

    private void showUserInfoPane() {
        userInfoPane.setVisible(true);
        changePasswordPane.setVisible(false);
        editProfilePane.setVisible(false);
    }

    @FXML
    private void handleChangePassword() throws Exception {
        String oldPass = oldPasswordField.getText().trim();
        String newPass = newPasswordField.getText().trim();
        String confirmPass = confirmPasswordField.getText().trim();

        if (newPass.equals(confirmPass)) {
            String encryptedOldPassword = AESUtils.encrypt(oldPass, key);
            String encryptedNewPassword = AESUtils.encrypt(newPass, key);

            String requestBody = "userid=" + userId + "&oldPassword=" + encryptedOldPassword + "&newPassword=" + encryptedNewPassword;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8088/api/user/change_password"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body().equals("Reset Password Success")) {
                passwordChangeMessage.setText("Password changed successfully!");
                passwordChangeMessage.setStyle("-fx-text-fill:green;");
            } else {
                passwordChangeMessage.setText("Failed to change password.");
                passwordChangeMessage.setStyle("-fx-text-fill:red;");
            }
        } else {
            passwordChangeMessage.setText("New password and confirmation do not match.");
            passwordChangeMessage.setStyle("-fx-text-fill:red;");
        }
    }

    @FXML
    private void handleSaveProfile() throws IOException, InterruptedException {
        UserInfo updatedInfo = new UserInfo();
        updatedInfo.setId(userId);
        updatedInfo.setPassword(passwordConfirmField.getText().trim());
        updatedInfo.setUsername(usernameField.getText().trim());
        updatedInfo.setFname(fnameField.getText().trim());
        updatedInfo.setLname(lnameField.getText().trim());
        updatedInfo.setBirthDate(birthDatePicker.getValue() == null ? null : birthDatePicker.getValue().atStartOfDay());
        updatedInfo.setGender(genderField.getText().trim());
        updatedInfo.setNationality(nationalityField.getText().trim());
        updatedInfo.setEmail(emailField.getText().trim());
        updatedInfo.setPhone(phoneField.getText().trim());

        boolean success = updateUserInfoOnServer(updatedInfo);
        if (success) {
            profileSaveMessage.setText("Profile updated successfully!");
            profileSaveMessage.setStyle("-fx-text-fill:green;");
            updateUserInfoPane(this.storedUserInfo);
        } else {
            profileSaveMessage.setText("Failed to update profile.");
            profileSaveMessage.setStyle("-fx-text-fill:red;");
        }
    }

    private void updateUserInfoPane(UserInfo userInfo) {
        userNameLabel.setText("Username: " + userInfo.getUsername());
        boolean hasOtherInfo = (userInfo.getFname() != null && !userInfo.getFname().isEmpty()) ||
                (userInfo.getLname() != null && !userInfo.getLname().isEmpty()) ||
                (userInfo.getBirthDate() != null) ||
                (userInfo.getGender() != null && !userInfo.getGender().isEmpty()) ||
                (userInfo.getNationality() != null && !userInfo.getNationality().isEmpty()) ||
                (userInfo.getEmail() != null && !userInfo.getEmail().isEmpty()) ||
                (userInfo.getPhone() != null && !userInfo.getPhone().isEmpty());

        if (!hasOtherInfo) {
            infoIncompleteLabel.setVisible(true);
            fnameLabel.setVisible(false);
            lnameLabel.setVisible(false);
            birthDateLabel.setVisible(false);
            genderLabel.setVisible(false);
            nationalityLabel.setVisible(false);
            emailLabel.setVisible(false);
            phoneLabel.setVisible(false);
        } else {
            infoIncompleteLabel.setVisible(false);
            fnameLabel.setVisible(userInfo.getFname() != null && !userInfo.getFname().isEmpty());
            fnameLabel.setText("First Name: " + (userInfo.getFname() == null ? "" : userInfo.getFname()));

            lnameLabel.setVisible(userInfo.getLname() != null && !userInfo.getLname().isEmpty());
            lnameLabel.setText("Last Name: " + (userInfo.getLname() == null ? "" : userInfo.getLname()));

            birthDateLabel.setVisible(userInfo.getBirthDate() != null);
            birthDateLabel.setText("Birth Date: " + (userInfo.getBirthDate() == null ? "" : userInfo.getBirthDate().toLocalDate().toString()));

            genderLabel.setVisible(userInfo.getGender() != null && !userInfo.getGender().isEmpty());
            genderLabel.setText("Gender: " + (userInfo.getGender() == null ? "" : userInfo.getGender()));

            nationalityLabel.setVisible(userInfo.getNationality() != null && !userInfo.getNationality().isEmpty());
            nationalityLabel.setText("Nationality: " + (userInfo.getNationality() == null ? "" : userInfo.getNationality()));

            emailLabel.setVisible(userInfo.getEmail() != null && !userInfo.getEmail().isEmpty());
            emailLabel.setText("Email: " + (userInfo.getEmail() == null ? "" : userInfo.getEmail()));

            phoneLabel.setVisible(userInfo.getPhone() != null && !userInfo.getPhone().isEmpty());
            phoneLabel.setText("Phone: " + (userInfo.getPhone() == null ? "" : userInfo.getPhone()));
        }
    }

    private void updateEditProfileFields(UserInfo userInfo) {
        usernameField.setText(userInfo.getUsername());
        fnameField.setText(userInfo.getFname());
        lnameField.setText(userInfo.getLname());
        if (userInfo.getBirthDate() != null) {
            birthDatePicker.setValue(userInfo.getBirthDate().toLocalDate());
        }
        genderField.setText(userInfo.getGender());
        nationalityField.setText(userInfo.getNationality());
        emailField.setText(userInfo.getEmail());
        phoneField.setText(userInfo.getPhone());
    }

    private UserInfo getUserInfoFromServer(int userId) throws IOException, InterruptedException {
        String requestBody = "userId=" + userId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8088/api/user/info"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        UserInfo info = mapper.readValue(response.body(), UserInfo.class);

        info.setUsername(username);

        return info;
    }


    private boolean updateUserInfoOnServer(UserInfo updatedInfo) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String json = mapper.writeValueAsString(updatedInfo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8088/api/user/change_information"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        UserInfo returnInfoAssure = mapper.readValue(response.body(), UserInfo.class);

        if (returnInfoAssure.getId() == updatedInfo.getId()) {
            this.storedUserInfo = returnInfoAssure;
            return true;
        } else {
            return false;
        }
    }

    public static class UserInfo {
        private int id;
        private String password;
        private String username;
        private String fname;
        private String lname;
        private LocalDateTime birthDate;
        private String gender;
        private String nationality;
        private String email;
        private String phone;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getLname() {
            return lname;
        }

        public void setLname(String lname) {
            this.lname = lname;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public LocalDateTime getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(LocalDateTime birthDate) {
            this.birthDate = birthDate;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
