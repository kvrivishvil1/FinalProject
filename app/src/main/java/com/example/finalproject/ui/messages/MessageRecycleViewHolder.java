package com.example.finalproject.ui.messages;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Helper;
import com.example.finalproject.R;

import java.util.Date;

public class MessageRecycleViewHolder extends RecyclerView.ViewHolder {


    public String getText() {
        return text.getText().toString();
    }

    public void setText(String name) {
        this.text.setText(name);
    }

    public String getDate() {
        return this.date.getText().toString();
    }

    public void setDate(Date date) {
        this.date.setText(Helper.formatDate(date, "dd/MM/yyyy - HH:mm:ss"));
    }

    public void showOrHideDate() {
        if (date.getVisibility() == View.VISIBLE)
            date.setVisibility(View.GONE);
        else
            date.setVisibility(View.VISIBLE);
    }

    public void setMaxWidth(int width) {
        this.text.setMaxWidth(width);
    }

    private TextView text;
    private TextView date;

    public MessageRecycleViewHolder(@NonNull View itemView) {
        super(itemView);

        text = itemView.findViewById(R.id.message);
        date = itemView.findViewById(R.id.date);
    }
}
