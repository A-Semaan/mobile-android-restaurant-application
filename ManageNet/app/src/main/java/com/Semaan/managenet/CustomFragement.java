package com.Semaan.managenet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import Asynchronous.AsynchronousOrdersByTable;
import DataClasses.MenuObject;
import DataClasses.Order;
import DataClasses.OrderItem;
import shared.User;

import static Asynchronous.AsynchronousOrdersByTable.orderListAdapter;
import static Asynchronous.AsynchronousOrdersByTable.ordersbyTable;
import static Asynchronous.AsynchronousOrdersByTable.tablenumber;
import static Asynchronous.AsynchronousOrdersByTable.tabs;

public class CustomFragement extends Fragment {

    private MenuArrayAdapter adapter;

    public CustomFragement(){
        super();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        adapter=(MenuArrayAdapter) bundle.getSerializable("adapter");
        ListView listView=(ListView)inflater.inflate(R.layout.menu_object_list_layout, container, false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("CLICKED AT "+position);
                System.out.println("pager click");
                TextView tv = view.findViewById(R.id.menu_object_name);
                String category = tabs.getTabAt(tabs.getSelectedTabPosition()).getText().toString();
                //    String name = ;
                MenuObject oi = AsynchronousOrdersByTable.menu.get(category).get(position);
                Order order=null;
                if(ordersbyTable.get(tablenumber)==null){
                    order= new Order(new Date(),LoginActivity.loggedInUser);
                    ordersbyTable.put(tablenumber,order);

                }
                else{
                    order=ordersbyTable.get(tablenumber);
                }
                System.out.println("Size is "+order.getOrderitems().size());
                order.addItem(oi);
                System.out.println("order item added");
                ArrayList<OrderItem> obs = order.getOrderitems();
                System.out.println("Size of objects is "+obs.size());
                orderListAdapter.setObjects(obs);
                System.out.println("List Switched");
                orderListAdapter.notifyDataSetChanged();

            }

        });
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return listView;
    }
}
