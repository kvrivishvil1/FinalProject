package com.example.finalproject.ui.usersearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.WifiDirectBroadcastReceiver;
import com.example.finalproject.ui.MainActivity;
import com.example.finalproject.ui.models.HistoryModel;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;

public class FindUserFragment extends Fragment implements UserSearchContract.View{

    private ConstraintLayout searchLayout;
    private RecyclerView recyclerView;
    private UserSearchRecycleViewAdapter adapter;
    private UserSearchPresenter presenter;

    private Button sendMessageBtn;
    private TextView statusText;
    private TextView msgText;

    public FindUserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.find_user_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((NavigationView)getActivity().findViewById(R.id.navigation)).setCheckedItem(R.id.open_chat);

        getActivity().findViewById(R.id.toolbar).setVisibility(View.GONE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)getActivity()).enableToggle();

        TextView title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("");

        sendMessageBtn = getActivity().findViewById(R.id.send_msg_btn);
        statusText = getActivity().findViewById(R.id.status_text);

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMessageSend();
            }
        });

        searchLayout = view.findViewById(R.id.user_search_layout);

        view.findViewById(R.id.cancel_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.main_fragment);
                navController.navigate(R.id.action_findUserFragment_to_historyFragment, null);
            }
        });


        this.recyclerView = view.findViewById(R.id.user_search_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.adapter = new UserSearchRecycleViewAdapter(this);
        recyclerView.setAdapter(adapter);

        ImageView deleteButton = getActivity().findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE);

        getActivity().findViewById(R.id.toolbar_subtitle).setVisibility(View.GONE);
        ((TextView) getActivity().findViewById(R.id.toolbar_title)).setText("მომხმარებლები");

        this.presenter = new UserSearchPresenter(this, getActivity());
        this.presenter.searchUsers();
    }



    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void showData(List<HistoryModel> list) {
        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        adapter.setItems(list);
    }

    @Override
    public void changeStatus(String status) {
        statusText.setText(status);
    }

    @Override
    public void setMessage(String message) {
        msgText.setText(message);
    }

    @Override
    public void chatClicked(HistoryModel model) {
        presenter.chatClicked(model);
    }

    @Override
    public void addUser(HistoryModel model) {
        HistoryModel newModel = presenter.addUser(model);
        Bundle args = new Bundle();
        args.putSerializable("NewHistoryModel", newModel);

        NavController navController = Navigation.findNavController(getActivity(), R.id.main_fragment);
        navController.navigate(R.id.action_findUserFragment_to_messageFragment, args);
    }

    @Override
    public void showProgressBar() {
        searchLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        searchLayout.setVisibility(View.GONE);
    }
}
