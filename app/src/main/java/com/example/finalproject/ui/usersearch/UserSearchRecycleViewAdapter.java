package com.example.finalproject.ui.usersearch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.ui.history.HistoryDividerRecycleViewHolder;
import com.example.finalproject.ui.history.HistoryRecycleViewHolder;
import com.example.finalproject.ui.messages.MessageContract;
import com.example.finalproject.ui.messages.MessageRecycleViewHolder;
import com.example.finalproject.ui.models.HistoryModel;
import com.example.finalproject.ui.models.MessageModel;

import java.util.ArrayList;
import java.util.List;

public class UserSearchRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private UserSearchContract.View view;
    private List<HistoryModel> items;

    public UserSearchRecycleViewAdapter(UserSearchContract.View view) {
        this.view = view;
        items = new ArrayList<>();
    }

    public void setItems(List<HistoryModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        if(type == 0) {
            final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.find_user_recycle_view_item, viewGroup, false);
            final UserSearchRecycleViewHolder holder = new UserSearchRecycleViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = holder.getAdapterPosition();
                    view.chatClicked(items.get(position/2));
                }
            });
            return  holder;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_divider_item, viewGroup, false);
            return  new HistoryDividerRecycleViewHolder(v);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return position % 2;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder recycleViewHolder, int i) {
        UserSearchRecycleViewHolder holder = recycleViewHolder instanceof UserSearchRecycleViewHolder ? ((UserSearchRecycleViewHolder) recycleViewHolder) : null;
        if(holder != null) {
            holder.setName(items.get(i/2).getName());
        }
    }

    @Override
    public int getItemCount() {
        return items.size() * 2 - 1;
    }
}

