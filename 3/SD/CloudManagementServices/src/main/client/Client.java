package main.client;

import jline.console.ConsoleReader;
import main.exception.*;
import main.server.imp.Notification;
import main.server.imp.scheduler.exceptions.RequestNotFoundException;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Client {
    private ClientCMS cms;

    private static NotificationGetter notificationGetter;
    private static Notifier notifier;
    private static ConsoleReader console;
    private static final Locale currentLocale = new Locale("pt", "PT");
    private static final ResourceBundle messages = ResourceBundle.getBundle("main.client.MessagesBundle", currentLocale);

    private void getServerPath(String ip, Integer port){
        cms = new ClientCMS(ip, port);
    }

    private synchronized void connect() throws IOException, InterruptedException {
        int timeout = 5;
        boolean connected = false;
        while (!connected) {
            try {
                cms.connect();
                connected = true;
            } catch (IOException e) {
                console.println(messages.getString("reconnect"));
                console.flush();
                timeout--;
                Thread.sleep(2000);
            }

            if (timeout == 0) {
                console.println(messages.getString("timeout"));
                console.flush();
                System.exit(1);
            }
        }
    }

    public static void start(String ip, Integer port) throws IOException, InterruptedException {
        console = new ConsoleReader();
        console.setPrompt("> ");
        console.clearScreen();

        notificationGetter = new NotificationGetter(ip, port, console);

        Client c = new Client();
        c.getServerPath(ip,port);
        c.connect();

        console.println(messages.getString("welcome"));

        try {
            c.commands();
        } catch (IOException e) {
            c.connect();
        }
    }

    private void authenticate(String email, boolean isRegister) throws IOException {
        console.println(messages.getString("password"));
        String password = console.readLine('*');

        cms.connect();

        try {
            if (isRegister) {
                cms.register(email,password);
            }
            cms.login(email,password);

            notificationGetter.setCredentials(email, password);
            notifier = new Notifier(notificationGetter);
            notifier.start();

            console.println(messages.getString("authenticated"));
        } catch (InvalidUserCredentialsException e) {
            console.println(messages.getString("wrongCredentials"));
        } catch (UserAlreadyExistsException e) {
            console.println(messages.getString("userExists"));
        }
    }

    private void commands() throws IOException {
        String command;

        while (true) {
            command = console.readLine();

            if (notifier != null) {
                notifier.setActive(false);
            }

            String word = command.contains(" ") ? command.split(" ")[0] : command;

            try {
                switch (word) {
                    case "help":
                        console.println(messages.getString("help"));
                        break;
                    case "login":
                        authenticate(command.split(" ")[1], false);
                        break;
                    case "register":
                        authenticate(command.split(" ")[1], true);
                        break;
                    case "logout":
                        console.println(messages.getString("logout"));
                        System.exit(0);
                        break;

                    case "balance":
                        cms.balance();
                        break;

                    case "reserve":
                        this.reservar(Integer.parseInt(command.split(" ")[1]));
                        break;
                    case "bid":
                        this.licitar(Integer.parseInt(command.split(" ")[1]),
                                Double.parseDouble(command.split(" ")[2]));
                        break;
                    case "release":
                        this.libertar(Long.parseLong(command.split(" ")[1]));
                        break;

                    case "list":
                        switch (command.split(" ")[1]) {
                            case "machines":
                                cms.listActiveMachines();
                                break;

                            case "requests":
                                cms.listActiveReservationRequests();
                                break;

                            case "bids":
                                cms.listActiveBids();
                                break;

                            default:
                                console.println(messages.getString("noExist"));
                                commands();
                        }
                        break;

                    default:
                        console.println(messages.getString("noExist"));
                        commands();
                }
            } catch (UserNotSignedException e) {
                console.println(messages.getString("noAuth"));
                commands();
            }
            catch (Exception e) {
                console.println(messages.getString("error"));
                commands();
            }

            if (word.equals("reserve") || word.equals("bid")) {
                waitForAcquire();
            }
            if (notifier != null) {
                notifier.setActive(true);
            }
        }
    }

    private void libertar(long requestId) throws IOException {
        try {
            this.cms.release(requestId);
        } catch (UserNotSignedException e) {
            console.println(messages.getString("noAuth"));
        } catch (RequestNotFoundException e) {
            console.println(messages.getString("requestNotFound"));
        }
    }

    private void licitar(int serverTypeId, double value) throws IOException {
        try {
            this.cms.bid(serverTypeId, value);
        } catch (UserNotSignedException e) {
            console.println(messages.getString("noAuth"));
        } catch (InvalidMachineTypeException e) {
            console.println(messages.getString("invalidMachineType"));
        }
    }

    private void reservar(int serverTypeId) throws IOException {
        try {
            this.cms.reserve(serverTypeId);
        } catch (UserNotSignedException e) {
            console.println(messages.getString("noAuth"));
        } catch (InvalidMachineTypeException e) {
            console.println(messages.getString("invalidMachineType"));
        }
    }

    private synchronized void waitForAcquire() {
        boolean printed = false;
        while(!printed) {
            printed = notificationGetter.printNotifications(true);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Client.start(args[0],Integer.valueOf(args[1]));
    }
}
