package Asynchronous;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;

import com.Semaan.managenet.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import Packets.DataPacket;
import Packets.Flags;
import Packets.Packet;
import Packets.PayPacket;
import Packets.Ports;
import Packets.ReplyPacket;
import Packets.RequestPacket;
import Packets.SQLPacket;
import shared.User;

import static android.content.Context.WIFI_SERVICE;

public class PacketSender extends AsyncTask implements Flags, Ports {

    private RequestPacket requestPacket;
    private DataPacket dataPacket;
    private SQLPacket sqlpacket;
    private PayPacket payPacket;
    private ReplyPacket replyPacket;
    private int port = REQUEST_PORT;

    private InetAddress ipAddress  = MainActivity.getServerAddres();

    InetAddress destination;
    int user;
    int flag;
    String source;

    public PacketSender(String source,InetAddress destination,int user,int flag){
        System.setProperty("java.net.preferIPv4Stack" , "true");
        this.destination=destination;
        this.user=user;
        this.flag=flag;
        System.out.println("REQUESTPACKET PROCESS STARTED");
    }

    public PacketSender(RequestPacket packet){
        requestPacket=packet;
    }

    public PacketSender(DataPacket packet){
        dataPacket=packet;
    }

    public PacketSender(SQLPacket packet){
        sqlpacket=packet;
    }

    public PacketSender(PayPacket packet){payPacket=packet;}

    public PacketSender(ReplyPacket packet){replyPacket=packet;}

    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            System.out.println("REQUESTPACKET CREATED");
            System.out.println(source + " :::::: "+InetAddress.getByName(source));

            Object o=null;
            if(dataPacket!=null){
                o=dataPacket;
            }
            else if(replyPacket!=null){
                o=replyPacket;
                ipAddress = replyPacket.getDestinationIP();
                port=USER_REQUEST_PORT;

            }
            else if(sqlpacket!=null){
                o=sqlpacket;
            }
            else if(payPacket!=null) {
                o=payPacket;
            }
            else if(requestPacket!=null){
                o=requestPacket;
                if(requestPacket.getRequest()==Flags.ORDER_EDITING_REQUEST)
                    AsynchronousOrdersByTable.isRequestingEditingRights=true;
                System.out.println(AsynchronousOrdersByTable.isRequestingEditingRights);
            }else{
                requestPacket=new RequestPacket(getIPAddress(true),destination, user,User.SERVER, flag);
                o=requestPacket;
                if(requestPacket.getRequest()==Flags.ORDER_EDITING_REQUEST)
                    AsynchronousOrdersByTable.isRequestingEditingRights=true;
                System.out.println(AsynchronousOrdersByTable.isRequestingEditingRights);
            }

            DatagramSocket socket = new DatagramSocket();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.flush();
            byte[] buffer = baos.toByteArray();
            System.out.println("ATTEMPTING TO SEDN REQUESTPACKET");
            DatagramPacket packet2
                    = new DatagramPacket(buffer, buffer.length, ipAddress, port);
            socket.send(packet2);
            System.out.println("REQUESTPACKET SENT "+packet2.toString() + " " + packet2.getAddress() + " " + packet2.getPort()+"  "+ "FROM" +packet2.getAddress());
            socket.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
}
