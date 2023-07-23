package com.app.meetingpoint.models;

import java.io.Serializable;

public class Topic implements Serializable {
    private String topicId;
    private String topic;


    public Topic( String topic) {
        this.topic = topic;
    }
    public Topic(){}

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
