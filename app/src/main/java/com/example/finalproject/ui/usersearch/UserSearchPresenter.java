package com.example.finalproject.ui.usersearch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.finalproject.Helper;
import com.example.finalproject.R;
import com.example.finalproject.WifiDirectBroadcastReceiver;
import com.example.finalproject.data.Database;
import com.example.finalproject.data.models.HistoryModelEntity;
import com.example.finalproject.data.models.MessageModelEntity;
import com.example.finalproject.ui.MainActivity;
import com.example.finalproject.ui.messages.MessageContract;
import com.example.finalproject.ui.messages.MessageFragment;
import com.example.finalproject.ui.models.HistoryModel;
import com.example.finalproject.ui.models.MessageModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    BroadcastReceiver receiver;
    IntentFilter intentFilter;

    List<WifiP2pDevice> peers = new ArrayList<>();

    HistoryModel clickedModel;

    public UserSearchPresenter(UserSearchContract.View view, Context context) {
        this.view = view;
        this.context = context;

        setupBroadcastReceiver();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void searchUsers() {
        setupDiscover();
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
    }

    private void setupDiscover() {
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
//                Toast.makeText(context, "Discovering peers...", Toast.LENGTH_SHORT).show();
                view.showProgressBar();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(context, "Discovering peers failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            view.hideProgressBar();

            List<HistoryModel> result = new ArrayList<>();

            if (!wifiP2pDeviceList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(wifiP2pDeviceList.getDeviceList());

                for (WifiP2pDevice device: wifiP2pDeviceList.getDeviceList()) {
                    result.add(new HistoryModel(device.deviceName, device));
                }
            }

            if (peers.size() == 0) {
                Toast.makeText(context.getApplicationContext(), "No devices found", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(context.getApplicationContext(), peers.size() + " Devices", Toast.LENGTH_SHORT).show();

            view.showData(result);
        }
    };

    @Override
    public WifiP2pManager.PeerListListener getPeerListListener() {
        return peerListListener;
    }

    @Override
    public void registerReceiver() {
        receiver = new WifiDirectBroadcastReceiver(wifiP2pManager, channel, this);
        context.registerReceiver(receiver, intentFilter);
//        Toast.makeText(context, "Receiver registered", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void unregisterReceiver() {
        context.unregisterReceiver(receiver);
//        Toast.makeText(context, "Receiver unregistered", Toast.LENGTH_SHORT).show();
    }

    @Override
    public HistoryModel addUser(HistoryModel model) {
        model.setLastMessageDate(new Date());
        long id = Database.getInstance().dataDao().insertHistory(FromModelToEntity(model));
        model.setId(id);

        return model;
    }

    @Override
    public void chatClicked(HistoryModel model) {
        clickedModel = model;

        final WifiP2pDevice device = model.getDevice();
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(context.getApplicationContext(), "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();

                addUser(clickedModel);

                Bundle args = new Bundle();
                args.putSerializable("NewHistoryModel", clickedModel);

                NavController navController = Navigation.findNavController((MainActivity)context, R.id.main_fragment);
                navController.navigate(R.id.action_findUserFragment_to_messageFragment, args);

            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(context, "Could not connect " + device.deviceName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;

            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                // Host
            } else if (wifiP2pInfo.groupFormed) {
                // Client
            }
        }
    };

    @Override
    public WifiP2pManager.ConnectionInfoListener getConnectionInfoListener() {
        return this.connectionInfoListener;
    }

    private HistoryModelEntity FromModelToEntity(HistoryModel model) {
        return new HistoryModelEntity(model.getName(), Helper.fromDate(model.getLastMessageDate()), model.getMessageCount());
    }

    static final int MESSAGE_READ = 1;


    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) message.obj;
                    String tempMsg = new String(readBuff, 0, message.arg1);
                    // msg in tempmsg
                    break;
            }
            return true;
        }
    });

    private class SendReceive extends Thread {
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket sock) {
            socket = sock;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (socket != null) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread {
        Socket socket;
        String hostAdd;

        public ClientClass(InetAddress hostAddress) {
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
