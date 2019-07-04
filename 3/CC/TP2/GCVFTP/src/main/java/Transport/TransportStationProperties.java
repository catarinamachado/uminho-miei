package Transport;

import Transport.UDPTransport.StationProperties;

import java.net.InetAddress;

public class TransportStationProperties extends StationProperties {

    private final int bufferSize;

    public TransportStationProperties(InetAddress ip,  int port,  int maxPacket, int bufferSize){
        super(ip,port,maxPacket, bufferSize * maxPacket);
        this.bufferSize = bufferSize;
    }

    public int transmissionChannelBufferSize(){
        return this.bufferSize;
    }


}
