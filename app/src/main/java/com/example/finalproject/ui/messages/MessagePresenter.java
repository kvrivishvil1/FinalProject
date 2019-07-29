package com.example.finalproject.ui.messages;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.example.finalproject.Helper;
import com.example.finalproject.data.DataDao;
import com.example.finalproject.data.Database;
import com.example.finalproject.data.models.HistoryModelEntity;
import com.example.finalproject.data.models.MessageModelEntity;
import com.example.finalproject.ui.models.HistoryModel;
import com.example.finalproject.ui.models.MessageModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagePresenter implements MessageContract.Presenter {

    private MessageContract.View view;

    public MessagePresenter(MessageContract.View view) {
        this.view = view;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void loadMessages(final long userId) {
        new AsyncTask<Void, Void, List<MessageModel>>() {

            @Override
            protected List<MessageModel> doInBackground(Void... voids) {
                Database.getInstance().dataDao().insertMessage(new MessageModelEntity(userId, "რას შვები რავახარ?", true, Helper.fromDate(new Date())));
                Database.getInstance().dataDao().insertMessage(new MessageModelEntity(userId, "რავი შენ?", false, Helper.fromDate(new Date())));
                Database.getInstance().dataDao().insertMessage(new MessageModelEntity(userId, "იცოდი რომ ამერიკის ოცდამეჩვიდმეტე პრეზიდენტს, რიჩარდ ნიქსონს ძალიან უყვარდა ხაჭოს კეჩუპით ჭამა?", true, Helper.fromDate(new Date())));
                Database.getInstance().dataDao().insertMessage(new MessageModelEntity(userId, "??", false, Helper.fromDate(new Date())));
//                Database.getInstance().dataDao().deleteMessages(userId);
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

}
