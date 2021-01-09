package com.Semaan.managenet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import Asynchronous.AsynchronousOrdersByTable;
import Asynchronous.ConnectivitySender;
import Asynchronous.PacketSender;
import Asynchronous.PacketServer;
import DataClasses.MenuObject;
import DataClasses.Order;
import Packets.ConnectivityOperation;
import Packets.Flags;
import Packets.RequestPacket;
import shared.User;
import shared.UserInfo;

import static Asynchronous.AsynchronousOrdersByTable.ordersbyTable;


public class CashierMainScreenActivity extends AppCompatActivity {

    private static double balance=0.0;
    public ProgressBar progressbar;
    public GridView gv;
    private static boolean doubleBackToExitPressedOnce = false;

    private PacketServer ps;

    public static CashierMainScreenActivity thiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_main_screen);
        ps = LoginActivity.getPacketServer();
        ps.setActivity(this);
        setProgressBarIndeterminateVisibility(true);
        thiss=this;
        progressbar = (ProgressBar) findViewById(R.id.progress_spinner_cashier);

        gv = (GridView) findViewById(R.id.waiter_main_screen_grid);
        Button externalOrder = (Button) findViewById(R.id.cashier_external_order);
        Button closeWaiter = (Button) findViewById(R.id.cashier_close_waiter);
        closeWaiter.setVisibility(View.GONE);


        try {
            if (ps.getStatus() != AsyncTask.Status.RUNNING)
                ps.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            requestRefresh();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AsynchronousOrdersByTable aobt = new AsynchronousOrdersByTable();
        aobt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        externalOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CashierMainScreenActivity.this, CashierMenuListActivity.class);
                intent.putExtra("table", -1);
                startActivity(intent);
            }
        });
        /*closeWaiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CashierMainScreenActivity.this, CashierCloseWaiterListActivity.class);
                startActivity(intent);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        ps.setActivity(this);
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            ConnectivityOperation co = new ConnectivityOperation(Flags.DISCONNECTION_REQUEST,new UserInfo(LoginActivity.loggedInUser.getUsername(),LoginActivity.loggedInUser.getPassword()));
            try {
                progressbar.setVisibility(View.VISIBLE);
                ConnectivitySender cs = new ConnectivitySender(co,MainActivity.getServerAddres());
                cs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Snackbar.make((View) gv.getParent(), "You will be logged out", Snackbar.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    private void requestRefresh() throws IOException {
        progressbar.setVisibility(View.VISIBLE);
        String ip;
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        PacketSender ps1 = new PacketSender(ip, MainActivity.getServerAddres(), User.CASHIER, Flags.REQUEST_TABLE_NUMBER);
        PacketSender ps2 = new PacketSender(ip, MainActivity.getServerAddres(), User.CASHIER, Flags.REQUEST_MENU);

        ps1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        ps2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public static void addToBalance(Double d){
        balance+=d;
    }
    public static double getBalance(){
        return balance;
    }
}