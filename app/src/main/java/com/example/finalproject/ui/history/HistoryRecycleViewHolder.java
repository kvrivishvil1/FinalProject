package com.example.finalproject.ui.history;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Helper;
import com.example.finalproject.R;

import java.util.Date;

public class HistoryRecycleViewHolder extends RecyclerView.ViewHolder {


    public String getName() {
        return name.getText().toString();
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public String getDate() {
        return this.date.getText().toString();
    }

    public void setDate(Date date) {
        this.date.setText(Helper.formatDate(date, "dd/MM/yyyy - HH:mm:ss"));
    }

    public String getCount() {
        return name.getText().toString();
    }

    public void setCount(long count) {
        if(count > 999) {
            this.count.setText("999+");
        } else {
            this.count.setText("" + count);
        }
    }

    private TextView name;
    private TextView date;
    private TextView count;

    public HistoryRecycleViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.history_name);
        date = itemView.findViewById(R.id.history_date);
        count = itemView.findViewById(R.id.message_count);
    }
}
