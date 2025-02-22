package com.wxy.javafxfrontend;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SearchController {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML private Button backButton;
    @FXML private Label routeLabel;
    @FXML private Label dateLabel;
    @FXML private Button prevDayButton;
    @FXML private Button nextDayButton;
    @FXML private VBox ticketContainer;

    private int userId;
    private String username;
    private String departStation;
    private String arrivalStation;
    private String selectedDate;

    private List<TripSearch> ticketData;

    public void initialize() {}

    public void setData(int userId, String username, String departStation, String arrivalStation, String date) throws IOException, InterruptedException {
        this.userId = userId;
        this.username = username;
        this.departStation = departStation;
        this.arrivalStation = arrivalStation;
        this.selectedDate = date;
        routeLabel.setText(departStation + " → " + arrivalStation);
        dateLabel.setText(date);

        String requestBody = "depart_station=" + departStation + "&arrival_station=" + arrivalStation + "&datetime=" + selectedDate;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8088/api/search"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, TripSearch.class);
        ticketData = mapper.readValue(response.body(), listType);
        renderTickets();
    }

    @FXML
    private void handlePrevDay() throws IOException, InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate dateTime = LocalDate.parse(this.selectedDate, formatter).minusDays(1);

        String requestBody = "depart_station=" + departStation + "&arrival_station=" + arrivalStation + "&datetime=" + dateTime.toString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8088/api/search"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, TripSearch.class);
        ticketData = mapper.readValue(response.body(), listType);
        renderTickets();

        this.selectedDate = dateTime.toString();
        dateLabel.setText(this.selectedDate);
    }

    @FXML
    private void handleNextDay() throws IOException, InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate dateTime = LocalDate.parse(this.selectedDate, formatter).plusDays(1);

        String requestBody = "depart_station=" + departStation + "&arrival_station=" + arrivalStation + "&datetime=" + dateTime.toString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8088/api/search"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, TripSearch.class);
        ticketData = mapper.readValue(response.body(), listType);

        renderTickets();

        this.selectedDate = dateTime.toString();
        dateLabel.setText(this.selectedDate);
    }

    private void renderTickets() {
        ticketContainer.getChildren().clear();
        if (ticketData != null && !ticketData.isEmpty()) {
            for (TripSearch item : ticketData) {
                VBox ticketItem = createTicketItem(item);
                ticketContainer.getChildren().add(ticketItem);
            }
        }
    }


    private VBox createTicketItem(TripSearch item) {
        VBox ticketBox = new VBox();
        ticketBox.getStyleClass().add("ticket-item");
        ticketBox.setSpacing(10);

        String depart = item.getStations().getFirst();
        String arrival = item.getStations().getLast();
        List<String> stations = item.getStations();
        List<String> arrivalTimeList = item.getArrivalTimeList();
        String departTime = arrivalTimeList.getFirst();
        String arriveTime = arrivalTimeList.getLast();
        int stopCount = stations.size() - 2;
        Label headerLabel = new Label(depart + " → " + arrival + " on Train: " + item.getTrain_name());
        headerLabel.getStyleClass().add("ticket-header");

        Label timeInfo = new Label("Departure Time: " + departTime + "   Arrival Time: " + arriveTime + "   Stop Station Number: " + stopCount);
        timeInfo.getStyleClass().add("ticket-subinfo");

        StringBuilder path = new StringBuilder();
        for (String station : stations) {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDateTime trainTime = LocalDateTime.parse(arrivalTimeList.get(stations.indexOf(station)), inputFormatter);
            String timeString = trainTime.format(outputFormatter);
            path.append(station).append(" ").append(timeString).append(" → ");
        }
        path = new StringBuilder(path.substring(0, path.length() - 3));
        Label stopInfo = new Label(path.toString());
        stopInfo.getStyleClass().add("ticket-stopinfo");

        Label expandButton = new Label("Check Seat Occupancy");
        expandButton.getStyleClass().add("ticket-expand-button");

        VBox seatInfoPanel = createSeatInfoPanel(item);
        seatInfoPanel.setVisible(false);
        seatInfoPanel.setManaged(false);

        expandButton.setOnMouseClicked(e -> {
            boolean currentlyVisible = seatInfoPanel.isVisible();
            seatInfoPanel.setVisible(!currentlyVisible);
            seatInfoPanel.setManaged(!currentlyVisible);
            expandButton.setText(currentlyVisible ? "Unfold to Check Seat Occupancy" : "Fold to Hide Seat Occupancy");
        });

        ticketBox.getChildren().addAll(headerLabel, timeInfo, stopInfo, expandButton, seatInfoPanel);

        return ticketBox;
    }

    private VBox createSeatInfoPanel(TripSearch item) {
        VBox seatPanel = new VBox(10);
        seatPanel.getStyleClass().add("seat-info-panel");

        double priceA = item.getPrices_A();
        double priceB = item.getPrices_B();
        double priceC = item.getPrices_C();
        int aLeft = item.getaSeatsLeft();
        int bLeft = item.getbSeatsLeft();
        int cLeft = item.getcSeatsLeft();

        HBox aRow = createSeatRow("A", aLeft, priceA, item);
        HBox bRow = createSeatRow("B", bLeft, priceB, item);
        HBox cRow = createSeatRow("C", cLeft, priceC, item);

        seatPanel.getChildren().addAll(aRow, bRow, cRow);

        return seatPanel;
    }

    private HBox createSeatRow(String seatType, int left, double price, TripSearch item) {
        HBox row = new HBox(10);
        row.getStyleClass().add("seat-type-row");

        Label seatTypeLabel = new Label("Seat Type: " + seatType);
        seatTypeLabel.getStyleClass().add("seat-type-label");

        DecimalFormat df = new DecimalFormat("#.00");
        String formattedValue = df.format(price);
        Label priceLabel = new Label("Price: " + formattedValue);
        priceLabel.getStyleClass().add("seat-available-label");

        Label availableLabel = new Label("Tickets Left: " + left);
        availableLabel.getStyleClass().add("seat-available-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(seatTypeLabel, priceLabel, availableLabel, spacer);

        if (left > 0) {
            Button bookBtn = new Button("Book Now");
            bookBtn.getStyleClass().add("book-button");
            bookBtn.setOnAction(e -> {
                try {
                    handleBookTicket(e, seatType, price, item);
                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            });

            row.getChildren().add(bookBtn);
        }

        return row;
    }

    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
        HomeController controller = loader.getController();
        controller.setParameters(userId, username);

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }



    private void handleBookTicket(ActionEvent event, String seatType, double price, TripSearch item) throws IOException, InterruptedException {
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setPathId(item.getPathId());
        booking.setDepartureStationName(item.getStations().getFirst());
        booking.setArrivalStationName(item.getStations().getLast());
        booking.setDepartureTime(item.getArrivalTimeList().getFirst());
        booking.setArrivalTime(item.getArrivalTimeList().getLast());
        booking.setSeatLevel(seatType);
        booking.setPrice(BigDecimal.valueOf(price));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(booking);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8088/api/booking"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode responseJson = objectMapper.readTree(response.body());
        String success = responseJson.get("Status").asText();

        if ("Success".equals(success)) {
            OrderController.OrderItem orderItem = new OrderController.OrderItem();
            orderItem.setTicketId(responseJson.get("ticketId").asInt());
            orderItem.setDepartStationName(item.getStations().getFirst());
            orderItem.setArrivalStationName(item.getStations().getLast());
            orderItem.setDepartureTime(item.getArrivalTimeList().getFirst());
            orderItem.setArrivalTime(item.getArrivalTimeList().getLast());
            orderItem.setSeatLevel(seatType);
            orderItem.setPrice(price);
            orderItem.setTrainName(item.getTrain_name());
            orderItem.setInvoiceId(responseJson.get("invoiceId").asInt());
            orderItem.setValidState(responseJson.get("validState").asText());
            orderItem.setPaymentState(responseJson.get("paymentState").asText());



            FXMLLoader loader = new FXMLLoader(getClass().getResource("booksuccess.fxml"));
            Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
            BooksuccessController controller = loader.getController();
            controller.setParameters(true, userId, username, orderItem, "Booking Successful! Redirecting to Payment Page...");

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else {
            OrderController.OrderItem orderItem = new OrderController.OrderItem();
            String message = "Booking failed. Error: " + success + "Redirecting to home...";
            FXMLLoader loader = new FXMLLoader(getClass().getResource("booksuccess.fxml"));
            Scene scene = new Scene(loader.load(), Settings.get_x(), Settings.get_y());
            BooksuccessController controller = loader.getController();
            controller.setParameters(false, userId, username, orderItem, message);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }

    }

    public static class TripSearch {
        private int pathId;
        private List<String> stations;
        private List<String> arrivalTimeList;
        private double prices_A;
        private double prices_B;
        private double prices_C;
        private String departStationId;
        private String arrivalStationId;
        private int aSeatsLeft;
        private int bSeatsLeft;
        private int cSeatsLeft;
        private String train_name;

        public String getTrain_name() {
            return train_name;
        }

        public void setTrain_name(String train_name) {
            this.train_name = train_name;
        }

        public int getPathId() {
            return pathId;
        }

        public void setPathId(int pathId) {
            this.pathId = pathId;
        }

        public List<String> getStations() {
            return stations;
        }

        public void setStations(List<String> stations) {
            this.stations = stations;
        }

        public List<String> getArrivalTimeList() {
            return arrivalTimeList;
        }

        public void setArrivalTimeList(List<String> arrivalTimeList) {
            this.arrivalTimeList = arrivalTimeList;
        }

        public double getPrices_A() {
            return prices_A;
        }

        public void setPrices_A(double prices_A) {
            this.prices_A = prices_A;
        }

        public double getPrices_B() {
            return prices_B;
        }

        public void setPrices_B(double prices_B) {
            this.prices_B = prices_B;
        }

        public double getPrices_C() {
            return prices_C;
        }

        public void setPrices_C(double prices_C) {
            this.prices_C = prices_C;
        }

        public String getDepartStationId() {
            return departStationId;
        }

        public void setDepartStationId(String departStationId) {
            this.departStationId = departStationId;
        }

        public String getArrivalStationId() {
            return arrivalStationId;
        }

        public void setArrivalStationId(String arrivalStationId) {
            this.arrivalStationId = arrivalStationId;
        }

        public int getaSeatsLeft() {
            return aSeatsLeft;
        }

        public void setaSeatsLeft(int aSeatsLeft) {
            this.aSeatsLeft = aSeatsLeft;
        }

        public int getbSeatsLeft() {
            return bSeatsLeft;
        }

        public void setbSeatsLeft(int bSeatsLeft) {
            this.bSeatsLeft = bSeatsLeft;
        }

        public int getcSeatsLeft() {
            return cSeatsLeft;
        }

        public void setcSeatsLeft(int cSeatsLeft) {
            this.cSeatsLeft = cSeatsLeft;
        }
    }

    public static class Booking {
        private int userId;
        private int pathId;
        private String departureStationName;
        private String arrivalStationName;
        private String departureTime;
        private String arrivalTime;
        private String seatLevel;
        private BigDecimal price;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getPathId() {
            return pathId;
        }

        public void setPathId(int pathId) {
            this.pathId = pathId;
        }

        public String getDepartureStationName() {
            return departureStationName;
        }

        public void setDepartureStationName(String departureStationName) {
            this.departureStationName = departureStationName;
        }

        public String getArrivalStationName() {
            return arrivalStationName;
        }

        public void setArrivalStationName(String arrivalStationName) {
            this.arrivalStationName = arrivalStationName;
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

        public String getSeatLevel() {
            return seatLevel;
        }

        public void setSeatLevel(String seatLevel) {
            this.seatLevel = seatLevel;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

}
