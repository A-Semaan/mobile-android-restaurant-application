package Asynchronous;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.Semaan.managenet.CashierCloseWaiterListActivity;
import com.Semaan.managenet.CashierMainScreenActivity;
import com.Semaan.managenet.CashierMenuListActivity;
import com.Semaan.managenet.LoginActivity;
import com.Semaan.managenet.MainActivity;
import com.Semaan.managenet.R;
import com.Semaan.managenet.WaiterMainScreenActivity;
import com.Semaan.managenet.WaiterMenuListActivity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import DataClasses.MenuObject;
import Packets.DataPacket;
import Packets.Flags;
import Packets.Packet;
import Packets.Ports;
import Packets.ReplyPacket;
import Packets.RequestPacket;
import Packets.SQLPacket;
import shared.AssistantManager;
import shared.User;
import shared.Waiter;

public class PacketServer extends AsyncTask implements Flags,Ports{
	
	private DatagramSocket ss;
	private CashierMainScreenActivity activityCashier;
	private WaiterMainScreenActivity activityWaiter;
	private CashierCloseWaiterListActivity activityCashierCloseWaiter;
	private CashierMenuListActivity activityCashierMenuList;
	private boolean isWaiter=false;

	public PacketServer(Activity activity) {
		System.setProperty("java.net.preferIPv4Stack" , "true");
		if(activity instanceof WaiterMainScreenActivity){
			this.activityWaiter=(WaiterMainScreenActivity) activity;
			isWaiter=true;
		}

		else if(activity instanceof CashierMainScreenActivity)
			this.activityCashier=(CashierMainScreenActivity) activity;

		try {
			ss = new DatagramSocket(USER_REQUEST_PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected Object doInBackground(Object[] objects) {
		synchronized (ss) {
			do {
				
				try {
					
					System.out.println("PACKET SERVER DEPLOYED");
					byte[] buf = new byte[2048];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
		            ss.receive(packet);
		            System.out.println("PACKET OBJECT RECIEVED");
		            InetAddress address = packet.getAddress();
		            int port = packet.getPort();
		            System.out.println("here1");
					
					byte[] actuals = packet.getData();
					System.out.println("here2");
					ByteArrayInputStream bis = new ByteArrayInputStream(actuals);
					System.out.println("here3");
					Object o = new ObjectInputStream(bis).readObject();
					
					System.out.println("here4");
					if(o instanceof Packet) {
						if(o instanceof DataPacket) {
							DataPacket p = (DataPacket) o;

						}
						else if(o instanceof ReplyPacket){
							final ReplyPacket p = (ReplyPacket) o;
							switch(p.getRequest()){
								case REPLY_MENU:
									if(isWaiter){
										activityWaiter.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												Log.e("MENU RECEIVEd","WAITER");
												AsynchronousOrdersByTable.menu=p.getMenu();
												Log.e("MENU SET","WAITER");
												Snackbar.make(activityWaiter.gv,"Menu received",Snackbar.LENGTH_SHORT);
												Log.e("MENU","SNACKBAR DISPLAYED  " +p+"  "+p.getMenu()+"  "+AsynchronousOrdersByTable.menu);
												activityWaiter.progressbar.setVisibility(View.INVISIBLE);
											}
										});
									}
									else{
										activityCashier.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												AsynchronousOrdersByTable.menu=p.getMenu();
												Snackbar.make(activityCashier.gv,"Menu received",Snackbar.LENGTH_SHORT);
												activityCashier.progressbar.setVisibility(View.INVISIBLE);
											}
										});
									}
									break;
								case REPLY_TABLE_NUMBER:
									if(isWaiter){
										activityWaiter.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												ArrayAdapter<String> ad = new ArrayAdapter<String>(activityWaiter, R.layout.table_layout,R.id.table_num,(String[])p.getData());
												activityWaiter.gv.setAdapter(ad);
												activityWaiter.gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
													@Override
													public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
														Intent intent = new Intent(activityWaiter, WaiterMenuListActivity.class);
														int tablenum = Integer.parseInt(((TextView)view.findViewById(R.id.table_num)).getText().toString());
														intent.putExtra("table",tablenum);
														if(AsynchronousOrdersByTable.ordersbyTable.keySet().contains(tablenum)){
															intent.putExtra("tableorder",AsynchronousOrdersByTable.ordersbyTable.get(tablenum));
														}
														activityWaiter.startActivity(intent);
													}
												});

											}
										});
									}else{
										activityCashier.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												ArrayAdapter<String> ad = new ArrayAdapter<String>(activityCashier, R.layout.table_layout,R.id.table_num,(String[])p.getData());
												activityCashier.gv.setAdapter(ad);
												activityCashier.gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
													@Override
													public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
														Intent intent = new Intent(activityCashier, CashierMenuListActivity.class);
														int tablenum = Integer.parseInt(((TextView)view.findViewById(R.id.table_num)).getText().toString());
														intent.putExtra("table",tablenum);
														if(AsynchronousOrdersByTable.ordersbyTable.keySet().contains(tablenum)){
															intent.putExtra("tableorder",AsynchronousOrdersByTable.ordersbyTable.get(tablenum));
														}
														activityCashier.startActivity(intent);
													}
												});
											}
										});
									}

									break;
								case REPLY_CONNECTED_WAITERS:
									activityCashierCloseWaiter.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											ArrayList<String> waiters = (ArrayList<String>) p.getData();
											ArrayAdapter arrayAdapter = new ArrayAdapter(CashierCloseWaiterListActivity.thiss,R.layout.list_view_row_layout,R.id.list_view_row_text_view,waiters);
											CashierCloseWaiterListActivity.lv.setAdapter(arrayAdapter);
											CashierCloseWaiterListActivity.progress.setVisibility(View.INVISIBLE);
										}
									});

									break;
								case ORDER_EDITING_APPROVED:
									System.out.println(AsynchronousOrdersByTable.isRequestingEditingRights);
									if(AsynchronousOrdersByTable.isRequestingEditingRights) {
										System.out.println("APPROVED");
										WaiterMenuListActivity.thiss.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												WaiterMenuListActivity.turnOnEditingRights();
											}
										});

										if (isWaiter) {
											Snackbar.make(WaiterMenuListActivity.tabs.getRootView(), "Editing rights approved, and will " +
													"expire in 5 seconds", Snackbar.LENGTH_LONG).show();
										} else {
											Snackbar.make(CashierMenuListActivity.tabs.getRootView(), "Editing rights approved, and will " +
													"expire in 5 seconds", Snackbar.LENGTH_LONG).show();
										}
										AsynchronousOrdersByTable.isRequestingEditingRights = false;
									}
									break;
								case ORDER_EDITING_DENIED:
									System.out.println(AsynchronousOrdersByTable.isRequestingEditingRights);
									if(AsynchronousOrdersByTable.isRequestingEditingRights) {

										System.out.println("DENIED");
										if (isWaiter) {
											Snackbar.make(WaiterMenuListActivity.tabs.getRootView(), "Editing rights denied", Snackbar.LENGTH_LONG).show();
										} else {
											Snackbar.make(CashierMenuListActivity.tabs.getRootView(), "Editing rights denied", Snackbar.LENGTH_LONG).show();
										}
										AsynchronousOrdersByTable.isRequestingEditingRights = false;
									}
									break;


							}
						}
						else if(o instanceof RequestPacket){
							final RequestPacket rp = (RequestPacket) o;
							final User u = (User) rp.getData();
							final Activity activity = activityCashier!=null?activityCashier:activityCashierMenuList;
							activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(activity)
                                    .setTitle("Confirm Edit")
                                    .setMessage(u.getUsername() +" is trying to edit an order, confirm?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            try {
                                                ReplyPacket rep = new ReplyPacket(rp.getDestinationIP().toString(),InetAddress.getByName(rp.getSourceIP()),
                                                        User.CASHIER,rp.getSourceUserType(),Flags.ORDER_EDITING_APPROVED);
                                                PacketSender ps = new PacketSender(rep);
                                                ps.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                            } catch (UnknownHostException e) {
                                                e.printStackTrace();
                                            }
                                        }})
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            try {
                                                ReplyPacket rep = new ReplyPacket(rp.getDestinationIP().toString(),InetAddress.getByName(rp.getSourceIP()),
                                                        User.CASHIER,rp.getSourceUserType(),Flags.ORDER_EDITING_DENIED);
                                                PacketSender ps = new PacketSender(rep);
                                                ps.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                            } catch (UnknownHostException e) {
                                                e.printStackTrace();
                                            }
                                        }}).show();
                                }
                            });

						}
					}
					//TODO
					
				} catch (IOException e) {	
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}while(true);
		}
	}
	
	public static void reportAccessApproved(ReplyPacket p)throws IOException {
		

		
	}
	
	public static void reportAccessDenied(RequestPacket p) throws IOException {

	}

	@Override
	protected void onCancelled() {
		ss.close();
		super.onCancelled();
	}

	public void setActivity(CashierMainScreenActivity activity){
		activityCashier=activity;
		activityWaiter=null;
		activityCashierCloseWaiter=null;
		activityCashierMenuList=null;
		isWaiter=false;
	}
	public void setActivity(WaiterMainScreenActivity activity){
		activityWaiter=activity;
		activityCashier=null;
		activityCashierCloseWaiter=null;
		activityCashierMenuList=null;
		isWaiter=true;
	}
	public void setActivity(CashierCloseWaiterListActivity activity){
		activityCashierCloseWaiter=activity;
		activityCashier=null;
		activityWaiter=null;
		activityCashierMenuList=null;
		isWaiter=false;
	}
	public void setActivity(CashierMenuListActivity activity){
		activityCashierMenuList=activity;
		activityCashierCloseWaiter=null;
		activityCashier=null;
		activityWaiter=null;
		isWaiter=false;
	}
}
