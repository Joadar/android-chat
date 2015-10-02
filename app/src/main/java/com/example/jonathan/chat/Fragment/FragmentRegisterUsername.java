package com.example.jonathan.chat.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jonathan.chat.Manager.UserManager;
import com.example.jonathan.chat.R;
import com.example.jonathan.chat.Utils.SocketServer;

/**
 * Created by Jonathan on 21/09/15.
 */
public class FragmentRegisterUsername extends Fragment implements View.OnClickListener {

    private UserManager userManager;

    private EditText usernameText;
    private EditText passwordText;
    private EditText passwordRepeatText;

    private Button loginButton;

    private ImageView backButton;

    private String sexeUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userManager = new UserManager(getActivity());

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

                userManager.register(usernameText.getText().toString(), passwordText.getText().toString(), sexeUser);

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
