package com.app.meetingpoint.models;

import com.airbnb.lottie.L;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String userId ;
    private String username ;
    private String email ;
    private String password ;
    private String image ;
    private String fcmToken ;
    private ArrayList<String> favoriteTopics ;
    private ArrayList<String> groups ;
    private boolean available ;
    private int rating ;
    private String work ;
    private String education ;
    private String address ;
    private String backImage;

    public User(){}


    public User(String username, String email, String password,
                String image, int rating,ArrayList<String> favoriteTopics ,
                ArrayList<String> groups,String address , String education,
                String work,String backImage) {

        this.username = username;
        this.email = email;
        this.password = password;
        this.image = image;
        this.rating = rating;
        this.groups = groups ;
        this.favoriteTopics = favoriteTopics ;
        this.address =address;
        this.backImage = backImage ;
        this.education = education ;
        this.work = work ;
    }
    public User(String username, String email, String password, String encodedImage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.image = encodedImage;
        favoriteTopics = null ;
        groups = null ;
        rating = 0 ;
    }

    public void setFavoriteTopics(ArrayList<String> favoriteTopics) {
        this.favoriteTopics = favoriteTopics;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public ArrayList<String> getFavoriteTopics() {
        return favoriteTopics;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBackImage() {
        return backImage;
    }

    public void setBackImage(String backImage) {
        this.backImage = backImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String encodedImage) {
        this.image = encodedImage;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


}
