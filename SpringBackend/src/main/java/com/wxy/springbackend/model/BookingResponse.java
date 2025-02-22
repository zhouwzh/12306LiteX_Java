package com.wxy.springbackend.model;

public class BookingResponse {
    private int invoiceId;
    private int ticketId;
    private String validState;
    private String paymentState;

    public BookingResponse(int invoiceId, int ticketId) {
        this.invoiceId = invoiceId;
        this.ticketId = ticketId;
        this.validState = "pending";
        this.paymentState = "false";
    }

    // Getters and setters (if needed)
    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
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
