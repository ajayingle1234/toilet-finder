package com.hardcastle.honeysuckervendor.Model;

/**
 * Created by abhijeet on 3/5/2018.
 */

public class AssignedRequestWithTime {

    private String vendorID;
    private String driverID;
    private String userID;
    private String serviceRequestID;
    private String assignedTime;

    public AssignedRequestWithTime() {}

    public AssignedRequestWithTime(String vendorID, String driverID, String userID, String serviceRequestID, String assignedTime) {
        this.vendorID = vendorID;
        this.driverID = driverID;
        this.userID = userID;
        this.serviceRequestID = serviceRequestID;
        this.assignedTime = assignedTime;
    }

    public String getVendorID() {
        return vendorID;
    }

    public void setVendorID(String vendorID) {
        this.vendorID = vendorID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getServiceRequestID() {
        return serviceRequestID;
    }

    public void setServiceRequestID(String serviceRequestID) {
        this.serviceRequestID = serviceRequestID;
    }

    public String getAssignedTime() {
        return assignedTime;
    }

    public void setAssignedTime(String assignedTime) {
        this.assignedTime = assignedTime;
    }
}
