package App;

import Common.*;
import Model.Packet;
import Model.RSAKeys;
import Model.TFile;
import Transport.GCVConnection;
import Transport.GCVSocket;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.TimeoutException;


public class Client {
    private static RSAKeys rsaKeys = new RSAKeys();

    private GCVSocket cs;
    private PublicKey serverPublicKey;
    private Encryption encryption;
    private String username;
    private String password;

    // ARGS: username password filesPath [knownHosts...]
    public static void main(String[] args) {
        new Thread(new Server(args)).start();

        rsaKeys.generate();
        new Client(args).run();
    }

    private Client(String[] args) {
        encryption = new Encryption();
        username = args[0];
        password = args[1];
    }

    private void run() {
        String input;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean connected = false;

        while(true) {
            System.out.print("> ");

            try {
                input = br.readLine();

                if(input.equals("exit")) {
                    System.exit(0);
                } else {
                    connected = parse(connected, input);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean parse(Boolean connected, String input) {
        List<String> s = new ArrayList<>(Arrays.asList(input.split(" ")));

        if(!s.get(0).equals("connect") && !connected) {
            System.out.println("Not connected");
        } else {
            switch (s.get(0)) {
                case "connect":
                    connect(s.get(1));
                    connected = true;
                    break;
                case "put":
                    if(s.size() > 2) {
                        put(s.get(1), s.get(2));
                    } else {
                        System.out.println("error");
                    }
                    break;
                case "upload":
                    s.remove(0);
                    put(s);
                    break;
                case "get":
                    s.remove(0);
                    get(s);
                    break;
                case "list":
                    list();
                    break;
                case "download":
                    download(s.get(1));
                    break;
                case "close":
                    close();
                    break;
                default:
                    System.out.println("Unknown command");
            }
        }

        return connected;
    }

    private void connect(String host) {
        try {
            cs = new GCVSocket(GCVConnection.send_buffer_size, true, 6969);
            cs.connect(host, 7220);

            Debugger.logAppLevel("Connected to server");

            Connection.send(cs, RSAKeys.getPublicKey(rsaKeys.getPublicKey()).getBytes());
            Debugger.logAppLevel("Sent RSA PublicKey");

            String r = Connection.receive(cs);
            String[] rs = r.split("'~'");

            serverPublicKey = RSAKeys.loadPublicKey(rs[0]);
            byte[] d = RSAKeys.decrypt(rsaKeys.getPrivateKey(), Base64.getDecoder().decode(rs[1].getBytes()));
            encryption.secretKey = encryption.loadSecretKey(new String(d));
            Debugger.logAppLevel("Got Server RSA PublicKey and AES key");

            Connection.send(cs, encryption.encrypt((username + "//" + password)).getBytes());
        } catch(IOException | TimeoutException e){
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            cs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void put(String filename, String remoteName) {
        File file = new File(filename);
        List<TFile> tFiles = new ArrayList<>();

        try {
            byte[] content = Files.readAllBytes(file.toPath());
            tFiles.add(new TFile(remoteName, content));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Packet packet = new Packet(ConnectionType.PUT, tFiles);
        sendTFiles(packet);
    }

    private void put(List<String> filenames) {
        List<File> files = new ArrayList<>();
        filenames.forEach(f -> files.add(new File(f)));
        sendFiles(files);
    }

    private void sendFiles(List<File> files)  {
        try {
            List<TFile> fs = new ArrayList<>();

            for (File file : files) {
                byte[] content = Files.readAllBytes(file.toPath());
                String name = file.getName();
                fs.add(new TFile(name, content));
            }

            Packet p = new Packet(ConnectionType.PUT, fs);
            sendTFiles(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendTFiles(Packet files) {
        files.setDigitalSignature(RSAKeys.sign(rsaKeys.getPrivateKey(), files.gettFiles().toString()));
        Connection.send(cs, encryption.encrypt(files.toString()).getBytes());
    }

    private void get(List<String> ss) {
        List<TFile> t = new ArrayList<>();
        ss.forEach(s -> t.add(new TFile(s)));
        Packet p = new Packet(ConnectionType.GET, t);
        Connection.send(cs, encryption.encrypt(p.toString()).getBytes());

        Packet packet = Packet.fromString(encryption.decrypt(Connection.receive(cs)));
        if (RSAKeys.verifySignature(serverPublicKey, packet.gettFiles().toString(), packet.getDigitalSignature())) {
            Helper.decryptAndPut("", packet);
        }
    }

    private void list() {
        Packet p = new Packet(ConnectionType.LIST);
        Connection.send(cs, encryption.encrypt(p.toString()).getBytes());
        Packet packet = Packet.fromString(encryption.decrypt(Connection.receive(cs)));
        packet.gettFiles().forEach(f -> System.out.println(f.getFilename()));
    }

    private void download(String filename) {
        List<TFile> tFiles = new ArrayList<>();
        tFiles.add(new TFile(filename));
        Packet p = new Packet(ConnectionType.ASK, tFiles);
        Connection.send(cs, encryption.encrypt(p.toString()).getBytes());

        String r = encryption.decrypt(Connection.receive(cs));
        r = r.substring(1, r.length()-1);
        String[] hosts = r.split(",");

        int i = 0;
        StringBuilder finalcontent = new StringBuilder();
        for (String host : hosts) {
            System.out.println("getting frag from "  + host);
            close();
            connect(host);

            Packet send = new Packet(
                    ConnectionType.FRAG,
                    Collections.singletonList(new TFile(filename, i, hosts.length, "")));
            Connection.send(cs, encryption.encrypt(send.toString()).getBytes());

            Packet packet = Packet.fromString(encryption.decrypt(Connection.receive(cs)));
            if (RSAKeys.verifySignature(serverPublicKey, packet.gettFiles().toString(), packet.getDigitalSignature())) {
                finalcontent.append(packet.gettFiles().get(0).getFileContents());
            }
            i++;
        }
        close();
        Helper.put("", filename, finalcontent.toString());
    }
}
