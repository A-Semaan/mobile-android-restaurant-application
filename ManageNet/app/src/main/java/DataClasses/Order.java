package DataClasses;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

import com.Semaan.managenet.CashierMenuListActivity;
import com.Semaan.managenet.LoginActivity;
import com.Semaan.managenet.R;
import com.Semaan.managenet.WaiterMenuListActivity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import shared.User;

import static Asynchronous.AsynchronousOrdersByTable.orderListAdapter;
import static Asynchronous.AsynchronousOrdersByTable.ordersbyTable;
import static Asynchronous.AsynchronousOrdersByTable.tablenumber;

public class Order implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -649543427318569440L;
	private static int next=0;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private int id;
    private Date timeStamp;
    private double total;
    ArrayList<OrderItem> orderitems;
    User orderTaker;

    public Order(Date timeStamp, User orderTaker) {
        id=next++;
        this.timeStamp = timeStamp;
        this.total = total;
        this.orderTaker = orderTaker;
        orderitems = new ArrayList<OrderItem>();
    }

    public int getId() {
        return id;
    }

    public String getTimeStamp() {
        return sdf.format(timeStamp);
    }

    public double getTotal() {
        double tot=0.0;
        for(OrderItem i:orderitems){
            tot+=(i.getMenuObject().getPrice()*i.getQtyOfMenuObject());
        }
        return tot;
    }

    public void addItem(MenuObject orderItem){
        OrderItem temp = new OrderItem(orderItem,1);
        if(contains(temp)) {
            System.out.println("OBJECT ALREAADY EXISTS INCREASING QUANTITY");
            Iterator<OrderItem>  it = orderitems.iterator();
            while(it.hasNext()){
                OrderItem oItem = it.next();
                if(oItem.getMenuObject().compareTo(orderItem)==0){
                    oItem.setQtyOfMenuObject(oItem.getQtyOfMenuObject()+1);
                    updateTotal();
                    return;
                }
            }
        }
       orderitems.add(temp);
        updateTotal();
    }

    public void incrementItem(MenuObject orderItem){
        Iterator<OrderItem>  it = orderitems.iterator();
        while(it.hasNext()){
            OrderItem oItem = it.next();
            if(oItem.getMenuObject().compareTo(orderItem)==0){
                oItem.setQtyOfMenuObject(oItem.getQtyOfMenuObject()+1);
            }
        }
        orderListAdapter.notifyDataSetChanged();
        updateTotal();
    }

    public void decrementItem(MenuObject orderItem){
        Iterator<OrderItem>  it = orderitems.iterator();

        while(it.hasNext()){
            OrderItem oItem = it.next();
            if(oItem.getMenuObject().compareTo(orderItem)==0){
                if(oItem.getQtyOfMenuObject()==1)
                    it.remove();
                else {
                    oItem.setQtyOfMenuObject(oItem.getQtyOfMenuObject() - 1);
                }
            }
        }
        orderListAdapter.notifyDataSetChanged();
        updateTotal();
    }

    public void setNote(OrderItem oi, String text){
        Iterator<OrderItem>  it = orderitems.iterator();

        while(it.hasNext()){
            OrderItem oItem = it.next();
            if(oItem.getMenuObject().compareTo(oi.getMenuObject())==0){
                oItem.setNote(text);
            }
        }
        orderListAdapter.notifyDataSetChanged();
        updateTotal();
    }

    public void removeItem(OrderItem orderItem){

        orderitems.remove(orderItem);
        updateTotal();
    }

    public void setquantityOfMenuObect(MenuObject orderItem,int qty){
        OrderItem temp = new OrderItem(orderItem,qty);
        orderitems.set(orderitems.indexOf(orderItem),temp);
        orderListAdapter.notifyDataSetChanged();
        updateTotal();
    }

    public boolean isEmpty(){
        return orderitems.isEmpty();
    }

    public ArrayList<OrderItem> getOrderitems() {
        return orderitems;
    }


    public User getOrderTaker() {
        return orderTaker;
    }

    private boolean contains(OrderItem oi){
        for(OrderItem i:orderitems){
            if(i.getMenuObject().compareTo(oi.getMenuObject())==0)
                return true;
        }
        return false;
    }
    private void updateTotal(){


        Activity activity;

        if(LoginActivity.loggedInUser.getFlag()== User.WAITER) {
            final TextView tv= WaiterMenuListActivity.total;
            WaiterMenuListActivity.thiss.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.setText(ordersbyTable.get(tablenumber).getTotal()+"");
                }
            });;
        }
        else {
            final TextView tv= CashierMenuListActivity.total;
            CashierMenuListActivity.thiss.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.setText(ordersbyTable.get(tablenumber).getTotal()+"");
                }
            });
        }

    }
}
