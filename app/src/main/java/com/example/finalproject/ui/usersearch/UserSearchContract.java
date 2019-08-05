package com.example.finalproject.ui.usersearch;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import com.example.finalproject.ui.models.HistoryModel;
import com.example.finalproject.ui.models.MessageModel;

import java.util.List;

public interface UserSearchContract {
    interface View {
        void showData(List<HistoryModel> list);
        void showProgressBar(String text);
        void hideProgressBar();
        void chatClicked(HistoryModel model);
        void changeStatus(String status);
    }

    interface Presenter {
        void searchUsers();
        HistoryModel addUser(HistoryModel model);
        WifiP2pManager.PeerListListener getPeerListListener();
        void registerReceiver();
        void unregisterReceiver();
        WifiP2pManager.ConnectionInfoListener getConnectionInfoListener();
        void chatClicked(HistoryModel model);
        void stopDiscovery();
        void setupDiscover();
        void onResume();
        void onPause();
        void onStop();
        void onDestroy();
        void setConnectedDevice(WifiP2pDevice device);
    }
}
