package com.example.finalproject.data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.finalproject.Helper;

import java.util.Date;

@Entity(tableName = "history")
public class HistoryModelEntity {

    public HistoryModelEntity(String name, long lastMessageDate, long messageCount) {
        this.name = name;
        this.lastMessageDate = lastMessageDate;
        this.messageCount = messageCount;
    }

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "lastMessageDate")
    private long lastMessageDate;

    @ColumnInfo(name = "messageCount")
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

    public long getLastMessageDate() {
        return this.lastMessageDate;
    }

    public void setLastMessageDate(long lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public Date getLastMessageDateAsDate() {
        return Helper.toDate(this.lastMessageDate);
    }

    public void setLastMessageDateAsDate(Date lastMessageDate) {
        this.lastMessageDate = Helper.fromDate(lastMessageDate);
    }

    public long getMessageCount() { return messageCount; }

    public void setMessageCount(long messageCount) { this.messageCount = messageCount; }
}