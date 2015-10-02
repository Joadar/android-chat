package com.example.jonathan.chat.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonathan.chat.ListActivity;
import com.example.jonathan.chat.Manager.UserManager;
import com.example.jonathan.chat.R;
import com.example.jonathan.chat.Utils.SocketServer;
import com.example.jonathan.chat.Utils.Tools;

import org.json.JSONObject;

import io.socket.emitter.Emitter;

/**
 * Created by Jonathan on 22/09/15.
 */
public class FragmentLoginNewAccount extends Fragment implements View.OnClickListener {

    private EditText username;
    private EditText password;
    private Button loginButton;
    private TextView useAccountSaved;

    private UserManager userManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        userManager = new UserManager(getActivity());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_new_account, container, false);

        username = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);
        loginButton = (Button) view.findViewById(R.id.loginButton);
        useAccountSaved = (TextView) view.findViewById(R.id.useAccountSaved);

        // add event click on button and text
        loginButton.setOnClickListener(this);
        useAccountSaved.setOnClickListener(this);

        username.setText(Tools.readFromPreferences(getContext(), "username", null));


        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == loginButton){
            // login
            if(!username.getText().toString().equals("") && !password.getText().toString().equals("")) {
                // if the server is not disconnected, we can access to the rest
                if (!SocketServer.getInstance().isDisconnected()) {

                    userManager.connect(username.getText().toString(), password.getText().toString(), null);

                } else {
                    Toast.makeText(getContext(), "Server not connected", Toast.LENGTH_LONG).show();
                }
            }

        } else if (v == useAccountSaved){ // display fragment login account existing
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            FragmentLoginAccountExisting nextFrag = new FragmentLoginAccountExisting();
            ft.replace(R.id.accountFragment, nextFrag, null);

            // Start the animated transition.
            ft.commit();
        }
    }
}
