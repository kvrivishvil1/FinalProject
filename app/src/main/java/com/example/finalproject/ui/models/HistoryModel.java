package com.example.finalproject.ui.models;

import java.io.Serializable;
import java.util.Date;

public class HistoryModel implements Serializable {

    public HistoryModel(long id, String name, Date lastMessageDate, long messageCount) {
        this.id = id;
        this.name = name;
        this.lastMessageDate = lastMessageDate;
        this.messageCount = messageCount;
    }

    public HistoryModel(String name) {
        this.name = name;
    }

    private long id;

    private String name;

    private Date lastMessageDate;

    private long messageCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public long getMessageCount() { return messageCount; }

    public void setMessageCount(long messageCount) { this.messageCount = messageCount; }
}
