package com.app.meetingpoint.models;

import java.io.Serializable;

public class DrawerGroup implements Serializable {
    private String encodedImage ;
    private String groupeName ;

    public DrawerGroup(String encodedImage, String groupeName) {
        this.encodedImage = encodedImage;
        this.groupeName = groupeName;
    }

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public String getGroupeName() {
        return groupeName;
    }

    public void setGroupeName(String groupeName) {
        this.groupeName = groupeName;
    }
}
