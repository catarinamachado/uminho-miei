package main;

import main.client.Client;
import main.server.Server;

import java.io.*;

public class CloudManagementServices {
    private static PrintWriter writer;

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean isServer = false;
        boolean isDebug = false;

        for (String arg : args) {
            if (arg.equals("server")) {
                isServer = true;
            }
            else if (arg.equals("debug")) {
                isDebug = true;
            }
        }

        setLogDestiny(isDebug);

        if (isServer) {
            Server.start(Integer.valueOf(args[2]),Integer.valueOf(args[3]),Integer.valueOf(args[4]));
        }
        else {
            Client.start("localhost",Integer.valueOf(args[2]));
        }
    }

    private static void setLogDestiny(boolean isDebug) {
        if (isDebug) {
            writer = new PrintWriter(System.out);
            return;
        }

        try {
            File file = new File("log.txt");

            if (!file.exists()) {
                while (!file.createNewFile());
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            writer = new PrintWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String message) {
        writer.println(message);
        writer.flush();
    }

    public static void log(Exception e) {
        e.printStackTrace(writer);
        writer.flush();
    }
}
