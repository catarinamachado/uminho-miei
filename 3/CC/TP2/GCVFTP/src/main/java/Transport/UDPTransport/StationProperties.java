package Transport.UDPTransport;

import java.net.InetAddress;

public class StationProperties {
    private final InetAddress ip;
    private final int mtu;
    private final int bufferSize;
    private int port;

    public StationProperties(InetAddress ip, int port, int mtu, int bufferSize) {

        this.port = port;
        this.ip = ip;
        this.mtu = mtu;
        this.bufferSize = bufferSize;
    }

    public int port() {
        return port;
    }

    public InetAddress ip() {
        return ip;
    }

    public int mtu() {
        return this.mtu;
    }

    public int channelBufferSize() {
        return this.bufferSize;
    }


}
