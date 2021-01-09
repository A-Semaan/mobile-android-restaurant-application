package Asynchronous;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.widget.TableLayout;
import android.widget.TextView;


import com.Semaan.managenet.CurrentOrderListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import DataClasses.MenuObject;
import DataClasses.Order;

public class AsynchronousOrdersByTable extends AsyncTask {

    public static final HashMap<Integer, Order> ordersbyTable = new HashMap<Integer,Order>();
    public static HashMap<String, ArrayList<MenuObject>> menu;
    public static int tablenumber;
    public static CurrentOrderListAdapter orderListAdapter;
    public static TabLayout tabs;
    public static boolean editingRightsApproved=false;
    public static boolean isRequestingEditingRights=false;

    public AsynchronousOrdersByTable(){

    }

    @Override
    protected Object doInBackground(Object[] objects) {

        return null;
    }


}
