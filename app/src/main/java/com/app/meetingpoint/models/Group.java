package com.app.meetingpoint.models;

import java.io.Serializable;

public class Group implements Serializable {
    private String id;
    private String groupName ;
    private String groupImage ;
    private String backImage ;
    private int numberOfUsers ;
    private int groupRating ;
    private String topic ;
    private String adminId ;
    private String description ;
    private String conversationId;

    public Group(){}


    public Group(String conversationId,String backImage,String groupName, String encodedImage, int numberOfUsers, int rating,String description,String adminId) {
        this.groupName = groupName;
        this.groupImage = encodedImage;
        this.conversationId = conversationId ;
        this.backImage = backImage;
        this.numberOfUsers = numberOfUsers;
        this.groupRating = rating;
        this.description = description ;
        this.adminId = adminId ;
    }
    public Group(String groupName, String encodedImage, int numberOfUsers) {
        this.groupName = groupName;
        this.groupImage = encodedImage;
        this.numberOfUsers = numberOfUsers;
        groupRating = 0 ;
        description = null;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getBackImage() {
        return backImage;
    }

    public void setBackImage(String backImage) {
        this.backImage = backImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }



    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }



    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public int getGroupRating() {
        return groupRating;
    }

    public void setGroupRating(int groupRating) {
        this.groupRating = groupRating;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}
