package com.example.finalproject.ui.models;

import android.net.wifi.p2p.WifiP2pDevice;

import java.io.Serializable;
import java.util.Date;

public class HistoryModel implements Serializable {

    public HistoryModel(long id, String name, Date lastMessageDate, long messageCount, WifiP2pDevice device) {
        this.id = id;
        this.name = name;
        this.lastMessageDate = lastMessageDate;
        this.messageCount = messageCount;
        this.device = device;
    }

    public HistoryModel(long id, String name, Date lastMessageDate, long messageCount) {
        this.id = id;
        this.name = name;
        this.lastMessageDate = lastMessageDate;
        this.messageCount = messageCount;
    }

    public HistoryModel(String name) {
        this.name = name;
    }

    public HistoryModel(String name, WifiP2pDevice device) {
        this.name = name;
        this.device = device;
    }

    private long id;

    private String name;

    private Date lastMessageDate;

    private long messageCount;

    private WifiP2pDevice device;

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

    public WifiP2pDevice getDevice() {
        return device;
    }

    public void setDevice(WifiP2pDevice device) {
        this.device = device;
    }
}
