package com.example.finalproject.ui.history;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.example.finalproject.data.Database;
import com.example.finalproject.data.models.HistoryModelEntity;
import com.example.finalproject.ui.models.HistoryModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryPresenter implements HistoryContract.Presenter {

    private HistoryContract.View view;

    public HistoryPresenter(HistoryContract.View view) {
        this.view = view;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void loadHistory() {
        new AsyncTask<Void, Void, List<HistoryModel>>() {

            @Override
            protected List<HistoryModel> doInBackground(Void... voids) {

                List<HistoryModelEntity> chats = Database.getInstance().dataDao().getChats();

                List<HistoryModel> result = new ArrayList<>();
                for (HistoryModelEntity model: chats) {
                    int messageCount = Database.getInstance().dataDao().getMessages(model.getId()).size();
                    model.setMessageCount(messageCount);
                    result.add(0, FromEntityToModel(model));
                }

                return result;
            }

            @Override
            protected void onPreExecute() {
                view.showProgressBar();
            }

            @Override
            protected void onPostExecute(List<HistoryModel> list) {
                view.hideProgressBar();
                view.showData(list);
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void clearHistory() {
        Database.getInstance().dataDao().deleteHistory();
    }

    @Override
    public void deleteHistory(long id) {
        Database.getInstance().dataDao().deleteHistory(id);
        Database.getInstance().dataDao().deleteMessages(id);
    }

    private HistoryModel FromEntityToModel(HistoryModelEntity model) {
        return new HistoryModel(model.getId(), model.getName(), model.getLastMessageDateAsDate(), model.getMessageCount());
    }
}
