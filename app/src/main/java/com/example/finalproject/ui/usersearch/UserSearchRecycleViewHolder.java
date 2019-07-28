package com.example.finalproject.ui.usersearch;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Helper;
import com.example.finalproject.R;

import org.w3c.dom.Text;

import java.util.Date;

public class UserSearchRecycleViewHolder extends RecyclerView.ViewHolder {


    TextView name;

    public String getName() {
        return name.getText().toString();
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public UserSearchRecycleViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.mobile_name);
    }
}
