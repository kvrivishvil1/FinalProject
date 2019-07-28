package com.example.finalproject.ui.history;

import android.content.Context;

import com.example.finalproject.ui.models.HistoryModel;

import java.util.List;

public interface HistoryContract {

    interface View {
        void showData(List<HistoryModel> list);
        void openHistory(HistoryModel model);
        void deleteHistory(HistoryModel model);
        Context getContext();
        void showProgressBar();
        void hideProgressBar();
    }

    interface Presenter {
        void loadHistory();
        void clearHistory();
        void deleteHistory(long id);
    }

}
