package com.example.jananathbanuka.sliitshuttle;

public class Vehicle {
    String vehicleID,driverName,vehicleNumber,route;

    public Vehicle(){}

    public Vehicle(String vehicleID, String driverName, String vehicleNumber, String route) {
        this.vehicleID = vehicleID;
        this.driverName = driverName;
        this.vehicleNumber = vehicleNumber;
        this.route = route;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }


}
