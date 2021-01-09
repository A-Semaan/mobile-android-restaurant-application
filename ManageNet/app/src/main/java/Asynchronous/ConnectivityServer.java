package Asynchronous;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import com.Semaan.managenet.CashierMainScreenActivity;
import com.Semaan.managenet.LoginActivity;
import com.Semaan.managenet.MainActivity;
import com.Semaan.managenet.ManagerMainScreenActivity;
import com.Semaan.managenet.WaiterMainScreenActivity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import Packets.ConnectivityOperation;
import Packets.ConnectivityReply;
import Packets.Flags;
import Packets.Ports;
import shared.AssistantManager;
import shared.Cashier;
import shared.Manager;
import shared.ServerAutomatedUser;
import shared.User;
import shared.Waiter;

public class ConnectivityServer extends AsyncTask implements Ports,Flags{

	private EditText view;

	private static DatagramSocket ss;
	
	private static HashMap<InetAddress, AssistantManager> assistantManagers = new HashMap<InetAddress, AssistantManager>();
	private static HashMap<InetAddress, Cashier> cashiers = new HashMap<InetAddress, Cashier>();
	private static HashMap<InetAddress, Waiter> waiters = new HashMap<InetAddress, Waiter>();
	private LoginActivity activity;
	
	public ConnectivityServer(LoginActivity activity, EditText view) {

		System.setProperty("java.net.preferIPv4Stack" , "true");
		this.view=view;
		this.activity=activity;
		try {
			ss = new DatagramSocket(USER_CONNECTIVITY_PORT);
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
					
					System.out.println("CONNECTIVITY SERVER DEPLOYED");
					byte[] buf = new byte[2048];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
		            ss.receive(packet);
		            System.out.println("CONNECTIVITY OBJECT RECIEVED");
		            InetAddress address = packet.getAddress();
		            int port = packet.getPort();
		            System.out.println("here1");
					
					byte[] actuals = packet.getData();
					System.out.println("here2");
					ByteArrayInputStream bis = new ByteArrayInputStream(actuals);
					System.out.println("here3");
					Object o = new ObjectInputStream(bis).readObject();
					
					System.out.println("here4");
					if(o instanceof ConnectivityReply) {
						System.out.println("here5");
						ConnectivityReply co = (ConnectivityReply)o;
						
						switch(co.getOperation()) {
						case CONNECTION_APPROVED: case CONNECTION_DENIED:
							analyseConnection(packet,co);
							break;
						case DISCONNECTION_APPROVED: case DISCONNECTION_DENIED:
							analyseDisconnection(packet,co);
							break;
						}
					}
					System.out.println("here6");
					//TODO
					
				} catch (IOException e) {	
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			}while(true);
		}
	}
	
	public void analyseConnection(DatagramPacket p, ConnectivityReply co) throws IOException {
		System.out.println("ANALYSING CONNECTION");

		if(co.getOperation()==CONNECTION_DENIED){
			System.out.println("CONNECTION DENIED");
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					activity.progressBar.setVisibility(View.INVISIBLE);
					view.setError("Username or password is incorrect");
					view.requestFocus();
				}
			});

			return;
		}
		User u=co.getUser();
		LoginActivity.loggedInUser=u;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				activity.progressBar.setVisibility(View.INVISIBLE);
			}
		});
		MainActivity.setServerAddres(p.getAddress());
		Intent intent;
		switch(u.getFlag()){
			case User.MANAGER:
			case User.ASSISTANT_MANAGER:
				intent = new Intent(activity,ManagerMainScreenActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("user",u);
				activity.startActivity(intent);
				break;
			case User.WAITER:
				intent = new Intent(activity, WaiterMainScreenActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("user",u);
				activity.startActivity(intent);
				break;
			case User.CASHIER:
				intent = new Intent(activity, CashierMainScreenActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("user",u);
				activity.startActivity(intent);
				break;
		}

	}
	public void analyseDisconnection(DatagramPacket p,ConnectivityReply co) throws IOException {
		if(LoginActivity.loggedInUser.getFlag()==User.WAITER){
			WaiterMainScreenActivity.thiss.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					WaiterMainScreenActivity.thiss.progressbar.setVisibility(View.INVISIBLE);
				}
			});
			WaiterMainScreenActivity.thiss.finish();
			AsynchronousOrdersByTable.ordersbyTable.clear();

		}else{
			CashierMainScreenActivity.thiss.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					CashierMainScreenActivity.thiss.progressbar.setVisibility(View.INVISIBLE);
				}
			});
			CashierMainScreenActivity.thiss.finish();
			AsynchronousOrdersByTable.ordersbyTable.clear();

		}
	}
	
	public static HashMap<InetAddress, AssistantManager> getAssistantManagerHashMap(){
		return assistantManagers;
	}
	
	public static HashMap<InetAddress, Cashier> getCashierHashMap(){
		return cashiers;
	}
	
	public static HashMap<InetAddress, Waiter> getWaiterHashMap(){
		return waiters;
	}
}
