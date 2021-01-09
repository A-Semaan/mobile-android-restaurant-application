package com.Semaan.managenet;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import Asynchronous.PacketSender;
import Asynchronous.PacketServer;
import Packets.Flags;
import Packets.RequestPacket;
import shared.User;

public class CashierCloseWaiterListActivity extends AppCompatActivity {

    public static ListView lv;
    public static ProgressBar progress;
    public static ArrayAdapter arrayAdapter;
    public static Context thiss;
    private PacketServer ps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_close_waiter_list);
        thiss=this;
        lv = (ListView) findViewById(R.id.cashier_close_waiter_list);
        progress = (ProgressBar) findViewById(R.id.progress_spinner_cashier_close_waiter);
        arrayAdapter = new ArrayAdapter(this,R.layout.list_view_row_layout);
        lv.setAdapter(arrayAdapter);
        ps=LoginActivity.getPacketServer();
        ps.setActivity(this);
        requestRefresh();
    }

    private void requestRefresh(){
        progress.setVisibility(View.VISIBLE);
        String ip;
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        RequestPacket p = new RequestPacket(ip,MainActivity.getServerAddres(), User.CASHIER,User.SERVER, Flags.REQUEST_CONNECTED_WAITERS);
        Log.e("REQUEST PACET",p.toString());
        PacketSender ps = new PacketSender(p);
        ps.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
