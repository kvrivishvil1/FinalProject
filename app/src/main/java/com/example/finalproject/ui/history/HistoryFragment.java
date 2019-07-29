package com.example.finalproject.ui.history;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.Button;


import com.example.finalproject.R;
import com.example.finalproject.ui.models.HistoryModel;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements HistoryContract.View {

    private HistoryRecycleViewAdapter adapter;
    private HistoryPresenter presenter;

    private Button clearHistoryBtn;

    private TextView historyStatus;
    private ProgressBar progressBar;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.history_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((NavigationView)getActivity().findViewById(R.id.navigation)).setCheckedItem(R.id.open_history);

        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        historyStatus = view.findViewById(R.id.history_search_status);
        progressBar = view.findViewById(R.id.progress_bar);

        this.clearHistoryBtn = view.findViewById(R.id.clear_history_button);
        this.clearHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.clearHistory();
                showData(new ArrayList<HistoryModel>());
            }
        });

        ImageView deleteButton = getActivity().findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE);

        RecyclerView recyclerView = view.findViewById(R.id.history_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        this.adapter = new HistoryRecycleViewAdapter(this);
        recyclerView.setAdapter(adapter);

        this.presenter = new HistoryPresenter(this);
        this.presenter.loadHistory();

    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        clearHistoryBtn.setVisibility(View.GONE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        clearHistoryBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void showData(List<HistoryModel> list) {
        historyStatus.setVisibility(list.size() > 0 ? View.GONE : View.VISIBLE);
        TextView title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("ისტორია" + (list.size() > 0 ? "(" + list.size() + ")" : ""));
        this.adapter.setItems(list);
    }

    @Override
    public void openHistory(HistoryModel model) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.main_fragment);
        Bundle args = new Bundle();
        args.putSerializable("HistoryModel", model);
        navController.navigate(R.id.action_historyFragment_to_messageFragment, args);
    }

    @Override
    public void deleteHistory(HistoryModel model) {
        presenter.deleteHistory(model.getId());
    }
}
