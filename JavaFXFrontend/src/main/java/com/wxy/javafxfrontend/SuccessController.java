package com.wxy.javafxfrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class SuccessController {
    private int userId;
    private String username;

    @FXML
    private Label successLabel;

    public void setSuccessLabel(String successMessage) {
        successLabel.setText(successMessage);
    }

    public void setParameters(int userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @FXML
    private void backToHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
            Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
            HomeController controller = loader.getController();
            controller.setParameters(userId, username);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
