package com.wxy.springbackend.model;

public class OrderProcess {
    private int ticketId;
    private int invoiceId;
    private String paymentState;
    private String validState;

    public OrderProcess() {
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public String getValidState() {
        return validState;
    }

    public void setValidState(String validState) {
        this.validState = validState;
    }

    @Override
    public String toString() {
        return "OrderProcess{" +
                "ticketId=" + ticketId +
                ", invoiceId=" + invoiceId +
                ", paymentState='" + paymentState + '\'' +
                ", validState='" + validState + '\'' +
                '}';
    }
}
