package com.example.jonathan.chat.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jonathan.chat.ListActivity;
import com.example.jonathan.chat.R;
import com.example.jonathan.chat.Utils.SocketServer;
import com.example.jonathan.chat.Utils.Tools;

import org.json.JSONObject;

/**
 * Created by Jonathan on 21/09/15.
 */
public class UsernameFragment extends Fragment implements View.OnClickListener {

    private EditText usernameText;
    private Button loginButton;
    private TextView backText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_username, container, false);

        usernameText = (EditText) view.findViewById(R.id.username);
        loginButton = (Button) view.findViewById(R.id.login);
        backText = (TextView) view.findViewById(R.id.back);

        
        // if the user is a boy
        Log.d("UsernameFragmentLog", "sexe = " + Tools.readFromPreferences(getContext(), "sexe", "0"));
        if(Tools.readFromPreferences(getContext(), "sexe", "0").equals("boy")){
            loginButton.setBackgroundResource(R.drawable.button_boy); // put the button color in blue
        } else {
            loginButton.setBackgroundResource(R.drawable.button_girl); // put the button color in pink
        }

        loginButton.setOnClickListener(this);
        backText.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == loginButton){
            login();
        } else if (v == backText){
            backToSexeChoice();
        }
    }

    private void login(){
        // just for test if the username is empty
        if(usernameText.length() != 0){

            Tools.saveToPreferences(getContext(), "username", usernameText.getText().toString());

            // Sending an object
            JSONObject obj = new JSONObject();
            try {
                obj.put("username", usernameText.getText().toString());
                obj.put("sexe", Tools.readFromPreferences(getContext(), "sexe", "0"));
            } catch(Exception e){

            }

            // instance the socket with the username
            SocketServer.getInstance(obj);

            Intent intent = new Intent(getContext(), ListActivity.class);
            getActivity().finish();
            startActivity(intent);
        }
    }

    private void backToSexeChoice(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);

        SexeFragment nextFrag = new SexeFragment();
        ft.replace(R.id.contentFragment, nextFrag, null);

        // Start the animated transition.
        ft.commit();
    }
}
