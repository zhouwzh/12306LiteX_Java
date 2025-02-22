package com.wxy.javafxfrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class HomeController {
    private int userId;
    private String username;

    @FXML
    private ImageView logoImageView;
    @FXML
    private TextField departureField;
    @FXML
    private TextField arrivalField;
    @FXML
    private DatePicker departureDatePicker;
    @FXML
    private Button searchButton;
    @FXML
    private Button swapButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label searchWarnText;
    @FXML
    private TextField gptField;
    @FXML
    private Button gptButton;

    public void setParameters(int login_userId, String login_username) {
        this.userId = login_userId;
        this.username = login_username;
    }

    @FXML
    public void initialize() throws IOException {
        departureDatePicker.setValue(LocalDate.now());

        swapButton.setOnAction(event -> swapLocations());
    }

    @FXML
    public void search(ActionEvent event) throws IOException, InterruptedException {
        if (departureField.getText().isEmpty() || arrivalField.getText().isEmpty()) {
            searchWarnText.setText("Please enter valid departure and arrival locations.");
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("search.fxml"));
        Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
        SearchController controller = loader.getController();
        controller.setData(userId, username, departureField.getText(), arrivalField.getText(), departureDatePicker.getValue().toString());

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void swapLocations() {
        String from = departureField.getText();
        String to = arrivalField.getText();
        departureField.setText(to);
        arrivalField.setText(from);
    }


    @FXML
    private void logout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void accountClick(ActionEvent event) throws IOException, InterruptedException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("account.fxml"));
        Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
        AccountController controller = loader.getController();
        controller.setParameters(this.userId, this.username);

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void ordersClick(ActionEvent event) throws IOException, InterruptedException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("order.fxml"));
        Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
        OrderController controller = loader.getController();
        controller.setParameters(this.userId, this.username);

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void gptClick(ActionEvent event) throws IOException, InterruptedException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gpt.fxml"));
        Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
        GptController controller = loader.getController();
        controller.setInitialMessage(this.userId, this.username, gptField.getText());

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
