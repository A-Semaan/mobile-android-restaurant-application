package com.Semaan.managenet;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.concurrent.ThreadPoolExecutor;

import Asynchronous.ConnectivityBroadcaster;
import Asynchronous.ConnectivityServer;
import Asynchronous.PacketServer;
import shared.User;
import shared.UserInfo;
import shared.Waiter;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button login;
    Toolbar toolbar;
    public ProgressBar progressBar;
    private static PacketServer ps;
    public static User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progress_spinner_login);


        username = (EditText) findViewById(R.id.login_username_text);
        password = (EditText) findViewById(R.id.login_password_text);
        login = (Button) findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields()){

                    progressBar.setVisibility(View.VISIBLE);

                    UserInfo userinfo = new UserInfo(username.getText().toString(),password.getText().toString());

                    ConnectivityBroadcaster cb = new ConnectivityBroadcaster(userinfo);
                    cb.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    if(username.getText().toString().equals("")){
                        username.setError("Field can't be empty");
                        username.requestFocus();
                    }else{
                        password.setError("Field can't be empty");
                        password.requestFocus();
                    }
                }
            }
            private boolean checkFields(){
                return !username.getText().toString().equals("")&&!password.getText().toString().equals("");
            }
        });

        ConnectivityServer cnsv = new ConnectivityServer(this,username);
        cnsv.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        ps= new PacketServer(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public static PacketServer getPacketServer(){
        return ps;
    }
}
