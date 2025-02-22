package com.wxy.javafxfrontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class OrderController {

    @FXML
    private VBox orderContainer;

    private int userId;
    private String username;
    private HttpClient httpClient = HttpClient.newHttpClient();

    public void setParameters(int userId, String username) {
        this.userId = userId;
        this.username = username;
        loadOrders();
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
        HomeController controller = loader.getController();
        controller.setParameters(userId, username);

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void loadOrders() {
        try {
            String requestBody = "userid=" + userId;
            String url = "http://localhost:8088/api/user/tickets";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, OrderItem.class);
            List<OrderItem> orders = mapper.readValue(response.body(), listType);

            renderOrders(orders);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void renderOrders(List<OrderItem> orders) {
        orderContainer.getChildren().clear();
        if (orders != null && !orders.isEmpty()) {
            for (OrderItem order : orders) {
                VBox orderItem = createOrderItem(order);
                orderContainer.getChildren().add(orderItem);
            }
        } else {
            Label noOrdersLabel = new Label("No orders found.");
            noOrdersLabel.getStyleClass().add("order-info-label");
            orderContainer.getChildren().add(noOrdersLabel);
        }
    }

    private VBox createOrderItem(OrderItem order) {
        VBox orderBox = new VBox(10);
        orderBox.getStyleClass().add("order-item");

        Label headerLabel = new Label(order.getTrainName() + ": " + order.getDepartStationName() + " → " + order.getArrivalStationName());
        headerLabel.getStyleClass().add("order-header");

        Label departureLabel = new Label("Departure: " + order.getDepartureTime());
        departureLabel.getStyleClass().add("order-info-label");

        Label arrivalLabel = new Label("Arrival: " + order.getArrivalTime());
        arrivalLabel.getStyleClass().add("order-info-label");

        Label seatLabel = new Label("Seat Level: " + order.getSeatLevel());
        seatLabel.getStyleClass().add("order-info-label");

        Label priceLabel = new Label("Price: $" + order.getPrice());
        priceLabel.getStyleClass().add("order-price-label");

        Label paymentStateLabel = new Label("Payment State: " + (order.getPaymentState().equals("true") ? "Paid" : "Not Paid"));
        paymentStateLabel.getStyleClass().add("order-price-label");

        Label statusLabel = new Label("Status: " + order.getValidState());
        statusLabel.getStyleClass().add("order-price-label");

        orderBox.getChildren().addAll(headerLabel, departureLabel, arrivalLabel, seatLabel, priceLabel, paymentStateLabel, statusLabel);

        HBox buttonContainer = new HBox();
        boolean isPaid = false;
        if (!"true".equals(order.getPaymentState()) && "pending".equals(order.getValidState())) {
            isPaid = true;
            buttonContainer.setSpacing(10);

            Button payNowButton = new Button("Pay Now");
            payNowButton.getStyleClass().add("pay-now-button");
            payNowButton.setOnAction(event -> handlePayNow(event, order));

            buttonContainer.getChildren().add(payNowButton);
            orderBox.getChildren().add(buttonContainer);
        }

        if (!"refund".equals(order.getValidState()) && !"cancelled".equals(order.getValidState())) {
            buttonContainer.setSpacing(10);

            Button cancelButton = new Button("Cancel/Refund");
            cancelButton.getStyleClass().add("pay-now-button");
            cancelButton.setOnAction(event -> {
                try {
                    handleCancel(event, order);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            buttonContainer.getChildren().add(cancelButton);
            if (!isPaid) orderBox.getChildren().add(buttonContainer);
        }

        return orderBox;
    }

    private void handleCancel(ActionEvent event, OrderItem order) throws IOException, InterruptedException {
        String requestBody = "ticketid=" + order.getTicketId();
        String url = "http://localhost:8088/api/ticket/cancel";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if ("cancel success".equals(response.body())) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("success.fxml"));
            Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
            SuccessController controller = loader.getController();
            controller.setParameters(userId, username);
            controller.setSuccessLabel("Cancel Success! Please return to Home to proceed.");

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else if ("refund success".equals(response.body())) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("success.fxml"));
            Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
            SuccessController controller = loader.getController();
            controller.setParameters(userId, username);
            controller.setSuccessLabel("Refund Success! Please return to Home to proceed.");

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

    private void handlePayNow(ActionEvent event, OrderItem order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("payment.fxml"));
            Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
            PaymentController controller = loader.getController();
            controller.setData(userId, username, order);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 根据示例返回信息创建OrderItem类以接收数据
    public static class OrderItem {
        private int ticketId;
        private double price;
        private String departStationName;
        private String arrivalStationName;
        private String seatLevel;
        private String departureTime;
        private String arrivalTime;
        private String trainName;
        private int invoiceId;
        private String validState;
        private String paymentState;

        public int getTicketId() {
            return ticketId;
        }

        public void setTicketId(int ticketId) {
            this.ticketId = ticketId;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getDepartStationName() {
            return departStationName;
        }

        public void setDepartStationName(String departStationName) {
            this.departStationName = departStationName;
        }

        public String getArrivalStationName() {
            return arrivalStationName;
        }

        public void setArrivalStationName(String arrivalStationName) {
            this.arrivalStationName = arrivalStationName;
        }

        public String getSeatLevel() {
            return seatLevel;
        }

        public void setSeatLevel(String seatLevel) {
            this.seatLevel = seatLevel;
        }

        public String getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(String departureTime) {
            this.departureTime = departureTime;
        }

        public String getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(String arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public String getTrainName() {
            return trainName;
        }

        public void setTrainName(String trainName) {
            this.trainName = trainName;
        }

        public int getInvoiceId() {
            return invoiceId;
        }

        public void setInvoiceId(int invoiceId) {
            this.invoiceId = invoiceId;
        }

        public String getValidState() {
            return validState;
        }

        public void setValidState(String validState) {
            this.validState = validState;
        }

        public String getPaymentState() {
            return paymentState;
        }

        public void setPaymentState(String paymentState) {
            this.paymentState = paymentState;
        }
    }
}
