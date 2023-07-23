package com.app.meetingpoint.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Conversation implements Serializable {
    private String conversationId ;
    private  String lastMessage ;
    private String lastMessageSender ;
    private String firstParty ="" ;
    private String secondParty="" ;
    private String group="" ;
    private String date="";
    private Date timestamp ;
    private boolean seen =false;

    public Conversation(Date timestamp ,
                        String lastMessage, String lastMessageSender, String firstParty,
                        String secondParty, String group, boolean seen) {

        this.timestamp = timestamp ;
        date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(this.timestamp);
        this.lastMessage = lastMessage;
        this.lastMessageSender = lastMessageSender;
        this.firstParty = firstParty;
        this.secondParty = secondParty;
        this.group = group;
        this.seen = seen;
    }

    public Conversation() {
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        this.date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(timestamp);
    }

    public String getDate() {
        return date;
    }



    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageSender() {
        return lastMessageSender;
    }

    public void setLastMessageSender(String lastMessageSender) {
        this.lastMessageSender = lastMessageSender;
    }

    public String getFirstParty() {
        return firstParty;
    }

    public void setFirstParty(String firstParty) {
        this.firstParty = firstParty;
    }

    public String getSecondParty() {
        return secondParty;
    }

    public void setSecondParty(String secondParty) {
        this.secondParty = secondParty;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
