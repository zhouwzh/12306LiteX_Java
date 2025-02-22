package com.wxy.javafxfrontend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;


public class GptController {

    @FXML
    private VBox chatContainer;

    @FXML
    private TextField userInputField;

    private int userId;
    private String username;
    private String initialMessage;
    private HttpClient httpClient = HttpClient.newHttpClient();

    public void setInitialMessage(int userId, String username, String message) {
        this.userId = userId;
        this.username = username;
        this.initialMessage = message;
        addUserMessage(initialMessage);
        getGptResponse(initialMessage);
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

    @FXML
    private void handleSend() {
        String userMessage = userInputField.getText().trim();
        if (!userMessage.isEmpty()) {
            userInputField.clear();
            addUserMessage(userMessage);
            getGptResponse(userMessage);
        }
    }

    private void addUserMessage(String msg) {
        HBox messageBox = new HBox();
        messageBox.getStyleClass().add("message-box-user");

        Label messageLabel = new Label(msg);
        messageLabel.getStyleClass().add("user-message");
        messageLabel.setWrapText(true);

        messageBox.getChildren().add(messageLabel);
        chatContainer.getChildren().add(messageBox);
    }

    private void addGptMessage(String msg) {
        HBox messageBox = new HBox();
        messageBox.getStyleClass().add("message-box-gpt");

        Label messageLabel = new Label(msg);
        messageLabel.getStyleClass().add("gpt-message");
        messageLabel.setWrapText(true);

        messageBox.getChildren().add(messageLabel);
        chatContainer.getChildren().add(messageBox);
    }

    private void addGptHyperLink(String instruction, String param1, String param2, String param3) {
        if (Objects.equals(instruction, "None")){
            return;
        }
        HBox messageBox = new HBox();
        messageBox.getStyleClass().add("message-box-gpt");

        Hyperlink hyperlink = new Hyperlink("Click here to " + instruction);
        hyperlink.setOnAction(event -> callback(event, instruction, param1, param2, param3));
        hyperlink.getStyleClass().add("gpt-message");
        hyperlink.setWrapText(true);
        messageBox.getChildren().add(hyperlink);
        chatContainer.getChildren().add(messageBox);
    }

    private void callback(ActionEvent event, String instruction, String param1, String param2, String param3) {
        System.out.println(instruction + param1 + param2 + param3);
        switch (instruction) {
            case "Search": {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
                    Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
                    HomeController controller = loader.getController();
                    controller.setParameters(userId, username);

                    Stage stage = (Stage) chatContainer.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "MyAccount" : {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("account.fxml"));
                    Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
                    AccountController controller = loader.getController();
                    controller.setParameters(userId, username);

                    Stage stage = (Stage) chatContainer.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "MyOrders" : {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("order.fxml"));
                    Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
                    OrderController controller = loader.getController();
                    controller.setParameters(userId, username);

                    Stage stage = (Stage) chatContainer.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "ShowTrains" : {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("search.fxml"));
                    Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
                    SearchController controller = loader.getController();
                    controller.setData(userId, username, param1, param2, param3);

                    Stage stage = (Stage) chatContainer.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;
            }

        }

    }

    private void getGptResponse(String userMessage) {
        try {
            String requestBody = "message=" + userMessage;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8088/api/chat"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body() == null) {
                javafx.application.Platform.runLater(() -> addGptMessage("Error getting response."));
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(response.body());
            javafx.application.Platform.runLater(() -> addGptMessage(responseJson.get("message").asText()));
            if (responseJson.hasNonNull("Instruction")) {
                javafx.application.Platform.runLater(() -> addGptHyperLink(responseJson.get("Instruction").asText(), responseJson.get("Param1").asText(), responseJson.get("Param2").asText(), responseJson.get("Param3").asText()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> addGptMessage("Error getting response."));
        }
    }
}
