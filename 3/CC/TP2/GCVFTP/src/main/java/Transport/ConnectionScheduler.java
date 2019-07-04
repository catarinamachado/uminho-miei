package Transport;

import Common.Debugger;
import Transport.Unit.ControlPacket;
import Transport.Unit.ControlPacketTypes.HI;
import Transport.Unit.Packet;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class ConnectionScheduler implements Runnable {

    private final DatagramSocket connection;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final Timer alarm = new Timer(true);
    private final ConcurrentHashMap<String, GCVSocket> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BlockingQueue<StampedControlPacket>> requests = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, BlockingQueue<StampedControlPacket>> listening = new ConcurrentHashMap<>();
    private LocalDateTime clearTime = LocalDateTime.now();
    private int maxPacket;

    ConnectionScheduler(int port,
                        long connection_request_ttl,
                        int maxPacket)
            throws SocketException {

        this.connection = new DatagramSocket(port);
        this.maxPacket = maxPacket;

        new Thread(this).start();

        Debugger.log(this.connection.getLocalPort() + "::" + this.connection.getLocalAddress());
        this.alarm.scheduleAtFixedRate(
                new RemoveExpired(),
                0,
                connection_request_ttl);
    }

    ConnectionScheduler.StampedControlPacket getStamped(String ip)
            throws InterruptedException {
        Debugger.log("connect");
        if (requests.containsKey(ip))
            return requests.get(ip).poll(GCVConnection.request_retry_timeout, TimeUnit.MILLISECONDS);
        else {
            LinkedBlockingQueue<StampedControlPacket> q = new LinkedBlockingQueue<>();
            requests.put(ip, q);
            return q.poll(GCVConnection.request_retry_timeout, TimeUnit.MILLISECONDS);
        }

    }

    ConnectionScheduler.StampedControlPacket getStampedByPort(int port)
            throws InterruptedException {
        Debugger.log("listening");
        if (listening.containsKey(port)) {
            return listening.get(port).take();
        } else {
            LinkedBlockingQueue<StampedControlPacket> q = new LinkedBlockingQueue<>();
            listening.put(port, q);
            return q.take();
        }

    }

    void announceConnection(String key, GCVSocket cs) {
        this.connections.put(key, cs);
    }

    private void supply(StampedControlPacket packet) throws InterruptedException {
        try {

            int port = packet.get().getPort();

            if (this.listening.containsKey(port)) {
                this.listening.get(port).put(packet);
            } else {
                LinkedBlockingQueue<StampedControlPacket> q = new LinkedBlockingQueue<>();
                q.put(packet);
                this.listening.put(port, q);
            }
            Debugger.log("TO LISTEN " + port);

        } catch (InterruptedException e) {

            if (this.requests.containsKey(packet.ip().toString())) {
                this.requests.get(packet.ip().toString()).put(packet);
            } else {
                LinkedBlockingQueue<StampedControlPacket> q = new LinkedBlockingQueue<>();
                q.put(packet);
                this.requests.put(packet.ip().toString(), q);
            }
            Debugger.log("TO CONNECT");

        }
    }

    void closeConnection(String key) {
        this.connections.remove(key);
    }

    public void run() {

        try {
            while (this.active.get()) {
                DatagramPacket packet = new DatagramPacket(new byte[this.maxPacket], this.maxPacket);
                //Debugger.log("ittt");

                try {
                    this.connection.receive(packet);

                    Packet synpacket = Packet.parse(packet.getData(),packet.getLength());

                    if (synpacket instanceof ControlPacket) {
                        //Debugger.log("valid + control");
                        ControlPacket cpacket = (ControlPacket) synpacket;
                        ControlPacket.Type packettype = cpacket.getType();

                        if (cpacket instanceof HI) {
                            Debugger.log("got " + packet.getLength() + " bytes ::-:: ip = " + packet.getAddress() + " port= " + packet.getPort());

                            if (connections.containsKey(packet.getAddress().toString() + packet.getPort())) {
                                connections.get(packet.getAddress().toString() + packet.getPort()).restart();
                            }else
                                this.supply(new StampedControlPacket((HI) cpacket, packet.getPort(), packet.getAddress()));
                        }
                    }
                } catch (StreamCorruptedException e) {
                    ;// erro no pacote ignora.
                    //Debugger.log("Packert error");
                }
            }
        } catch (InterruptedException | IOException e) {
            e.getStackTrace();
        }

    }

    void close() {
        this.active.set(false);
        this.alarm.cancel();
        this.connection.close();
    }

    private class RemoveExpired extends TimerTask {
        public void run() {

            listening.values().forEach( l -> l.removeIf(p -> p.isRecent(clearTime)));
            requests.values().forEach( l -> l.removeIf(p -> p.isRecent(clearTime)));
            clearTime = LocalDateTime.now();
        }
    }

    public class StampedControlPacket {
        private final HI obj;
        private final int port;
        private final InetAddress address;
        private final LocalDateTime t = LocalDateTime.now();

        StampedControlPacket(HI obj, int port, InetAddress address) {
            this.obj = obj;
            this.port = port;
            this.address = address;
        }

        boolean isRecent(LocalDateTime cleartime) {
            return cleartime.isAfter(t);
        }

        public HI get() {
            return this.obj;
        }

        int port() {
            return this.port;
        }

        InetAddress ip() {
            return this.address;
        }

    }
}
