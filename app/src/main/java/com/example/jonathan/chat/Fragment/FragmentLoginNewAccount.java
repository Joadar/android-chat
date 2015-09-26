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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

                    // Sending an object
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("username", username.getText().toString());
                        obj.put("password", password.getText().toString());
                    } catch (Exception e) {

                    }

                    // emit the new user
                    SocketServer.getInstance().getSocket().emit("new_user", obj);

                    SocketServer.getInstance().getSocket().on("logged", new Emitter.Listener() {

                        @Override
                        public void call(final Object... args) {

                            /** check if activity still exist */
                            if (getActivity() == null) {
                                return;
                            }


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Tools.saveToPreferences(getContext(), "connected", "true");
                                    Tools.saveToPreferences(getContext(), "username", username.getText().toString());
                                    Tools.saveToPreferences(getContext(), "password", password.getText().toString());
                                    Tools.saveToPreferences(getContext(), "sexe", String.valueOf(args[0]));

                                    Intent intent = new Intent(getContext(), ListActivity.class);
                                    getActivity().finish();
                                    startActivity(intent);
                                }
                            });
                        }
                    });

                    SocketServer.getInstance().getSocket().on("error_user", new Emitter.Listener() {

                        @Override
                        public void call(final Object... args) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Error about the account", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
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
