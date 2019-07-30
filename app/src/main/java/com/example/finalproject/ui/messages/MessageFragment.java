package com.example.finalproject.ui.messages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Helper;
import com.example.finalproject.R;
import com.example.finalproject.ui.MainActivity;
import com.example.finalproject.ui.models.HistoryModel;
import com.example.finalproject.ui.models.MessageModel;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class MessageFragment extends Fragment implements MessageContract.View {

    private MessageRecycleViewAdapter adapter;
    private RecyclerView recyclerView;
    private MessagePresenter presenter;

    private HistoryModel model;
    private boolean hideInput;

    private EditText messageText;
    private ImageView sendButton;
    private ProgressBar progressBar;

    static final int MESSAGE_READ = 1;

    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;


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

    public MessageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.message_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels - (int) (displayMetrics.widthPixels * 0.2);

        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);

        ((MainActivity)getActivity()).disableToggle();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getArguments();
        if(bundle == null) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.main_fragment);
            navController.navigate(R.id.action_messageFragment_to_historyFragment, null);
        }
        progressBar = view.findViewById(R.id.progress_bar);
        messageText = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.message_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageText.getText().toString();
                if (!msg.isEmpty()) return;
                sendReceive.write(msg.getBytes());
            }
        });

        model = (HistoryModel) bundle.getSerializable("HistoryModel");
        if(model == null) {
            model = (HistoryModel) bundle.getSerializable("NewHistoryModel");
            hideInput = false;
        } else {
            hideInput = true;
            messageText.setVisibility(View.GONE);
            sendButton.setVisibility(View.GONE);
        }


        this.recyclerView = view.findViewById(R.id.message_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.adapter = new MessageRecycleViewAdapter(this, width);
        recyclerView.setAdapter(adapter);

        ImageView deleteButton = getActivity().findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());
                alertDialogBuilder
                        .setCancelable(false)
                        .setMessage("გსურთ " + model.getName() + "-თან მიმოწერის წაშლა?")
                        .setPositiveButton("კი",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        presenter.deleteHistory(model.getId());
                                        NavController navController = Navigation.findNavController(getActivity(), R.id.main_fragment);
                                        navController.navigate(R.id.action_messageFragment_to_historyFragment, null);
                                    }
                                })
                        .setNegativeButton("არა",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface,int id) {
                                        dialogInterface.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        deleteButton.setVisibility(View.VISIBLE);

        this.presenter = new MessagePresenter(this);
        this.presenter.loadMessages(model.getId());
        ((TextView) getActivity().findViewById(R.id.toolbar_title)).setText(model.getName());
        getActivity().findViewById(R.id.toolbar_subtitle).setVisibility(View.VISIBLE);
        ((TextView) getActivity().findViewById(R.id.toolbar_subtitle)).setText(Helper.formatDate(model.getLastMessageDate(), "dd/MM/yyyy - HH:mm:ss"));

        NavigationView navigation = getActivity().findViewById(R.id.navigation);
        int size = navigation.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigation.getMenu().getItem(i).setChecked(false);
        }
    }



    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        messageText.setVisibility(View.GONE);
        sendButton.setVisibility(View.GONE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        if(!hideInput) {
            messageText.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showData(List<MessageModel> list) {
        this.adapter.setItems(list);
    }

    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
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
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
