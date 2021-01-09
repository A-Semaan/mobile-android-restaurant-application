package Asynchronous;

import android.os.AsyncTask;

import com.Semaan.managenet.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import Packets.ConnectivityOperation;
import Packets.Flags;
import Packets.Ports;
import shared.UserInfo;

public class ConnectivityBroadcaster extends AsyncTask implements Ports, Flags {

    private static String broadcastIp;
    private UserInfo userinfo;

    public ConnectivityBroadcaster(UserInfo user){
        userinfo=user;
        try {
            broadcastIp=getBroadcast();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            sendBroadcast();
        }
        catch(SocketException e){
            e.printStackTrace();
        }
        catch(IOException e ){
            e.printStackTrace();
        }

        return null;
    }
    public static String getBroadcast() throws SocketException {

        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface ni = en.nextElement();
            System.out.println(" Display Name = " + ni.getDisplayName());

            List<InterfaceAddress> list = ni.getInterfaceAddresses();
            Iterator<InterfaceAddress> it = list.iterator();

            while (it.hasNext()) {
                InterfaceAddress ia = it.next();
                if(ia.getBroadcast()==null)continue;
                System.out.println(" Broadcast = " + ia.getBroadcast());
                return (ia.getBroadcast()+"").substring(1);
            }
        }
        return null;

    }
    private void sendBroadcast() throws IOException {
        ConnectivityOperation co = new ConnectivityOperation(CONNECTION_REQUEST,userinfo);
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(co);
        oos.flush();
        byte[] buffer = baos.toByteArray();

        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(broadcastIp), CONNECTIVITY_PORT);
        socket.send(packet);
        System.out.println(packet.toString() + " " + packet.getAddress() + " " + packet.getPort());
        socket.close();
    }

}
