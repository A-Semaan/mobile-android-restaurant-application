package com.Semaan.managenet;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import Asynchronous.AsynchronousOrdersByTable;
import Asynchronous.PacketSender;
import Asynchronous.PacketServer;
import DataClasses.MenuObject;
import DataClasses.Order;
import Packets.PayPacket;
import shared.User;

import static Asynchronous.AsynchronousOrdersByTable.ordersbyTable;
import static Asynchronous.AsynchronousOrdersByTable.tablenumber;
import static Asynchronous.AsynchronousOrdersByTable.menu;

public class CashierMenuListActivity extends AppCompatActivity {

    private Order order;
    private static int tableNumber;
    private static  CurrentOrderListAdapter orderListAdapter;
    private static  ListView cashierCurrentOrderList;
    public static PageAdapter adapter;
    public static TabLayout tabs;
    public static TextView total;
    public static CashierMenuListActivity thiss;
    private PacketServer ps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_menu_list);
        tableNumber = getIntent().getIntExtra("table",-1);
        tablenumber=tableNumber;
        if(ordersbyTable.containsKey(tableNumber)){
            order = ordersbyTable.get(new Integer(tableNumber));

        }
        else{
            order = new Order(new Date(),LoginActivity.loggedInUser);
            ordersbyTable.put(tableNumber,order);
        }
        ps = LoginActivity.getPacketServer();
        ps.setActivity(this);
        cashierCurrentOrderList = (ListView) findViewById(R.id.cashier_menu_list_view);
        Button pay = (Button) findViewById(R.id.cashier_pay_button);
        tabs = (TabLayout) findViewById(R.id.cashier_menu_tab_layout);
        AsynchronousOrdersByTable.tabs=tabs;
        final ViewPager pager = (ViewPager) findViewById(R.id.cashier_menu_view_pager);
        System.out.println(menu+"  "+menu.toString()+"  "+menu.keySet()+"  "+menu.keySet().toString());
        thiss = this;
        for(String menutitle:menu.keySet()){
            tabs.addTab(tabs.newTab().setText(menutitle));
            System.out.println(menutitle);
        }
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        total = (TextView) findViewById(R.id.cashier_current_order_total);
        orderListAdapter = new CurrentOrderListAdapter(this,R.layout.current_order_list_row,order.getOrderitems());
        cashierCurrentOrderList.setAdapter(orderListAdapter);
        total.setText(order.getTotal()+"");
        AsynchronousOrdersByTable.orderListAdapter=orderListAdapter;
        adapter = new PageAdapter
                (this,getSupportFragmentManager(), tabs.getTabCount(),menu);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(order.isEmpty()){
                    return;
                }else{
                    WaiterMainScreenActivity.addToBalance(order.getTotal());
                    WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                    int ipAddress = wifiInfo.getIpAddress();
                    String ip = Formatter.formatIpAddress(ipAddress);
                    PayPacket pp = new PayPacket(ip,MainActivity.getServerAddres(), User.CASHIER,User.SERVER,order.getOrderitems(),order.getTotal(),order.getOrderTaker());
                    PacketSender ps = new PacketSender(pp);
                    ps.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    order = new Order(new Date(),LoginActivity.loggedInUser);
                    ordersbyTable.put(tableNumber,order);
                    orderListAdapter = new CurrentOrderListAdapter(CashierMenuListActivity.this,R.layout.current_order_list_row,order.getOrderitems());
                    cashierCurrentOrderList.setAdapter(orderListAdapter);
                    CashierMenuListActivity.super.onBackPressed();
                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(order.isEmpty()){
            if(ordersbyTable.containsKey(tablenumber))
                ordersbyTable.remove(tablenumber);

        }else{
            ordersbyTable.remove(tablenumber);
            ordersbyTable.put(tablenumber,order);

        }
    }
}