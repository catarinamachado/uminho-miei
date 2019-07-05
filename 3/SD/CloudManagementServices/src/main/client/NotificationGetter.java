package main.client;

import jline.console.ConsoleReader;
import main.exception.InvalidUserCredentialsException;
import main.server.imp.Notification;

import java.io.IOException;
import java.net.BindException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

class NotificationGetter {
    private String email;
    private String pass;
    private ClientCMS c;
    private ConsoleReader console;
    private int lastNotifications;

    private static Locale currentLocale = new Locale("pt", "PT");
    private static ResourceBundle messages = ResourceBundle.getBundle("main.client.MessagesBundle", currentLocale);

    NotificationGetter(String ip, Integer port, ConsoleReader newConsole) {
        this.email = null;
        this.pass = null;
        this.c = new ClientCMS(ip, port);
        this.console = newConsole;
        this.lastNotifications = 0;
    }

    void setCredentials(String newEmail, String newPass) {
        this.email = newEmail;
        this.pass = newPass;
        this.lastNotifications = 0;
    }

    boolean printNotifications(boolean waiting) {
        String message;
        boolean acquire = false;

        try {
            try{
                c.connect();
            } catch (BindException ignored) { }
            c.login(email,pass);

            List<Notification> r = c.getNotifications();

            if (r.size() != lastNotifications) {
                console.println("\n-------");
            }

            for (Notification s : r.subList(lastNotifications, r.size())) {
                message = s.message();
                if (message.contains("acquire")) {
                    acquire = true;
                }
                console.println(messages.getString("notification") + message);
            }

            if (r.size() != lastNotifications) {
                lastNotifications = r.size();
                console.println("-------\n");

                if (!waiting) {
                    console.print("> ");
                }
            }

            console.flush();
            c.logout();
        } catch (IOException | InvalidUserCredentialsException e) {
            e.printStackTrace();
        }

        return acquire;
    }
}
