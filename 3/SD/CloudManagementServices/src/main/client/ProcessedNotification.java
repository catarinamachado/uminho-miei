package main.client;

import main.server.imp.Notification;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ProcessedNotification extends Notification {


    public ProcessedNotification(String codedmessage){
        super(codedmessage);
    }

    @Override
    public String message() {
        String codedmessage = super.message();

        try {
            String[] v = codedmessage.split(" ");
            StringBuilder message = new StringBuilder();
            for (int i = 0; i < (v.length - 2); i++) {
                message.append(v[i]).append(" ");
            }
            LocalDateTime past = LocalDateTime.parse(v[(v.length - 2)]);

            message.append(past);
            return message.toString();
        } catch (Exception e) {}

        return codedmessage;
    }

}
