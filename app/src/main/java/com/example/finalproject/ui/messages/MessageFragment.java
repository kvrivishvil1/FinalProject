package com.example.finalproject.ui.messages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.ui.models.HistoryModel;
import com.example.finalproject.ui.models.MessageModel;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MessageFragment extends Fragment implements MessageContract.View {

    private MessageRecycleViewAdapter adapter;
    private RecyclerView recyclerView;
    private MessagePresenter presenter;

    private HistoryModel model;
    private boolean hideInput;

    private EditText messageText;
    private Button sendButton;
    private ProgressBar progressBar;

    public MessageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.message_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels - (int) (displayMetrics.widthPixels * 0.2);

        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);

        Bundle bundle = getArguments();
        if(bundle == null) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.main_fragment);
            navController.navigate(R.id.action_messageFragment_to_historyFragment, null);
        }
        progressBar = view.findViewById(R.id.progress_bar);
        messageText = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.message_send);

        model = (HistoryModel) bundle.getSerializable("HistoryModel");
        if(model == null) {
            model = (HistoryModel) bundle.getSerializable("NewHistoryModel");
            hideInput = false;
        } else {
            hideInput = true;
            messageText.setVisibility(View.GONE);
            sendButton.setVisibility(View.GONE);
        }


        this.recyclerView = view.findViewById(R.id.message_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.adapter = new MessageRecycleViewAdapter(this, width);
        recyclerView.setAdapter(adapter);

        ImageView deleteButton = getActivity().findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        view.getContext());
                alertDialogBuilder
                        .setCancelable(false)
                        .setMessage("გსურთ " + model.getName() + "თან მიმოწერის წაშლა?")
//                        .set
                        .setPositiveButton("კი",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        presenter.deleteHistory(model.getId());
                                        NavController navController = Navigation.findNavController(getActivity(), R.id.main_fragment);
                                        navController.navigate(R.id.action_messageFragment_to_historyFragment, null);
                                    }
                                })
                        .setNegativeButton("არა",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface,int id) {
                                        dialogInterface.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }
        });
        deleteButton.setVisibility(View.VISIBLE);

        this.presenter = new MessagePresenter(this);
        this.presenter.loadMessages(model.getId());
        ((TextView) getActivity().findViewById(R.id.toolbar_title)).setText(model.getName());

        NavigationView navigation = getActivity().findViewById(R.id.navigation);
        int size = navigation.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigation.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        messageText.setVisibility(View.GONE);
        sendButton.setVisibility(View.GONE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        if(!hideInput) {
            messageText.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showData(List<MessageModel> list) {
        this.adapter.setItems(list);
    }

}
