package com.wxy.javafxfrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;

public class BooksuccessController {

    @FXML
    private Label successLabel;

    private OrderController.OrderItem orderItem;
    private boolean state;
    private int userId;
    private String username;
    private String message;

    @FXML
    public void initialize() throws IOException, InterruptedException {}

    public void setParameters(boolean state, int userId, String username, OrderController.OrderItem orderItem, String message) throws InterruptedException, IOException {
        this.state = state;
        this.userId = userId;
        this.username = username;
        this.orderItem = orderItem;
        this.message = message;

        successLabel.setText(message);
    }

    @FXML
    private void gotoPay(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("payment.fxml"));
        Scene scene = new Scene(loader.load(),Settings.get_x(), Settings.get_y());
        PaymentController controller = loader.getController();
        controller.setData(userId, username, orderItem);

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
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
