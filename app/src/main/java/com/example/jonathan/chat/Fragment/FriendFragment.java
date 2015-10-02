package com.example.jonathan.chat.Fragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jonathan.chat.Adapter.FriendAdapter;
import com.example.jonathan.chat.FriendActivity;
import com.example.jonathan.chat.Model.User;
import com.example.jonathan.chat.R;

import java.util.ArrayList;


/**
 * Created by Jonathan on 27/09/15.
 */
public class FriendFragment extends Fragment {

    private int color;
    private FriendAdapter adapter;
    private boolean isRequest;

    private ArrayList<User> listFriends;

    public FriendFragment(){

    }

    public static FriendFragment newInstance(int color, ArrayList<User> list, boolean isRequest) {
        FriendFragment dialog = new FriendFragment();

        Bundle args = new Bundle();
        args.putInt("color", color);
        args.putParcelableArrayList("friends", list);
        args.putBoolean("request", isRequest);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listFriends = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        color = getArguments().getInt("color");

        listFriends = getArguments().getParcelableArrayList("friends");

        Log.i("friendFragment", "friendFragment friends =  = " + listFriends);

        isRequest = getArguments().getBoolean("request");


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Log.i("friendFragment", "friendFragment.size = " + listFriends.size());

        adapter = new FriendAdapter(getContext(), listFriends, isRequest);

        recyclerView.setAdapter(adapter);

        return view;
    }

}