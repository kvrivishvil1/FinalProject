package com.example.finalproject.ui.messages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.ui.models.HistoryModel;
import com.example.finalproject.ui.models.MessageModel;

import java.util.ArrayList;
import java.util.List;

public class MessageRecycleViewAdapter extends RecyclerView.Adapter<MessageRecycleViewHolder> {

    private MessageContract.View view;
    private int width;
    private List<MessageModel> items;

    public MessageRecycleViewAdapter(MessageContract.View view, int width) {
        this.view = view;
        this.width = width;
        items = new ArrayList<>();
    }

    public void setItems(List<MessageModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        final View v;
        if (type == 0)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sent_message_recycle_view_item, viewGroup, false);
        else
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.received_message_recycle_view_item, viewGroup, false);


        final MessageRecycleViewHolder holder = new MessageRecycleViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showOrHideDate();
            }
        });
        return holder;
    }

    @Override
    public int getItemViewType(final int position) {
        return items.get(position).isSent() ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRecycleViewHolder recycleViewHolder, int i) {
        recycleViewHolder.setMaxWidth(this.width);
        recycleViewHolder.setText(items.get(i).getText());
        recycleViewHolder.setDate(items.get(i).getDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

