package com.example.finalproject.ui.history;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.ui.models.HistoryModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private HistoryContract.View view;
    private List<HistoryModel> items;

    public HistoryRecycleViewAdapter(HistoryContract.View view) {
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
            final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_recycle_view_item, viewGroup, false);
            final HistoryRecycleViewHolder holder = new HistoryRecycleViewHolder(v);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int position = holder.getAdapterPosition();
                        view.openHistory(items.get(position/2));
                    }
                });
                v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    final int position = holder.getAdapterPosition();

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            view.getContext());
                    alertDialogBuilder
                            .setCancelable(false)
                            .setMessage("გსურთ " + items.get(position/2).getName() + "თან მიმოწერის წაშლა?")
                            .setPositiveButton("კი",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            view.deleteHistory(items.get(position/2));
                                            removeItem(position/2);
                                        }
                                    })
                            .setNegativeButton("არა",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface,int id) {
                                            dialogInterface.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                    return true;
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
        return position%2;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder recycleViewHolder, int i) {
        HistoryRecycleViewHolder holder = recycleViewHolder instanceof HistoryRecycleViewHolder ? ((HistoryRecycleViewHolder) recycleViewHolder) : null;
        if(holder != null) {
            holder.setName(items.get(i/2).getName());
            holder.setDate(items.get(i/2).getLastMessageDate());
            holder.setCount(items.get(i/2).getMessageCount());
        }
    }

    @Override
    public int getItemCount() {
        return items.size() * 2 - 1;
    }

    public void removeItem(int index) {
        items.remove(index);
        notifyDataSetChanged();
    }
}

