package com.app.meetingpoint.models;

import java.io.Serializable;

public class React implements Serializable {
    private String reactorId ;
    private String reactorImage ;
    private String reactorUserName ;

    public React(){}

    public React(String reactorId, String reactorImage, String reactorUserName) {
        this.reactorId = reactorId;
        this.reactorImage = reactorImage;
        this.reactorUserName = reactorUserName;
    }

    public String getReactorId() {
        return reactorId;
    }

    public void setReactorId(String reactorId) {
        this.reactorId = reactorId;
    }

    public String getReactorImage() {
        return reactorImage;
    }

    public void setReactorImage(String reactorImage) {
        this.reactorImage = reactorImage;
    }

    public String getReactorUserName() {
        return reactorUserName;
    }

    public void setReactorUserName(String reactorUserName) {
        this.reactorUserName = reactorUserName;
    }
}
