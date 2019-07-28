package com.example.finalproject.data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.finalproject.Helper;

import java.util.Date;

@Entity(tableName = "messages")
public class MessageModelEntity {

    public MessageModelEntity(long userId, String text, boolean sent, long messageDate) {
        this.userId = userId;
        this.text = text;
        this.sent = sent;
        this.messageDate = messageDate;
    }

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "userId")
    private long userId;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "sent")
    private boolean sent;

    @ColumnInfo(name = "date")
    private long messageDate;

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }

    public void setUserId(long userId) { this.userId = userId; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public boolean isSent() { return sent; }

    public void setSent(boolean sent) { this.sent = sent; }

    public long getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(long messageDate) {
        this.messageDate = messageDate;
    }

    public Date getMessageDateAsDate() {
        return Helper.toDate(this.messageDate);
    }

    public void setMessageDateAsDate(Date Message) {
        this.messageDate = Helper.fromDate(Message);
    }
}
