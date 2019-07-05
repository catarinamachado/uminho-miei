package main.server;

import main.exception.InvalidMachineTypeException;
import main.exception.InvalidUserCredentialsException;
import main.exception.UserAlreadyExistsException;
import main.exception.UserNotSignedException;
import main.server.imp.Notification;
import main.server.imp.ServerCMS;
import main.server.imp.SharedServerState;
import main.server.imp.scheduler.exceptions.RequestNotFoundException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class Worker extends Thread {
    private final Socket clientSocket;
    private final ServerCMS cms;

    Worker(Socket clientSocket, SharedServerState cms) {
        this.clientSocket = clientSocket;
        this.cms = new ServerCMS(cms);
    }

    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            Scanner scanner = new Scanner(input, StandardCharsets.UTF_8);
            PrintWriter serverPrintOut = new PrintWriter(
                    new OutputStreamWriter(output, StandardCharsets.UTF_8), true);

            boolean done = false;

            while(!done && scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String result = parseAndExecute(line);

                if (result.contains("quit")) {
                    done = true;
                    serverPrintOut.println("\nGoodbye!\n");
                } else {
                    serverPrintOut.println(result);
                }

            }

            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String parseAndExecute(String line) {
        StringBuilder r = new StringBuilder("Error: Unknown command");
        String[] commands = line.toLowerCase().split(" ");

        switch (commands[0]) {
            case "":
                r = new StringBuilder();
                break;

            case "quit":
                r = new StringBuilder("quit");
                break;

            case "login":
                try {
                    cms.login(commands[1], commands[2]);
                    r = new StringBuilder("0");
                } catch (InvalidUserCredentialsException e) {
                    r = new StringBuilder("1");
                }
                break;
            case "register":
                try {
                    cms.register(commands[1], commands[2]);
                    r = new StringBuilder("0");
                } catch (UserAlreadyExistsException e) {
                    r = new StringBuilder("1");
                }
                break;
            case "reserve":
                try {
                    cms.reserve(Integer.valueOf(commands[1]));
                    r = new StringBuilder("0");
                } catch (UserNotSignedException e) {
                    r = new StringBuilder("1");
                } catch (InvalidMachineTypeException e) {
                    r = new StringBuilder("2");
                }
                break;
            case "release":
                try {
                    cms.release(Long.valueOf(commands[1]));
                    r = new StringBuilder("0");
                } catch (UserNotSignedException e) {
                    r = new StringBuilder("1");
                } catch (RequestNotFoundException e) {
                    r = new StringBuilder("2");
                }
                break;
            case "bid":
                try {
                    cms.bid(Integer.valueOf(commands[1]),Double.valueOf(commands[2]));
                    r = new StringBuilder("0");
                } catch (UserNotSignedException e) {
                    r = new StringBuilder("1");
                } catch (InvalidMachineTypeException e) {
                    r = new StringBuilder("2");
                }
                break;
            case "balance":
                try {
                    cms.balance();
                    r = new StringBuilder("0");
                } catch (UserNotSignedException e) {
                    r = new StringBuilder("1");
                }
                break;
            case "activemachines":
                try {
                    cms.listActiveMachines();
                    r = new StringBuilder("0");
                } catch (UserNotSignedException e) {
                    r = new StringBuilder("1");
                }
                break;
            case "activebids":
                try {
                    cms.listActiveBids();
                    r = new StringBuilder("0");
                } catch (UserNotSignedException e) {
                    r = new StringBuilder("1");
                }
                break;
            case "reservationrequests":
                try {
                    cms.listActiveReservationRequests();
                    r = new StringBuilder("0");
                } catch (UserNotSignedException e) {
                    r = new StringBuilder("1");
                }
                break;
            case "singlenotificacao":
                try {
                    Notification n = cms.getNotification();
                    r = new StringBuilder("0 " + n.message());

                } catch (UserNotSignedException e) {
                    r = new StringBuilder("1");
                } catch (InterruptedException e) {
                    r = new StringBuilder("2");
                }
                break;
            case "notificacoes":
                try {
                    List<Notification> n = cms.getNotifications();
                    r = new StringBuilder();
                    for (Notification not:n) {
                        r.append(not.message());
                        r.append("\n");
                    }
                    r.append("end");
                } catch (UserNotSignedException e) {
                    r = new StringBuilder("end");
                }
        }
        return r.toString();
    }
}
