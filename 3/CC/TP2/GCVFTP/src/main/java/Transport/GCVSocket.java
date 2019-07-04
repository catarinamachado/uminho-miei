package Transport;

import Common.Debugger;
import Transport.CongestionControl.WindowRateControl;
import Transport.Listener.ListenerGate;
import Transport.Listener.ListenerProperties;
import Transport.Speaker.SpeakerGate;
import Transport.Speaker.SpeakerProperties;
import Transport.Unit.ControlPacket;
import Transport.Unit.ControlPacketTypes.HI;
import Transport.Unit.Packet;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class GCVSocket {

    private static ConnectionScheduler common_daemon=null;

    private static void activate(){
        if( common_daemon == null){
            try {
                GCVSocket.common_daemon = new ConnectionScheduler(
                        GCVConnection.port,
                        GCVConnection.connection_receive_ttl,
                        GCVConnection.stdmtu);

            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    private static void announceSocketConnection(String key, GCVSocket cs){
        if( common_daemon != null)
            GCVSocket.common_daemon.announceConnection(key,cs);
    }

    private static void closeSocketConnection(String key){
        if( common_daemon != null)
            GCVSocket.common_daemon.closeConnection(key);
    }

    private static ConnectionScheduler.StampedControlPacket getStampedPacketByPort(int port) throws InterruptedException{
        return GCVSocket.common_daemon.getStampedByPort(port);
    }

    private static ConnectionScheduler.StampedControlPacket getStampedPacket( String ip) throws InterruptedException{
        return GCVSocket.common_daemon.getStamped(ip);
    }

    public static void closeConnection(){
        GCVSocket.common_daemon.close();
        GCVSocket.common_daemon = null;
    }

    private Executor actuary;
    private boolean persistent = true;

    private final AtomicBoolean active = new AtomicBoolean(false);

    private final int mtu = GCVConnection.stdmtu;
    private int maxWindow = GCVConnection.send_buffer_size;
    private InetAddress localhost;
    private final int port;
    private boolean direct = false;
    private HI hi;

    private TransmissionTransportChannel channel ;

    public GCVSocket(int port) throws IOException {
        GCVSocket.activate();
        Debugger.log("GCVSocket created");
        this.localhost = InetAddress.getLocalHost();
        this.port = port;
        //
    }

    public GCVSocket(int maxWindow, boolean persistent, int port) throws IOException {
        GCVSocket.activate();
        this.localhost = InetAddress.getLocalHost();
        this.persistent = persistent;
        this.maxWindow = maxWindow;
        this.port = port;
    }

    private GCVSocket(int maxWindow,
                      boolean persistent,
                      int port,
                      InetAddress localhost,
                      TransmissionTransportChannel channel,
                      SpeakerProperties senderProp,
                      ListenerProperties receiveProp,
                      int theirSeq,
                      int ourSeq,
                      int time,
                      String socketkey) throws IOException
    {
        this.maxWindow = maxWindow;
        this.persistent = persistent;
        this.port = port;
        this.channel = channel;
        this.localhost = localhost;
        this.direct = true;

        this.boot(senderProp,receiveProp,theirSeq,ourSeq,time);
        GCVSocket.announceSocketConnection(socketkey, this);
    }

    private void boot(SpeakerProperties me, ListenerProperties caller, int their_seq, int our_seq, int time) throws IOException{
        this.active.set(true);

        SpeakerGate sgate = new SpeakerGate(me,channel,our_seq,GCVConnection.rate_control_interval/1000);
        ListenerGate rgate = new ListenerGate(caller,channel,their_seq);

        sgate.confirmHandshake();

        channel.window().boot( their_seq,our_seq, time);

        this.actuary = new Executor(sgate, rgate, channel.window());

        for(int i = 0; i< GCVConnection.number_of_executor_workers ; i++) {
            Thread worker = new Thread(this.actuary);
            worker.start();
        }

        Debugger.log(":."+ their_seq + " : " +our_seq);
    }

    public GCVSocket listen() throws InterruptedException, IOException{

        ConnectionScheduler.StampedControlPacket receivedStampedPacket =
                GCVSocket.getStampedPacketByPort(this.port);

        HI hiPacket = receivedStampedPacket.get();/*waiting for datagram*/

        InetSocketAddress sa = new InetSocketAddress(0);

        //WindowRateControl channelWindow = new WindowRateControl(hiPacket.getMaxWindow());

        SpeakerProperties senderProp = new SpeakerProperties(
                this.localhost,
                sa.getPort(),
                this.mtu,
                hiPacket.getMaxWindow(),
                persistent);

        ListenerProperties receiveProp = new ListenerProperties(
                receivedStampedPacket.ip(),
                receivedStampedPacket.port(),
                hiPacket.getMTU(),
                this.maxWindow);

        Debugger.log(" : " + receivedStampedPacket.ip() + " : " + receivedStampedPacket.port());

        TransmissionTransportChannel tmpChannel = new TransmissionTransportChannel(
                senderProp ,
                receiveProp,
                new WindowRateControl(hiPacket.getMaxWindow(),this.persistent)

        );

        this.hi = new HI(
                (short)0,
                mtu,
                maxWindow
        );

        tmpChannel.sendPacket( this.hi );

        int responseTime = tmpChannel.window().connectionTime();

        return new GCVSocket(
                this.maxWindow,
                this.persistent,
                this.port,
                this.localhost,
                tmpChannel,
                senderProp,
                receiveProp,
                hiPacket.getSeq(),
                this.hi.getSeq(),
                responseTime,
                receivedStampedPacket.ip().toString() + receivedStampedPacket.port()
        );

    }

    public void connect(String ip) throws IOException, TimeoutException {

        this.connect(InetAddress.getByName(ip));

    }

    public void connect(InetAddress ip) throws IOException, TimeoutException {

        this.hi = new HI(
                (short)0,
                mtu,
                maxWindow
        );

        byte[] serializedHiPacket = this.hi.markedSerialize();

        DatagramSocket cs = new DatagramSocket(this.port);
        cs.setSoTimeout(GCVConnection.request_retry_timeout);

        for(int tries = 0; tries < GCVConnection.request_retry_number; tries++ ) {

            Debugger.log("sent " + serializedHiPacket.length + " bytes");

            cs.send(new DatagramPacket(
                        serializedHiPacket,
                        0,
                        serializedHiPacket.length,
                        ip,
                        GCVConnection.port)
            );
            try {
                Debugger.log(":localport " + cs.getLocalPort() );

                ConnectionScheduler.StampedControlPacket receivedStampedPacket =
                        GCVSocket.getStampedPacket(ip.toString());

                ControlPacket cdu = receivedStampedPacket.get();

                this.connectBoot(cs, cdu, ip, receivedStampedPacket.port(), this.hi);

                return;

            }catch (SocketTimeoutException|StreamCorruptedException|InterruptedException|NullPointerException ste){
                ;// espera por outro.
            }
        }

        throw new TimeoutException();

    }

    public void connect(String ip, int targetPort) throws IOException, TimeoutException {

        this.connect(InetAddress.getByName(ip),targetPort);

    }

    public void connect(InetAddress ip, int targetPort) throws IOException, TimeoutException {

        this.hi = new HI(
                targetPort,
                mtu,
                maxWindow
        );

        byte[] serializedHiPacket = this.hi.markedSerialize();

        DatagramPacket responseDatagram = new DatagramPacket(
                new byte[serializedHiPacket.length],
                serializedHiPacket.length);

        DatagramSocket cs = new DatagramSocket(this.port);
        cs.setSoTimeout(GCVConnection.request_retry_timeout);

        for(int tries = 0; tries < GCVConnection.request_retry_number; tries++ ) {

            Debugger.log("sent " + serializedHiPacket.length + " bytes");
            cs.send(new DatagramPacket(
                    serializedHiPacket,
                    0,
                    serializedHiPacket.length,
                    ip,
                    GCVConnection.port)
            );
            try {
                Debugger.log(":localport " + cs.getLocalPort() );
                cs.receive(responseDatagram);
                Debugger.log(responseDatagram.getPort() + " : ");
                Packet du = Packet.parse(responseDatagram.getData(),responseDatagram.getLength());
                Debugger.log("parsed with sucess");
                if(du instanceof ControlPacket){
                    ControlPacket cdu = (ControlPacket)du;

                    this.connectBoot(cs, cdu, ip, responseDatagram.getPort(), this.hi);
                    return;
                }

            }catch (SocketTimeoutException|StreamCorruptedException ste){
                ;// espera por outro.
                Debugger.log(" ################################## corrrrrrrrrrrrrrrupted");
            }
        }

        throw new TimeoutException();

    }

    private void connectBoot(DatagramSocket cs ,ControlPacket cdu, InetAddress ip, int port, HI hiPacket ) throws IOException{
        if( cdu instanceof HI ){
            HI response_hello_packet = (HI)cdu;
            ListenerProperties receiveProp= new ListenerProperties(
                    ip,
                    port,
                    response_hello_packet.getMTU(),
                    this.maxWindow
            );

            SpeakerProperties sendProp = new SpeakerProperties(
                    this.localhost,
                    this.port,
                    mtu,
                    response_hello_packet.getMaxWindow(),
                    persistent);

            this.channel = new TransmissionTransportChannel(cs,
                    sendProp ,
                    receiveProp,
                    new WindowRateControl(maxWindow,this.persistent)
            );

            this.boot(sendProp,receiveProp, response_hello_packet.getSeq(), hiPacket.getSeq(),0);

            GCVSocket.announceSocketConnection(ip.toString() + port, this);
            return;
        }
    }

    public void close() throws IOException{
        Debugger.log("GCVSocket closed");
        if( !this.actuary.hasTerminated() )
            this.actuary.terminate();

        GCVSocket.closeSocketConnection(
                    this.channel.getOtherStationProperties().ip().toString()
                            + this.channel.getOtherStationProperties().port());

        this.channel.close();
    }

    public void send( byte[] data) throws IOException, InterruptedException {
        if(this.actuary.hasTerminated())
            throw new IOException("GCVSocket has disconnected");

        this.actuary.send(data);
    }

    public void send( InputStream io ) throws  IOException{
        if(this.actuary.hasTerminated())
            throw new IOException("GCVSocket has disconnected");

        this.actuary.send(io);
    }

    public void sendWhenReady( InputStream io ) throws  IOException, InterruptedException{
        if(this.actuary.hasTerminated())
            throw new IOException("GCVSocket has disconnected");

        this.actuary.sendWhenReady(io);
    }

    public OutputStream send() throws  IOException{
        if(this.actuary.hasTerminated())
            throw new IOException("GCVSocket has disconnected");

        PipedOutputStream producer = new PipedOutputStream();
        PipedInputStream consumer = new PipedInputStream(producer);
        this.actuary.send(consumer);
        return producer;
    }

    public byte[] receive() throws InterruptedException {
        return this.actuary.getStream();
    }

    void restart() throws IOException {

        if( this.direct ){
            this.channel.sendPacket(this.hi);
        }else {
            this.channel.adhoc(new DatagramPacket(
                    this.hi.markedSerialize(),
                    0,
                    this.hi.markedSerialize().length,
                    this.channel.getOtherStationProperties().ip(),
                    GCVConnection.port));
        }

    }

}
