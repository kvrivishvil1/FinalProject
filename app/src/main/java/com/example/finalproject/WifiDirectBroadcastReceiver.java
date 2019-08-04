package com.example.finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import com.example.finalproject.ui.MainActivity;
import com.example.finalproject.ui.usersearch.UserSearchContract;

import java.util.ArrayList;
import java.util.Collection;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private UserSearchContract.Presenter presenter;



    public WifiDirectBroadcastReceiver(WifiP2pManager m, WifiP2pManager.Channel c, UserSearchContract.Presenter p) {
        manager = m;
        channel = c;
        presenter = p;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
//                Toast.makeText(context, "Wifi is ON", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(context, "Wifi is OFF", Toast.LENGTH_SHORT).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (manager != null) {
                manager.requestPeers(channel, presenter.getPeerListListener());
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) return;

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            WifiP2pGroup group = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);

            if (networkInfo.isConnected() && group != null) {
                if (group.isGroupOwner()) {
                    WifiP2pDevice dev = new ArrayList<>(group.getClientList()).get(0);
                    presenter.setConnectedDevice(dev);
                } else {
                    presenter.setConnectedDevice(group.getOwner());
                }
                manager.requestConnectionInfo(channel, presenter.getConnectionInfoListener());
            } else {
                presenter.setConnectedDevice(null);
            }
            presenter.setConnected(networkInfo.isConnected());
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, 10000);
            if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
                Toast.makeText(context, "---------Discovery started", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "+++++++++Discovery stopped", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
