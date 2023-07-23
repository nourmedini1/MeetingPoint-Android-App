package com.app.meetingpoint.models;

import java.io.Serializable;

public class Comment implements Serializable {
    private String userImage ;
    private String userName ;
    private String comment ;
    private String userId ;

    public Comment(){}

    public Comment(String userId ,String userImage, String userName, String comment) {
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
