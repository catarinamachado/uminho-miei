package main.client;

import main.CMS;
import main.exception.*;
import main.server.imp.Notification;
import main.server.imp.scheduler.exceptions.RequestNotFoundException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientCMS implements CMS {

    private Socket s;
    private final String ip;
    private final Integer port;
    private PrintWriter pw;
    private BufferedReader reader;

    ClientCMS(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }


    public void login(String email, String password) throws InvalidUserCredentialsException, IOException {
        pw.println("login " + email + " " + password);
        pw.flush();
        String x = reader.readLine();
        if (x != null){
            String[] arg = x.split(" ");
            switch (arg[0]){
                case "0":
                    return;
                case "1":
                    throw new InvalidUserCredentialsException();
                default:
                    throw new InvalidUserCredentialsException();
            }
        }
        else
            throw new IOException();
    }

    public void logout() throws IOException{
        try{
            pw.println("quit");
            pw.flush();
        }finally {
            s.close();
        }
    }

    public void register(String email, String password) throws UserAlreadyExistsException, IOException {
        pw.println("register " + email + " " +password);
        pw.flush();

        String x = reader.readLine();
        if (x != null){
            String[] arg = x.split(" ");
            switch (arg[0]) {
                case "0":
                    return;
                case "1":
                    throw new UserAlreadyExistsException();
                default:
                    throw new UserAlreadyExistsException();
            }
        }
        else
            throw new IOException();
    }

    public void reserve(Integer serverTypeId) throws UserNotSignedException, InvalidMachineTypeException, IOException {
        pw.println("reserve " + serverTypeId);
        pw.flush();
        String x = reader.readLine();
        if (x != null){
            String[] arg = x.split(" ");
            switch (arg[0]){
                case "0":
                    return;
                case "1":
                    throw new UserNotSignedException();
                case "2":
                    throw new InvalidMachineTypeException();
                default:
                    throw new InvalidMachineTypeException();
            }
        }
        else
            throw new IOException();
    }

    public void bid(Integer serverTypeId, Double value) throws UserNotSignedException, InvalidMachineTypeException, IOException {
        pw.println("bid " + serverTypeId +" "+value);
        pw.flush();
        String x = reader.readLine();
        if (x != null){
            //Interpertar resultados
            String[] arg = x.split(" ");
            switch (arg[0]){
                case "0":
                    return;
                case "1":
                    throw new InvalidMachineTypeException();
                case "2":
                    throw new UserNotSignedException();
                default:
                    throw new InvalidMachineTypeException();
            }
        }
        else
            throw new IOException();
    }

    public void release(Long requestId) throws UserNotSignedException, RequestNotFoundException, IOException {
        pw.println("release " + requestId);
        pw.flush();
        String x = reader.readLine();
        if (x != null){
            String[] arg = x.split(" ");
            switch (arg[0]){
                case "0":
                    return;
                case "1":
                    throw new UserNotSignedException();
                case "2":
                    throw new RequestNotFoundException();
                default:
                    throw new RequestNotFoundException();
            }
        }
        else
            throw new IOException();
    }

    @Override
    public void balance() throws UserNotSignedException, IOException {
        pw.println("balance");
        push();
    }

    @Override
    public void listActiveMachines() throws UserNotSignedException, IOException {
        pw.println("activemachines");
        push();
    }

    @Override
    public void listActiveBids() throws UserNotSignedException, IOException {
        pw.println("activebids");
        push();
    }

    @Override
    public void listActiveReservationRequests() throws UserNotSignedException, IOException {
        pw.println("reservationRequests");
        push();
    }

    private void push() throws UserNotSignedException, IOException {
        pw.flush();
        String x;
        x = reader.readLine();
        if (x != null){
            System.out.println(x);
            String[] arg = x.split(" ");
            switch (arg[0]){
                case "0":
                    return;
                case "1":
                    throw new UserNotSignedException();
                default:
                    throw new IOException();
            }
        }
        else
            throw new IOException();
    }

    @Override
    public Notification getNotification() throws UserNotSignedException, InterruptedException, IOException {
        pw.println("singleNotificacao");
        pw.flush();
        String x;
        x = reader.readLine();
        if (x != null){
            String[] arg = x.split(" ");
            switch (arg[0]){
                case "0":
                    return new ProcessedNotification(x.substring(1));
                case "1":
                    throw new UserNotSignedException();
                case "2":
                    throw new InterruptedException();
                default:
                    throw new IOException();
            }
        }
        else
            throw new IOException();
    }

    @Override
    public List<Notification> getNotifications() throws IOException {
        pw.println("notificacoes");
        pw.flush();
        ArrayList<Notification> res = new ArrayList<>();
        String x;
        do{
            x = reader.readLine();
            if (x!=null && !x.startsWith("end")){
                res.add(new ProcessedNotification(x));
            }
        }while (x!=null && !x.startsWith("end"));
        return res;
    }

    void connect() throws IOException {
        this.s = new Socket(ip, port);
        pw = new PrintWriter(s.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

}
