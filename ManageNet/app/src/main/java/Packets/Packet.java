package Packets;

import java.io.Serializable;
import java.net.InetAddress;

public abstract class Packet implements Flags,Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1573438363741100096L;
	protected String sourceIP;
    protected InetAddress destinationIP;
    protected int sourceUserType;
    protected int destinationUserType;
}
