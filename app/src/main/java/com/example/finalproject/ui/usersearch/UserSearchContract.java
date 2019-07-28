package com.example.finalproject.ui.usersearch;

import com.example.finalproject.ui.models.HistoryModel;
import com.example.finalproject.ui.models.MessageModel;

import java.util.List;

public interface UserSearchContract {
    interface View {
        void showData(List<HistoryModel> list);
        void addUser(HistoryModel model);
        void showProgressBar();
        void hideProgressBar();
    }

    interface Presenter {
        void searchUsers();
        HistoryModel addUser(HistoryModel model);
    }
}
