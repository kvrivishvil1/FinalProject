package com.example.finalproject.ui.messages;

import com.example.finalproject.ui.models.HistoryModel;
import com.example.finalproject.ui.models.MessageModel;

import java.util.List;

public interface MessageContract {

    interface View {
        void showData(List<MessageModel> list);
        void showProgressBar();
        void hideProgressBar();
    }

    interface Presenter {
        void loadMessages(long userId);
    }

}
