package main.server;

import main.server.imp.SharedServerState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Server {
    private static final Map<Long, Worker> workers = new HashMap<>();

    public static void start(Integer port, int employees, int instancespertype) {
        try {
            System.out.println("Server is starting...");
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is up at " + serverSocket.getLocalSocketAddress());
            Map<String,Double> machinetypes = new HashMap<>();
            machinetypes.put("Gpu.large12GB",4.0);
            machinetypes.put("tpu.large32GB",16.0);
            machinetypes.put("cpu.smal10GB",8.0);

            List<List<String>> list = new ArrayList<>();
            for(String a : machinetypes.keySet()){
                List<String> h = new ArrayList<>();
                for( int i = 0; i< instancespertype; i++){
                    h.add(a + i);/* atribuir nomes às instâncias */
                }
                list.add(h);
            }
            SharedServerState t = new SharedServerState(machinetypes,list,employees);

            ///*
            while (!serverSocket.isClosed()) {
                Socket connectionSocket = serverSocket.accept();
                Worker worker = new Worker(connectionSocket, t);
                worker.start();
                workers.put(worker.getId(), worker);
            }
            //*/


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            System.out.println(e.toString());
        }
        System.out.println("Terminate");

    }

    public static synchronized Worker getWorker(Thread thread) {
        return workers.get(thread.getId());
    }

    public static void main(String[] args){
        Server.start(Integer.valueOf(args[0]),Integer.valueOf(args[1]),Integer.valueOf(args[2]));
    }
}
