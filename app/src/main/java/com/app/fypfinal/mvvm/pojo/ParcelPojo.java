package com.app.fypfinal.mvvm.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParcelPojo extends Super {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("qr_image")
    @Expose
    private Object qrImage;
    @SerializedName("tracking_id")
    @Expose
    private String trackingId;
    @SerializedName("postal_charges")
    @Expose
    private Integer postalCharges;
    @SerializedName("service_type")
    @Expose
    private String serviceType;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("dispatchLocation")
    @Expose
    private String dispatchLocation;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("postman")
    @Expose
    private Integer postman;
    @SerializedName("sender")
    @Expose
    private Integer sender;
    @SerializedName("receiver")
    @Expose
    private Integer receiver;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getQrImage() {
        return qrImage;
    }

    public void setQrImage(Object qrImage) {
        this.qrImage = qrImage;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public Integer getPostalCharges() {
        return postalCharges;
    }

    public void setPostalCharges(Integer postalCharges) {
        this.postalCharges = postalCharges;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDispatchLocation() {
        return dispatchLocation;
    }

    public void setDispatchLocation(String dispatchLocation) {
        this.dispatchLocation = dispatchLocation;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Integer getPostman() {
        return postman;
    }

    public void setPostman(Integer postman) {
        this.postman = postman;
    }

    public Integer getSender() {
        return sender;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }

    public Integer getReceiver() {
        return receiver;
    }

    public void setReceiver(Integer receiver) {
        this.receiver = receiver;
    }

}

