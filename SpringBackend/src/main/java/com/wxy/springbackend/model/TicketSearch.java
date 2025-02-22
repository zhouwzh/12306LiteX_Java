package com.wxy.springbackend.model;

import java.time.LocalDateTime;

public class TicketSearch {
    private int ticketId;
    private double price;
    private String departStationName;
    private String arrivalStationName;
    private String seatLevel;
    private String departureTime;
    private String arrivalTime;
    private String trainName;
    private int invoiceId;
    private String PaymentState;
    private String ValidState;

    public TicketSearch() {
    }

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

    public String getPaymentState() {
        return PaymentState;
    }

    public void setPaymentState(String paymentState) {
        PaymentState = paymentState;
    }

    public String getValidState() {
        return ValidState;
    }

    public void setValidState(String validState) {
        ValidState = validState;
    }
}
