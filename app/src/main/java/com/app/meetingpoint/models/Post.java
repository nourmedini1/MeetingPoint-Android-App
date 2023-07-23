package com.app.meetingpoint.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable {
    private String postId ;
    private String posterName ;
    private String postDate ;
    private String postVisibility ;
    private String caption;
    private String image ;
    private String posterImage;
    private String posterId ;
    private String groupId ;
    private String groupName ;
    private String shareGroup="" ;
    private String shareGroupName="" ;
    private String sharerName = "";
    private String sharerId = "";
    private int nbComments ;
    private int nbReacts ;
    private int nbShares;
    private ArrayList<String> comments;
    private ArrayList<String> reacts ;
    private ArrayList<String> shareTo ;
    public Post(){};

    public Post(String posterId, String groupName , String groupId, String posterImage, String posterName, String postDate, String postVisibility, String postCaption,
                String postImage, int nbComments, int nbReacts,
                int nbShares, ArrayList<String> comments, ArrayList<String> reacts, ArrayList<String> shareTo) {
        this.shareTo = shareTo ;
        this.posterId = posterId ;
        this.groupName = groupName;
        this.groupId = groupId;
        this.posterImage = posterImage ;
        this.posterName = posterName;
        this.postDate = postDate;
        this.postVisibility = postVisibility;
        this.caption = postCaption;
        this.image = postImage;
        this.nbComments = nbComments;
        this.nbReacts = nbReacts;
        this.nbShares = nbShares;
        this.comments = comments;
        this.reacts = reacts;
    }
    public Post(String posterId, String groupId, String posterImage, String posterName, String postDate, String postVisibility, String postCaption,
                String postImage, int nbComments, int nbReacts,
                int nbShares, ArrayList<String> comments, ArrayList<String> reacts, ArrayList<String> shareTo) {
        this.shareTo = shareTo ;
        this.posterId = posterId ;
        this.groupName="";
        this.groupId = groupId;
        this.posterImage = posterImage ;
        this.posterName = posterName;
        this.postDate = postDate;
        this.postVisibility = postVisibility;
        this.caption = postCaption;
        this.image = postImage;
        this.nbComments = nbComments;
        this.nbReacts = nbReacts;
        this.nbShares = nbShares;
        this.comments = comments;
        this.reacts = reacts;
    }

    public Post( String posterName,
                String postDate, String postVisibility
            , String postCaption, String postImage,
                String posterImage, String posterId,
                String groupId, String groupName,
                int nbComments, int nbReacts, int nbShares) {
        this.posterName = posterName;
        this.postDate = postDate;
        this.postVisibility = postVisibility;
        this.caption = postCaption;
        this.image = postImage;
        this.posterImage = posterImage;
        this.posterId = posterId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.nbComments = nbComments;
        this.nbReacts = nbReacts;
        this.nbShares = nbShares;
    }

    public String getShareGroupName() {
        return shareGroupName;
    }

    public void setShareGroupName(String shareGroupName) {
        this.shareGroupName = shareGroupName;
    }

    public String getShareGroup() {
        return shareGroup;
    }

    public String getSharerName() {
        return sharerName;
    }

    public String getSharerId() {
        return sharerId;
    }

    public void setSharerId(String sharerId) {
        this.sharerId = sharerId;
    }

    public void setSharerName(String sharerName) {
        this.sharerName = sharerName;
    }

    public void setShareGroup(String shareGroup) {
        this.shareGroup = shareGroup;
    }

    public ArrayList<String> getShareTo() {
        return shareTo;
    }

    public void setShareTo(ArrayList<String> shareTo) {
        this.shareTo = shareTo;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostVisibility() {
        return postVisibility;
    }

    public void setPostVisibility(String postVisibility) {
        this.postVisibility = postVisibility;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String postCaption) {
        this.caption = postCaption;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String postImage) {
        this.image = postImage;
    }

    public int getNbComments() {
        return nbComments;
    }

    public void setNbComments(int nbComments) {
        this.nbComments = nbComments;
    }

    public int getNbReacts() {
        return nbReacts;
    }

    public void setNbReacts(int nbReacts) {
        this.nbReacts = nbReacts;
    }

    public int getNbShares() {
        return nbShares;
    }

    public void setNbShares(int nbSHares) {
        this.nbShares = nbSHares;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public ArrayList<String> getReacts() {
        return reacts;
    }

    public void setReacts(ArrayList<String> reacts) {
        this.reacts = reacts;
    }
}
