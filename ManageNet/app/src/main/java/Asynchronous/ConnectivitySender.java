package Asynchronous;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import Packets.ConnectivityOperation;
import Packets.Flags;
import Packets.Ports;
import shared.User;

public class ConnectivitySender extends AsyncTask implements Flags, Ports {

    private ConnectivityOperation co;
    private InetAddress address;

    public ConnectivitySender(ConnectivityOperation co, InetAddress address) throws IOException {
        this.address=address;
        this.co=co;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            DatagramSocket socket = new DatagramSocket();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(co);
            oos.flush();
            byte[] buffer = baos.toByteArray();
            System.out.println("ATTEMPTING TO SEND CONNECTIVITY PACKET");
            DatagramPacket packet2
                    = new DatagramPacket(buffer, buffer.length, address, CONNECTIVITY_PORT);
            socket.send(packet2);
            System.out.println("CONNECTIVITY PACKET SENT " + packet2.toString() + " " + packet2.getAddress() + " " + packet2.getPort() + "  " + "FROM" + packet2.getAddress());
            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
