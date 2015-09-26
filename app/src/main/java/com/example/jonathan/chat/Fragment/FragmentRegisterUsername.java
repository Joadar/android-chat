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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jonathan.chat.ListActivity;
import com.example.jonathan.chat.R;
import com.example.jonathan.chat.Utils.SocketServer;
import com.example.jonathan.chat.Utils.Tools;

import org.json.JSONObject;

import io.socket.emitter.Emitter;

/**
 * Created by Jonathan on 21/09/15.
 */
public class FragmentRegisterUsername extends Fragment implements View.OnClickListener {

    private EditText usernameText;
    private EditText passwordText;
    private EditText passwordRepeatText;

    private Button loginButton;

    private ImageView backButton;

    private String sexeUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_register_username, container, false);

        usernameText = (EditText) view.findViewById(R.id.username);
        passwordText = (EditText) view.findViewById(R.id.password);
        passwordRepeatText = (EditText) view.findViewById(R.id.passwordRepeat);

        loginButton = (Button) view.findViewById(R.id.login);
        backButton = (ImageView) view.findViewById(R.id.backButton);

        // get arguments sent
        Bundle data = this.getArguments();
        sexeUser = (String) data.get("sexe");
        // if the user is a boy
        if (sexeUser.equals("1")){
            loginButton.setBackgroundResource(R.drawable.button_boy); // put the button color in blue
        } else {
            loginButton.setBackgroundResource(R.drawable.button_girl); // put the button color in pink
        }

        loginButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == loginButton){
            login();
        } else if (v == backButton){
            backToSexeChoice();
        }
    }

    private void login(){
        // just for test if the username is empty AND if the password and repeat password are empty and if password and repeat password have the same value
        //if(usernameText.length() != 0 && passwordText.length() != 0 &&
          //      passwordText.getText().toString() == passwordRepeatText.getText().toString()){

            // if the server is not disconnected, we can access to the rest
            if(!SocketServer.getInstance().isDisconnected()) {

                // first we emit an user register

                // Sending an object
                JSONObject newUser = new JSONObject();
                try {
                    newUser.put("username", usernameText.getText().toString());
                    newUser.put("password", passwordText.getText().toString());
                    newUser.put("sexe", sexeUser);
                } catch (Exception e) {

                }

                SocketServer.getInstance().getSocket().emit("user_register", newUser);

                // get rooms one by one
                SocketServer.getInstance().getSocket().on("user_register_fail", new Emitter.Listener() {

                    @Override
                    public void call(final Object... args) {
                        Toast.makeText(getContext(), "Error, try again", Toast.LENGTH_LONG).show();
                    }
                });

                SocketServer.getInstance().getSocket().on("user_register_success", new Emitter.Listener() {

                    @Override
                    public void call(final Object... args) {
                        // second we emit the user login if there is no problem before

                        Tools.saveToPreferences(getContext(), "username", usernameText.getText().toString());
                        Tools.saveToPreferences(getContext(), "connected", "true");

                        // Sending an object
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("username", usernameText.getText().toString());
                            obj.put("sexe", sexeUser);
                        } catch (Exception e) {

                        }

                        // instance the socket with the username
                        SocketServer.getInstance().getSocket().emit("new_user", obj);

                        Intent intent = new Intent(getContext(), ListActivity.class);
                        getActivity().finish();
                        startActivity(intent);
                    }
                });

            } else {
                Toast.makeText(getContext(), "Server not connected", Toast.LENGTH_LONG).show();
            }
        //}
    }

    private void backToSexeChoice(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);

        FragmentRegisterSexe nextFrag = new FragmentRegisterSexe();
        ft.replace(R.id.contentFragment, nextFrag, null);

        // Start the animated transition.
        ft.commit();
    }
}
