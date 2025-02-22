package com.wxy.springbackend.model;

public class PathStation {
    private int pathid;
    private String start_time;
    private int station_id;
    private String station_name;
    private String station_type;
    private String train_name;
    private int  a_seats_available;
    private int  b_seats_available;
    private int  c_seats_available;


    public PathStation() {
    }

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
        return "PathStation{" +
                "pathid=" + pathid +
                ", start_time='" + start_time + '\'' +
                ", station_id=" + station_id +
                ", station_name='" + station_name + '\'' +
                ", station_type='" + station_type + '\'' +
                ", train_name='" + train_name + '\'' +
                ", a_seats_available=" + a_seats_available +
                ", b_seats_available=" + b_seats_available +
                ", c_seats_available=" + c_seats_available +
                '}';
    }
}
