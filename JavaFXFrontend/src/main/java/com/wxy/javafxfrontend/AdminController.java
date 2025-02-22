package com.wxy.javafxfrontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class AdminController {

    @FXML
    private ImageView logoImageView;

    @FXML
    private ImageView trainMapView;

    @FXML
    private ScrollPane scheduleScrollPane;

    @FXML
    private VBox scheduleContainer;

    private HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
        return;
    }

    @FXML
    private void showTrainMap() {
        trainMapView.setVisible(true);
        scheduleScrollPane.setVisible(false);
    }

    @FXML
    private void showTrainSchedules() throws IOException, InterruptedException {
        trainMapView.setVisible(false);
        scheduleScrollPane.setVisible(true);
        loadTrainSchedules();
    }

    private void loadTrainSchedules() throws IOException, InterruptedException {
        scheduleContainer.getChildren().clear();
        String url = "http://localhost:8088/api/path_station";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, PathStation.class);
        List<PathStation> pathStations = mapper.readValue(response.body(), listType);

        for (PathStation train : pathStations) {
            Label trainLabel = new Label(train.toString());
            trainLabel.getStyleClass().add("panel-subtitle");
            scheduleContainer.getChildren().add(trainLabel);
        }
    }

    public static class PathStation {
        private int pathid;
        private String start_time;
        private int station_id;
        private String station_name;
        private String station_type;
        private String train_name;
        private int  a_seats_available;
        private int  b_seats_available;
        private int  c_seats_available;

        public String getTrain_name() {
            return train_name;
        }

        public void setTrain_name(String train_name) {
            this.train_name = train_name;
        }

        public int getPathid() {
            return pathid;
        }

        public void setPathid(int pathid) {
            this.pathid = pathid;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public int getStation_id() {
            return station_id;
        }

        public void setStation_id(int station_id) {
            this.station_id = station_id;
        }

        public String getStation_name() {
            return station_name;
        }

        public void setStation_name(String station_name) {
            this.station_name = station_name;
        }

        public String getStation_type() {
            return station_type;
        }

        public void setStation_type(String station_type) {
            this.station_type = station_type;
        }

        public int getA_seats_available() {
            return a_seats_available;
        }

        public void setA_seats_available(int a_seats_available) {
            this.a_seats_available = a_seats_available;
        }

        public int getB_seats_available() {
            return b_seats_available;
        }

        public void setB_seats_available(int b_seats_available) {
            this.b_seats_available = b_seats_available;
        }

        public int getC_seats_available() {
            return c_seats_available;
        }

        public void setC_seats_available(int c_seats_available) {
            this.c_seats_available = c_seats_available;
        }

        @Override
        public String toString() {
            return "train_name='" + train_name + '\'' +
                    ", start_time='" + start_time + '\'' +
                    ", station='" + station_name + '\'' +
                    ", station_type='" + station_type + '\'' +
                    ", a_seats_avail=" + a_seats_available +
                    ", b_seats_avail=" + b_seats_available +
                    ", c_seats_avail=" + c_seats_available;
        }
    }

}
