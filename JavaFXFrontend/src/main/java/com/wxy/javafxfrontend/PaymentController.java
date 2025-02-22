package com.wxy.javafxfrontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.wxy.javafxfrontend.OrderController.OrderItem;
import javafx.stage.Stage;


public class PaymentController {

    @FXML
    private Label orderInfoHeader;
    @FXML
    private Label departArrivalLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label trainNameLabel;
    @FXML
    private Label seatLabel;
    @FXML
    private Label priceLabel;

    @FXML
    private RadioButton visaOption;
    @FXML
    private RadioButton applePayOption;
    @FXML
    private RadioButton googlePayOption;
    @FXML
    private RadioButton paypalOption;

    @FXML
    private VBox visaPane;

    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField expiryDateField;
    @FXML
    private TextField cvvField;
    @FXML
    private TextField cardHolderNameField;

    @FXML
    private Button paymentButton;

    @FXML
    private Label paymentStatusLabel;

    @FXML
    private ToggleGroup toggleGroup;

    private int userId;
    private String username;
    private OrderItem orderItem;

    private HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        visaPane.setVisible(false);
        toggleGroup = new ToggleGroup();
        visaOption.setToggleGroup(toggleGroup);
        applePayOption.setToggleGroup(toggleGroup);
        googlePayOption.setToggleGroup(toggleGroup);
        paypalOption.setToggleGroup(toggleGroup);
    }

    public void setData(int userId, String username, OrderItem orderItem) {
        this.userId = userId;
        this.username = username;
        this.orderItem = orderItem;

        orderInfoHeader.setText("Order # " + orderItem.getInvoiceId() + " for " + username);
        departArrivalLabel.setText("Route: " + orderItem.getDepartStationName() + " → " + orderItem.getArrivalStationName());
        timeLabel.setText("Time: " + orderItem.getDepartureTime() + " - " + orderItem.getArrivalTime());
        trainNameLabel.setText("Train: " + orderItem.getTrainName());
        seatLabel.setText("Seat Level: " + orderItem.getSeatLevel());
        priceLabel.setText("Price: $" + orderItem.getPrice());
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("order.fxml"));
            Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
            OrderController controller = loader.getController();
            controller.setParameters(userId, username);

            Stage stage = (Stage) orderInfoHeader.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePaymentOptionChange() {
        visaPane.setVisible(visaOption.isSelected());
        if (visaOption.isSelected()) {
            paymentButton.setText("Proceed with Visa Card");
        } else if (applePayOption.isSelected()) {
            paymentButton.setText("Proceed with Apple Pay");
        } else if (googlePayOption.isSelected()) {
            paymentButton.setText("Proceed with Google Pay");
        } else if (paypalOption.isSelected()) {
            paymentButton.setText("Proceed with PayPal");
        }
    }

    @FXML
    private void handlePay() {
        paymentStatusLabel.setVisible(false);
        String paymentMethod = null;

        if (visaOption.isSelected()) {
            paymentMethod = "Visa";
            if (!validateVisaFields()) return;
        } else if (applePayOption.isSelected()) {
            paymentMethod = "ApplePay";
        } else if (googlePayOption.isSelected()) {
            paymentMethod = "GooglePay";
        } else if (paypalOption.isSelected()) {
            paymentMethod = "PayPal";
        }

        if (paymentMethod == null) {
            showError("Please select a payment method.");
            return;
        }

        try {
            String requestBody = "ticketid=" + orderItem.getTicketId();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8088/api/tickets/payment"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body().equals("payment success!")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("success.fxml"));
                Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
                SuccessController controller = loader.getController();
                controller.setParameters(userId, username);

                Stage stage = (Stage) orderInfoHeader.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                paymentStatusLabel.setText("Payment failed. Please try again.");
                paymentStatusLabel.setStyle("-fx-text-fill: red;");
                paymentStatusLabel.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error while processing payment.");
        }
    }

    private boolean validateVisaFields() {
        String cardNumber = cardNumberField.getText().trim();
        String expiryDate = expiryDateField.getText().trim();
        String cvv = cvvField.getText().trim();
        String cardHolder = cardHolderNameField.getText().trim();

        if (cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty() || cardHolder.isEmpty()) {
            showError("Please fill all card details.");
            return false;
        }
        return true;
    }

    private void showError(String msg) {
        paymentStatusLabel.setText(msg);
        paymentStatusLabel.setStyle("-fx-text-fill: red;");
        paymentStatusLabel.setVisible(true);
    }
}

