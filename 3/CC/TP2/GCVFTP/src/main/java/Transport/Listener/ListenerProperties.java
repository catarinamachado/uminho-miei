package Transport.Listener;

import Transport.TransportStationProperties;

import java.net.InetAddress;

public class ListenerProperties extends TransportStationProperties {

    public ListenerProperties(InetAddress ip, int port, int maxpacket, int buffer){
        super(ip,port,maxpacket, buffer);
    }
}
