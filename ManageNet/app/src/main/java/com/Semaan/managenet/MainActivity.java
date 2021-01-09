package com.Semaan.managenet;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.net.InetAddress;


public class MainActivity extends AppCompatActivity {

    private static InetAddress serverAddres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();
            }
        }, 2000);

    }

    public static InetAddress getServerAddres(){
        return serverAddres;
    }

    public static void setServerAddres(InetAddress address){
        serverAddres=address;
    }
}
