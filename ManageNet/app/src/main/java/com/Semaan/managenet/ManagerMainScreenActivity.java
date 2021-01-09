package com.Semaan.managenet;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.InetAddress;
import java.util.HashSet;

import shared.AssistantManager;
import shared.Cashier;
import shared.Manager;
import shared.User;
import shared.Waiter;

public class ManagerMainScreenActivity extends AppCompatActivity {

    private static int typeOfManager;
    private ListView lv;
    private static boolean doubleBackToExitPressedOnce=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main_screen);


        lv=  (ListView) findViewById(R.id.manager_main_screen_list);
        ArrayAdapter<CharSequence> adapter;

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch(position){
                    case 0:
                        intent = new Intent(ManagerMainScreenActivity.this, ManagerUserListActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(ManagerMainScreenActivity.this, ManagerCategoryListActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(ManagerMainScreenActivity.this, ManagerMenuListActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        setTableNumber();
                        break;
                    case 4:
                        intent = new Intent(ManagerMainScreenActivity.this, ManagerShowReportActivity.class);
                        startActivity(intent);
                        break;

                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Snackbar.make((View)lv.getParent(), "You will be logged out", Snackbar.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }
    private void setTableNumber(){

    }
}
