package App;

import Common.*;
import Model.*;
import Transport.GCVConnection;
import Transport.GCVSocket;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class Listener implements Runnable {
    private static RSAKeys rsaKeys = new RSAKeys();
    private static Map<String, User> users = new HashMap<>();
    private static SFiles files = new SFiles();
    private static List<String> peers = new ArrayList<>();

    private GCVSocket cs;
    private Encryption encryption;
    private String path = GlobalVariables.filesPath;

    public Listener(GCVSocket cs) {
        this.cs = cs;
        encryption = new Encryption();
        encryption.generate();
        Debugger.logAppLevel("AES key generated");
    }

    public static RSAKeys getRsaKeys() {
        return rsaKeys;
    }

    public static List<String> getPeers() {
        return peers;
    }

    public void run() {
        Debugger.logAppLevel("Client connected");
        PublicKey publicKey;
        String firstPacket = Connection.receive(cs);

        if(firstPacket.contains("~~~")) {
            Debugger.logAppLevel("New peer registered");
            peers.add(firstPacket.substring(3));
            return;
        } else {
            publicKey = RSAKeys.loadPublicKey(firstPacket);
            Debugger.logAppLevel("Got client RSA PublicKey");
        }

        String mpk = RSAKeys.getPublicKey(rsaKeys.getPublicKey());
        String aes = Base64.getEncoder().encodeToString(RSAKeys.encrypt(publicKey, encryption.getSecretKey().getBytes()));
        Connection.send(cs, (mpk + "'~'" + aes).getBytes());
        Debugger.logAppLevel("Sent RSA PublicKey and AES key");

        login(encryption.decrypt(Connection.receive(cs)));
        loop(publicKey);
    }

    private void login(String s) {
        String[] ss = s.split("//");
        String username = ss[0];
        String password = ss[1];//

        if (users.containsKey(username)) {
            User u = users.get(username);
            if (u.getPassword().equals(password)) {
                path += username + "/";
            }
        } else {
            users.put(username, new User(username, password));
            path += username + "/";
            File directory = new File(path);
            if (!directory.exists()){
                directory.mkdirs();
            }
        }
    }

    private void loop(PublicKey publicKey) {
        while (true) {
            Debugger.logAppLevel("Receiving loop");
            Packet packet = Packet.fromString(encryption.decrypt(Connection.receive(cs)));
            Debugger.logAppLevel("Packet received");

            if (packet.getConnectionType().equals(ConnectionType.PUT)) {
                if (RSAKeys.verifySignature(publicKey, packet.gettFiles().toString(), packet.getDigitalSignature())) {
                    addToFiles(Helper.decryptAndPut(path, packet));
                } else {
                    Debugger.logAppLevel("Invalid");
                }
            } else if (packet.getConnectionType().equals(ConnectionType.GET)) {
                getFileAndSend(cs, packet);
            } else if (packet.getConnectionType().equals(ConnectionType.LIST)) {
                list();
            } else if (packet.getConnectionType().equals(ConnectionType.ASK)) {
                ask(packet);
            } else if (packet.getConnectionType().equals(ConnectionType.FRAG)) {
                frag(packet);
            } else if (packet.getConnectionType().equals(ConnectionType.SHARE)) {
                updateFiles(packet);
            }
        }
    }

    private void getFileAndSend(GCVSocket cs, Packet packet) {
        List<TFile> tFiles = new ArrayList<>();

        packet.gettFiles().forEach(f -> {
            try {
                File file = new File(path + f.getFilename());
                byte[] content = Files.readAllBytes(file.toPath());
                tFiles.add(new TFile(f.getFilename(), content));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Packet p = new Packet(ConnectionType.PUT, tFiles);
        p.setDigitalSignature(RSAKeys.sign(rsaKeys.getPrivateKey(), p.gettFiles().toString()));
        Connection.send(cs, encryption.encrypt(p.toString()).getBytes());
    }

    private void list() {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        List<TFile> tFiles = new ArrayList<>();

        if(listOfFiles == null) return;

        for(File f : listOfFiles) {
            if (f.isFile()) {
                tFiles.add(new TFile(f.getName()));
            }
        }

        Packet p = new Packet(ConnectionType.INFORM, tFiles);
        Connection.send(cs, encryption.encrypt(p.toString()).getBytes());
    }

    private void ask(Packet packet) {
        List<String> hosts = files.get(packet.gettFiles().get(0).getFilename());
        Connection.send(cs, encryption.encrypt(hosts.toString()).getBytes());
    }

    private void addToFiles(List<String> fs) {
        fs.forEach(f -> {
            try {
                files.add(f, InetAddress.getLocalHost().getHostAddress());
                tellPeersIHave(f);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        });
    }

    private void tellPeersIHave(String filename) {
        List<TFile> tFiles = Collections.singletonList(new TFile(filename));
        peers.forEach(p -> {
            try {
                GCVSocket cs = new GCVSocket(GCVConnection.send_buffer_size,true, 7220);
                cs.connect(p, 7220);
                Connection.send(cs,
                        new Packet(
                                ConnectionType.SHARE,
                                tFiles,
                                InetAddress.getLocalHost().getHostAddress()).toString().getBytes()
                );
                cs.close();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateFiles(Packet p) {
        files.add(p.gettFiles().get(0).getFilename(), p.getDigitalSignature());
    }

    private void frag(Packet packet) {
        try {
            TFile tFile = packet.gettFiles().get(0);
            File file = new File(path + tFile.getFilename());
            byte[] content = Files.readAllBytes(file.toPath());
            List<byte[]> l = Helper.divideArray(content, tFile.getTotal());

            TFile t = new TFile(tFile.getFilename(), l.get(tFile.getPart()));

            Packet p = new Packet(ConnectionType.PUT, Collections.singletonList(t));
            Connection.send(cs, encryption.encrypt(p.toString()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
