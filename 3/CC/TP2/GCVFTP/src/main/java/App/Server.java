package App;

import Common.Debugger;
import Common.GlobalVariables;
import Model.RSAKeys;
import Transport.GCVConnection;
import Transport.GCVSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Server implements Runnable {
    public Server(String[] args) {
        GlobalVariables.filesPath = args[2];

        List<String> peers = Listener.getPeers();
        for (int i = 3; i < args.length; i++) {
            peers.add(args[i]);
            tellPeerIExist(args[i]);
        }

        RSAKeys rsaKeys = Listener.getRsaKeys();
        rsaKeys.generate();
        Debugger.logAppLevel("RSA keys generated");
    }

    public void run() {
        while (true) {
            try {
                GCVSocket cs = new GCVSocket(GCVConnection.send_buffer_size,true, 7220);
                new Thread(new Listener(cs.listen())).start();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void tellPeerIExist(String host) {
        try {
            GCVSocket cs = new GCVSocket(GCVConnection.send_buffer_size,true, 7220);
            cs.connect(host);
            cs.send(("~~~" + InetAddress.getLocalHost().getHostAddress()).getBytes());
            cs.close();
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
