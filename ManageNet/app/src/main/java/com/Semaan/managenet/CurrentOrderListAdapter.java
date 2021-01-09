package com.Semaan.managenet;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import Asynchronous.PacketSender;
import DataClasses.OrderItem;
import Packets.Flags;
import Packets.RequestPacket;
import shared.User;

import static Asynchronous.AsynchronousOrdersByTable.editingRightsApproved;
import static Asynchronous.AsynchronousOrdersByTable.ordersbyTable;
import static Asynchronous.AsynchronousOrdersByTable.tablenumber;
import static android.content.Context.WIFI_SERVICE;

public class CurrentOrderListAdapter extends ArrayAdapter{

    Context mContext;
    int resource;
    ArrayList<OrderItem> objects;

    public CurrentOrderListAdapter(Context context, int resource, List<OrderItem> objects){
        super(context,resource,objects);
        mContext=context;
        this.resource=resource;
        this.objects=(ArrayList<OrderItem>) objects;

    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(mContext);
        View vi = li.inflate(R.layout.current_order_list_row,parent,false);
        TextView name = vi.findViewById(R.id.current_order_item_name);
        final TextView qty = vi.findViewById(R.id.current_order_item_qty);
        final EditText note = vi.findViewById(R.id.current_order_item_note);
        final ImageButton increment = vi.findViewById(R.id.current_order_item_increment);
        ImageButton decrement = vi.findViewById(R.id.current_order_item_decrement);
        final OrderItem oi = objects.get(position);

        name.setText(oi.getMenuObject().getName());
        qty.setText(oi.getQtyOfMenuObject()+"");
        note.setText(oi.getNote());
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newqty=Integer.parseInt(qty.getText().toString())+1;
                qty.setText(newqty+"");
                ordersbyTable.get(tablenumber).incrementItem(oi.getMenuObject());
                ViewParent vp = parent.getParent();

            }
        });
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginActivity.loggedInUser.getFlag()!=User.WAITER||editingRightsApproved) {
                    int newqty = Integer.parseInt(qty.getText().toString()) - 1;
                    qty.setText(newqty + "");
                    ordersbyTable.get(tablenumber).decrementItem(oi.getMenuObject());
                }
                else{
                    Snackbar.make(increment.getRootView(),"Requesting editing rights",Snackbar.LENGTH_SHORT).show();
                    System.out.println("Snackbar shown");
                    WifiManager wifiMgr = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                    int ipAddress = wifiInfo.getIpAddress();
                    String ip = Formatter.formatIpAddress(ipAddress);
                    RequestPacket rp = new RequestPacket(ip,MainActivity.getServerAddres(), LoginActivity.loggedInUser.getFlag(),User.SERVER, Flags.ORDER_EDITING_REQUEST);
                    PacketSender ps = new PacketSender(rp);
                    ps.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

            }
        });
        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                ordersbyTable.get(tablenumber).setNote(oi,text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return vi;
    }

    public void setObjects(List<OrderItem> objects) {
        this.objects = (ArrayList<OrderItem>) objects;
        notifyDataSetChanged();
    }
}
