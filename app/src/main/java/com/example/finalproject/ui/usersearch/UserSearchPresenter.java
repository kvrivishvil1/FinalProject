package com.example.finalproject.ui.usersearch;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.example.finalproject.Helper;
import com.example.finalproject.data.Database;
import com.example.finalproject.data.models.HistoryModelEntity;
import com.example.finalproject.data.models.MessageModelEntity;
import com.example.finalproject.ui.messages.MessageContract;
import com.example.finalproject.ui.models.HistoryModel;
import com.example.finalproject.ui.models.MessageModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserSearchPresenter implements UserSearchContract.Presenter {

    private UserSearchContract.View view;

    public UserSearchPresenter(UserSearchContract.View view) {
        this.view = view;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void searchUsers() {
        new AsyncTask<Void, Void, List<HistoryModel>>() {

            @Override
            protected List<HistoryModel> doInBackground(Void... voids) {
                List<HistoryModel> result = new ArrayList<>();
                try{

                    Thread.sleep(1000);
                    result.add(new HistoryModel("User1"));
                    result.add(new HistoryModel("User2"));
                    result.add(new HistoryModel("User3"));
                    result.add(new HistoryModel("User4"));

                }catch (Exception ex) {

                }
                return  result;
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
    public HistoryModel addUser(HistoryModel model) {
        model.setLastMessageDate(new Date());
        long id = Database.getInstance().dataDao().insertHistory(FromModelToEntity(model));
        model.setId(id);

        return model;
    }

    private HistoryModelEntity FromModelToEntity(HistoryModel model) {
        return new HistoryModelEntity(model.getName(), Helper.fromDate(model.getLastMessageDate()), model.getMessageCount());
    }

}
