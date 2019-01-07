package com.duallab.bustimetable;

import java.time.LocalTime;

public class BusService implements Comparable<BusService> {

    private String companyName;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    public BusService() {

    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public LocalTime getDepartureTime() {
        return this.departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return this.arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public int compareTo(BusService busServiceObject) {
        return this.departureTime.compareTo(busServiceObject.departureTime);
    }

    @Override
    public String toString() {
        return this.companyName + " " + this.departureTime + " " + this.arrivalTime;
    }
}
