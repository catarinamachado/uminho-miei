package main.client;

public class Notifier extends Thread {
    private boolean active;
    private NotificationGetter notificationGetter;

    Notifier(NotificationGetter notificationGetter) {
        this.active = true;
        this.notificationGetter = notificationGetter;
    }

    void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void run() {
        while (true) {
            if (active) {
                notificationGetter.printNotifications(false);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
