package Transport.UDPTransport;

import Common.Debugger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class TransmissionChannel implements Channel {
    private final DatagramSocket cs;
    private final StationProperties in;
    private final StationProperties out;
    private final byte[] data_buffer;
    private boolean connected =false;

    public TransmissionChannel(StationProperties send,
                               StationProperties receive) throws SocketException {
        this.cs = new DatagramSocket(send.port());
        this.cs.setSendBufferSize(send.channelBufferSize());
        this.cs.setReceiveBufferSize(receive.channelBufferSize());
        //this.cs.connect(receive.ip(), receive.port());
        this.in = send;
        this.out = receive;
        this.data_buffer = new byte[in.mtu()];
    }

    public TransmissionChannel(DatagramSocket cs,
                               StationProperties send,
                               StationProperties receive) throws SocketException {
        this.cs = cs;
        this.cs.setSoTimeout(0);
        this.cs.setSendBufferSize(send.channelBufferSize());
        this.cs.setReceiveBufferSize(receive.channelBufferSize());


        Debugger.log(" - " + receive.ip()+ " , " + receive.port() + ":::- ");
        //this.cs.connect(receive.ip(), receive.port());
        this.in = send;
        this.out = receive;
        this.data_buffer = new byte[in.mtu()];
    }

    public void send(byte[] data) throws IOException {
        int sz = data.length;

        if (data.length > out.mtu())
            sz = out.mtu();

        cs.send(new DatagramPacket(data, 0, sz, out.ip(), out.port()));
    }

    public void adhoc(DatagramPacket packet) throws IOException{
        this.cs.send(packet);
    }

    public byte[] receive() throws IOException {
        DatagramPacket packet = new DatagramPacket(this.data_buffer, in.mtu());

        if( !this.connected ) {
            do {
                cs.receive(packet);
            } while (packet.getPort() != this.out.port());
        }else{
            cs.receive(packet);
        }

        this.connected = true;
        this.cs.connect(this.out.ip(), this.out.port());

        byte[] dest = new byte[packet.getLength()];

        ByteBuffer.wrap(packet.getData()).get(dest, 0, packet.getLength());

        return dest;
    }

    public void close() {
        if (!this.cs.isClosed()) {
            this.cs.disconnect();
            this.cs.close();
        }
    }

    public StationProperties getinStationProperties() {
        return in;
    }

    public StationProperties getoutStationProperties() {
        return out;
    }

}
