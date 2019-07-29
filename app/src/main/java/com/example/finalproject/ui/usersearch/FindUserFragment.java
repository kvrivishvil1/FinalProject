package com.example.finalproject.ui.usersearch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.ui.models.HistoryModel;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class FindUserFragment extends Fragment implements UserSearchContract.View{

    private ConstraintLayout searchLayout;
    private RecyclerView recyclerView;
    private UserSearchRecycleViewAdapter adapter;
    private UserSearchPresenter presenter;

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

        this.presenter = new UserSearchPresenter(this);
        this.presenter.searchUsers();
    }

    @Override
    public void showData(List<HistoryModel> list) {
        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        adapter.setItems(list);
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
