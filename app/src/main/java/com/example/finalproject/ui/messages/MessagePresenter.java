package com.example.finalproject.ui.messages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.finalproject.Helper;
import com.example.finalproject.R;
import com.example.finalproject.data.DataDao;
import com.example.finalproject.data.Database;
import com.example.finalproject.data.models.HistoryModelEntity;
import com.example.finalproject.data.models.MessageModelEntity;
import com.example.finalproject.ui.SocketHandler;
import com.example.finalproject.ui.models.HistoryModel;
import com.example.finalproject.ui.models.MessageModel;
import com.example.finalproject.ui.usersearch.UserSearchPresenter;

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

public class MessagePresenter implements MessageContract.Presenter {

    private MessageContract.View view;
    private Context context;
    private HistoryModel model;
    private boolean isHistory;

    SendReceive sendReceive;

    static final int MESSAGE_READ = 1;


    public MessagePresenter(MessageContract.View view, Context context, HistoryModel model, boolean isHistory) {
        this.view = view;
        this.context = context;
        this.model = model;
        this.isHistory = isHistory;

        if (!isHistory) {
            sendReceive = new SendReceive(SocketHandler.getSocket());
            sendReceive.start();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void loadMessages(final long userId) {
        new AsyncTask<Void, Void, List<MessageModel>>() {

            @Override
            protected List<MessageModel> doInBackground(Void... voids) {
                List<MessageModelEntity> messages = Database.getInstance().dataDao().getMessages(userId);

                List<MessageModel> result = new ArrayList<>();
                for (MessageModelEntity model: messages) {
                    result.add(FromEntityToModel(model));
                }

                return result;
            }

            @Override
            protected void onPreExecute() {
                view.showProgressBar();
            }

            @Override
            protected void onPostExecute(List<MessageModel> list) {
                view.hideProgressBar();
                view.showData(list);
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void deleteHistory(long id) {
        Database.getInstance().dataDao().deleteHistory(id);
        Database.getInstance().dataDao().deleteMessages(id);
    }

    private MessageModel FromEntityToModel(MessageModelEntity model) {
        return new MessageModel(model.getId(), model.getUserId(), model.getText(), model.isSent(), model.getMessageDateAsDate());
    }

    @Override
    public void sendMessage(String msg) {
        sendReceive.write(msg.getBytes());
        newMessage(msg, true);
    }

    @SuppressLint("StaticFieldLeak")
    private void newMessage(final String msg, final boolean sent) {

        new AsyncTask<Void, Void, List<MessageModel>>() {

            @Override
            protected List<MessageModel> doInBackground(Void... voids) {
                Database.getInstance().dataDao().insertMessage(new MessageModelEntity(model.getId(), msg, sent, Helper.fromDate(new Date())));
                List<MessageModelEntity> messages = Database.getInstance().dataDao().getMessages(model.getId());

                List<MessageModel> result = new ArrayList<>();
                for (MessageModelEntity model: messages) {
                    result.add(FromEntityToModel(model));
                }

                return result;
            }

            @Override
            protected void onPostExecute(List<MessageModel> list) {
                view.showData(list);
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) message.obj;
                    String tempMsg = new String(readBuff, 0, message.arg1);
//                     msg in tempmsg
                    if (tempMsg.equals(SocketHandler.getStopWord())) {
                        Toast.makeText(context, "კავშირი მოწყობილობასთან გაწყვეტილია", Toast.LENGTH_LONG).show();

                        NavController navController = Navigation.findNavController((Activity) context, R.id.main_fragment);
                        navController.navigate(R.id.action_messageFragment_to_historyFragment, null);
                        Helper.closeKeyboard((Activity) context);
                        break;
                    }
                    newMessage(tempMsg, false);
                    break;
            }
            return true;
        }
    });

    public class SendReceive extends Thread {
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt) {
            socket = skt;
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

        public void write(final byte[] bytes) {
            try {
                (new Thread() {
                    @Override
                    public void run() {
                        try {
                            outputStream.write(bytes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
