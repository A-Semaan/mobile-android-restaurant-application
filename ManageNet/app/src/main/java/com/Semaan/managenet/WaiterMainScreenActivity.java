package com.Semaan.managenet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
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
import Asynchronous.ConnectivityBroadcaster;
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


public class WaiterMainScreenActivity extends AppCompatActivity {

    private static double balance;
    public ProgressBar progressbar;
    private static boolean doubleBackToExitPressedOnce=false;
    public GridView gv;

    private PacketServer ps;

    public static WaiterMainScreenActivity thiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_main_screen);
        setProgressBarIndeterminateVisibility(true);
        thiss=this;
        ps=LoginActivity.getPacketServer();
        ps.setActivity(this);

        progressbar = (ProgressBar) findViewById(R.id.progress_spinner_waiter);
        gv = (GridView) findViewById(R.id.waiter_main_screen_grid);


        try {
            if(ps.getStatus()!=AsyncTask.Status.RUNNING)
                ps.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            requestRefresh();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*Cursor c = DatabaseTask.query("SELECT * FROM RestaurantTables");
        c.moveToFirst();
        CursorAdapter ca = new SimpleCursorAdapter(this,R.layout.table_layout,c,new String[]{"_id"},new int[]{R.id.table_num});
        gv.setAdapter(ca);
        setProgressBarIndeterminateVisibility(false);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WaiterMainScreenActivity.this,WaiterMenuListActivity.class);
                int tablenum = Integer.parseInt(((TextView)view.findViewById(R.id.table_num)).getText().toString());
                intent.putExtra("table",tablenum);
                startActivity(intent);
            }
        });*/
        AsynchronousOrdersByTable aobt = new AsynchronousOrdersByTable();
        aobt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        Snackbar.make((View)gv.getParent(), "You will be logged out", Snackbar.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

    private void requestRefresh() throws IOException {
        progressbar.setVisibility(View.VISIBLE);
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = Formatter.formatIpAddress(ipAddress);
        PacketSender ps1 = new PacketSender(ip,MainActivity.getServerAddres(),User.WAITER,Flags.REQUEST_TABLE_NUMBER);
        PacketSender ps2 = new PacketSender(ip,MainActivity.getServerAddres(),User.WAITER,Flags.REQUEST_MENU);

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
