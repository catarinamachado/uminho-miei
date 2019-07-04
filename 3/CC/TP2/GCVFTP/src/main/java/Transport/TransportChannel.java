package Transport;

import Transport.UDPTransport.Channel;
import Transport.Unit.Packet;

import java.io.IOException;

public interface TransportChannel extends Channel {
    void sendPacket( Packet packet) throws IOException;
    Packet receivePacket() throws IOException;
    void close();
    int inMTU();
    int outMTU();
    Window window();
}
