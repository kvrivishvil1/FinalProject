package com.example.finalproject.ui.models;

import java.util.Date;

public class MessageModel {

    public MessageModel(long id, long userId, String text, boolean sent, Date date) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.sent = sent;
        this.date = date;
    }

    private long id;

    private long userId;

    private String text;

    private boolean sent;

    private Date date;

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }

    public void setUserId(long userId) { this.userId = userId; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public boolean isSent() { return sent; }

    public void setSent(boolean sent) { this.sent = sent; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }
}
