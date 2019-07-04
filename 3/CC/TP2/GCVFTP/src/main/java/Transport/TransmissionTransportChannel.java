package Transport;

import Common.Debugger;
import Transport.CongestionControl.WindowRateControl;
import Transport.UDPTransport.TransmissionChannel;
import Transport.Unit.ControlPacketTypes.NOPE;
import Transport.Unit.ControlPacketTypes.OK;
import Transport.Unit.ControlPacketTypes.SURE;
import Transport.Unit.Packet;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class TransmissionTransportChannel extends TransmissionChannel implements TransportChannel {

    private Window window;

    public TransmissionTransportChannel(
            TransportStationProperties send,
            TransportStationProperties receive,
            WindowRateControl window) throws SocketException {
        super(send,receive);
        this.window = window;
    }

    public TransmissionTransportChannel(DatagramSocket cs,
                                        TransportStationProperties send,
                                        TransportStationProperties receive,
                                        WindowRateControl window
                                        ) throws SocketException {
        super(cs,send,receive);
        this.window = window;
    }

    public void sendPacket(Packet p) throws IOException {
        this.window.sentTransmission();

        if( p instanceof SURE){
            this.window.sentSure((SURE)p);
        }else if( p instanceof NOPE){
            this.window.sentNope((NOPE)p);
        }else if( p instanceof OK){
            this.window.sentOk((OK)p);
        }

        this.send(p.markedSerialize());
    }

    public Packet receivePacket() throws IOException {

        try {
            Packet p = Packet.parse(super.receive());

            if (p instanceof SURE) {
                this.window.receivedSure((SURE) p);
            } else if (p instanceof NOPE) {
                this.window.receivedNope((NOPE) p);
            } else if (p instanceof OK) {
                this.window.receivedOk((OK) p);
            }

            this.window.receivedTransmission();
            return p;
        }catch (StreamCorruptedException e){
            Debugger.log("DROP#################################");
            return this.receivePacket();
        }
    }

    public Window window() {
        return this.window;
    }

    public int inMTU(){
        return this.getinStationProperties().mtu();
    }

    public int outMTU(){
        return this.getoutStationProperties().mtu();
    }

    public TransportStationProperties getSelfStationProperties(){
        return (TransportStationProperties)this.getinStationProperties();
    }

    public TransportStationProperties getOtherStationProperties(){
        return (TransportStationProperties)this.getoutStationProperties();
    }

}
