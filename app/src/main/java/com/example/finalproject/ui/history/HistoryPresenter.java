package com.example.finalproject.ui.history;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

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
//                Database.getInstance().dataDao().insertHistory(new HistoryModelEntity("Name1", Helper.fromDate(new Date()), 1));
//                Database.getInstance().dataDao().insertHistory(new HistoryModelEntity("Name2", Helper.fromDate(new Date()), 1));
//                Database.getInstance().dataDao().insertHistory(new HistoryModelEntity("Name3", Helper.fromDate(new Date()), 999));
//                Database.getInstance().dataDao().insertHistory(new HistoryModelEntity("Name4", Helper.fromDate(new Date()), 99999));

                List<HistoryModelEntity> chats = Database.getInstance().dataDao().getChats();

                List<HistoryModel> result = new ArrayList<>();
                for (HistoryModelEntity model: chats) {
                    result.add(FromEntityToModel(model));
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
