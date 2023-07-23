package com.app.meetingpoint.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatMessage implements Serializable {
    private String messageId;
    private String senderId;
    private String conversationId;
    private String messageBody;
    private String image="";
    private Date timestamp ;
    private String date ;
    private String senderName;

    public ChatMessage() {
    }

    public ChatMessage(String senderName ,Date timestamp , String senderId, String conversationId, String messageBody, String image) {
        this.timestamp = timestamp ;
        date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(this.timestamp);
        this.senderId = senderId;
        this.conversationId = conversationId;
        this.messageBody = messageBody;
        this.senderName = senderName;
        this.image = image;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        this.date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(timestamp);
    }
    public String getDate(){return date;}

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



}
