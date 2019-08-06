package com.example.finalproject.ui.usersearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.finalproject.Helper;
import com.example.finalproject.R;
import com.example.finalproject.WifiDirectBroadcastReceiver;
import com.example.finalproject.data.Database;
import com.example.finalproject.data.models.HistoryModelEntity;
import com.example.finalproject.ui.MainActivity;
import com.example.finalproject.ui.SocketHandler;
import com.example.finalproject.ui.models.HistoryModel;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserSearchPresenter implements UserSearchContract.Presenter {

    private UserSearchContract.View view;
    private Context context;

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;

    enum Status {
        NOT_CONNECTED,
        CONNECTING,
        CONNECTED
    }

    private Status status = Status.NOT_CONNECTED;

    BroadcastReceiver receiver;
    IntentFilter intentFilter;

    List<WifiP2pDevice> peers = new ArrayList<>();

    HistoryModel clickedModel;

    WifiP2pDevice connectedDevice;

    boolean reconnecting;

    public UserSearchPresenter(UserSearchContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.clickedModel = null;
        this.connectedDevice = null;
        this.reconnecting = false;

        setupBroadcastReceiver();
        disconnect();
    }

    private void setupBroadcastReceiver() {
        wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(context.getApplicationContext(), context.getMainLooper(), null);

        receiver = new WifiDirectBroadcastReceiver(wifiP2pManager, channel, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
    }

    private WifiP2pManager.ActionListener discoverListener = new WifiP2pManager.ActionListener() {

        @Override
        public void onSuccess() {
//                Toast.makeText(context, "Discovering peers...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(int reason) {
//            Toast.makeText(context, "Discovering peers failed. reason: " + reason, Toast.LENGTH_SHORT).show();
        }
    };

    private WifiP2pManager.ActionListener stopDiscoverListener = new WifiP2pManager.ActionListener() {

        @Override
        public void onSuccess() {
//                Toast.makeText(context, "Peers discovery stopped", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(int reason) {
//            Toast.makeText(context, "Peers discovery stop failed. reason: " + reason, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void setupDiscover() {
        wifiP2pManager.discoverPeers(channel, discoverListener);
    }

    @Override
    public void setDiscovery(boolean on) {
        view.setDiscovery(on);
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            List<HistoryModel> result = new ArrayList<>();

            if (status != Status.CONNECTING)
                view.hideProgressBar();

            if (!wifiP2pDeviceList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(wifiP2pDeviceList.getDeviceList());

                for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                    result.add(new HistoryModel(device.deviceName, device));
                }
            }

            if (peers.size() == 0) {
//                Toast.makeText(context.getApplicationContext(), "No devices found", Toast.LENGTH_SHORT).show();
            }

//            Toast.makeText(context.getApplicationContext(), peers.size() + " Devices", Toast.LENGTH_SHORT).show();

            view.showData(result);
        }
    };

    @Override
    public void stopDiscovery() {
        peers = new ArrayList<>();
        view.showData(new ArrayList<HistoryModel>());
        wifiP2pManager.stopPeerDiscovery(channel, stopDiscoverListener);
    }

    private void cancelConnect() {
        wifiP2pManager.cancelConnect(channel, cancelListener);
    }

    @Override
    public void onResume() {
        registerReceiver();
        view.showProgressBar(FindUserFragment.SEARCHING_DEVICES);
        setupDiscover();
    }

    @Override
    public void onPause() {
        unregisterReceiver();
        stopDiscovery();
    }

    @Override
    public void onStop() {
        int x = 5;
    }


    @Override
    public void onDestroy() {
        unregisterReceiver();
        stopDiscovery();

        disconnect();
    }

    private void disconnect() {
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("main", "removeGroup onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("main", "removeGroup onFailure -" + reason);
            }
        });

        cancelConnect();

        deleteGroups();

        SocketHandler.closeSocket();
    }

    private void deleteGroups() {
        Method deletePersistentGroupMethod = null;
        try {
            deletePersistentGroupMethod = WifiP2pManager.class.getMethod("deletePersistentGroup", WifiP2pManager.Channel.class, int.class, WifiP2pManager.ActionListener.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for (int netid = 0; netid < 32; netid++) {
            try {
                deletePersistentGroupMethod.invoke(wifiP2pManager, this.channel, netid, null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public WifiP2pManager.PeerListListener getPeerListListener() {
        return peerListListener;
    }

    @Override
    public void registerReceiver() {
        receiver = new WifiDirectBroadcastReceiver(wifiP2pManager, channel, this);
        context.registerReceiver(receiver, intentFilter);
    }

    @Override
    public void unregisterReceiver() {
        context.unregisterReceiver(receiver);
        receiver = null;
    }

    @Override
    public HistoryModel addUser(HistoryModel model) {
        model.setLastMessageDate(new Date());
        long id = Database.getInstance().dataDao().insertHistory(FromModelToEntity(model));
        model.setId(id);

        return model;
    }

    @Override
    public void setConnectedDevice(WifiP2pDevice device) {
        connectedDevice = device;
    }

    private WifiP2pManager.ActionListener connectListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
//            Toast.makeText(context.getApplicationContext(), "Connected to " + clickedModel.getDevice().deviceName, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(int reason) {
            String deviceName = "device";
            if (clickedModel != null)
                deviceName = clickedModel.getDevice().deviceName;
//            Toast.makeText(context, "Could not connect " + deviceName + " reason: " + reason, Toast.LENGTH_SHORT).show();
        }
    };

    private WifiP2pManager.ActionListener cancelListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
//            Toast.makeText(context.getApplicationContext(), "Cancel connect successful", Toast.LENGTH_SHORT).show();
            if (reconnecting) {
                reconnecting = false;
                connectToDevice();
            }
        }

        @Override
        public void onFailure(int reason) {
//            Toast.makeText(context.getApplicationContext(), "Cancel connect failed", Toast.LENGTH_SHORT).show();

            if (reconnecting) {
                reconnecting = false;
                connectToDevice();
            }
        }
    };

    @Override
    public void chatClicked(HistoryModel model) {
        clickedModel = model;

        status = Status.CONNECTING;
        view.showProgressBar(FindUserFragment.CONNECTING_TO + " " + model.getDevice().deviceName + "-თან");

        reconnect();
    }

    private void reconnect() {
        this.reconnecting = true;
        // it will connect in success
        wifiP2pManager.cancelConnect(channel, cancelListener);
    }

    private void connectToDevice() {
        final WifiP2pDevice device = clickedModel.getDevice();
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        wifiP2pManager.connect(channel, config, connectListener);
    }

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                ((MainActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context.getApplicationContext(), "Server", Toast.LENGTH_SHORT).show();
                    }
                });

                (new Thread() {
                    @Override
                    public void run() {
                        try {
                            ServerSocket serverSocket = new ServerSocket(SocketHandler.getPort());
                            serverSocket.setReuseAddress(true);
                            Socket socket = serverSocket.accept();
                            socket.setReuseAddress(true);
                            SocketHandler.setSocket(socket);

                            status = Status.CONNECTED;

                            gotoChat(new HistoryModel(connectedDevice.deviceName, connectedDevice));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            } else if (wifiP2pInfo.groupFormed) {
                ((MainActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context.getApplicationContext(), "Client", Toast.LENGTH_SHORT).show();
                    }
                });
                (new Thread() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket();
                            socket.setReuseAddress(true);
                            SocketHandler.setSocket(socket);

                            Date startDate = new Date();
                            while (true) {
                                try {
                                    socket.connect(new InetSocketAddress(groupOwnerAddress, SocketHandler.getPort()), 10000);
                                    status = Status.CONNECTED;

                                    gotoChat(new HistoryModel(connectedDevice.deviceName, connectedDevice));
                                    break;
                                } catch (Exception e) {
                                    Thread.sleep(1000);
                                    Date currDate = new Date();
                                    if (currDate.getTime() - startDate.getTime() > 30000)
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    };

    private void gotoChat(HistoryModel model) {
        model = addUser(model);

        Bundle args = new Bundle();
        args.putSerializable("NewHistoryModel", model);

        NavController navController = Navigation.findNavController((MainActivity) context, R.id.main_fragment);
        navController.navigate(R.id.action_findUserFragment_to_messageFragment, args);
    }

    @Override
    public WifiP2pManager.ConnectionInfoListener getConnectionInfoListener() {
        return this.connectionInfoListener;
    }

    private HistoryModelEntity FromModelToEntity(HistoryModel model) {
        return new HistoryModelEntity(model.getName(), Helper.fromDate(model.getLastMessageDate()), model.getMessageCount());
    }

}
