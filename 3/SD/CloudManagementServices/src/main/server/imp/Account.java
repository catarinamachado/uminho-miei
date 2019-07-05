package main.server.imp;


import main.server.contract.Appropriator;

import main.server.contract.Unique;
import main.server.imp.requests.AuctionTicket;
import main.server.imp.requests.Receipt;
import main.server.imp.requests.ReservationTicket;
import main.server.imp.requests.Ticket;
import main.server.imp.scheduler.exceptions.RequestNotFoundException;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Account implements Unique<String>, Appropriator {
    private final String email;
    private String password;
    private Double credit;
    private final Map<Long,Receipt> machines = new HashMap<>();
    private final Map<Long, Ticket> requests = new HashMap<>();
    private final SharedQueue<Notification> notifications = new SharedQueue<>();
    private final SharedQueue<Notification> lastnotifications = new SharedQueue<>();

    private final ReentrantLock rl = new ReentrantLock();

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
        this.credit = 0.0;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        this.rl.lock();
        try{
            return this.password;
        }finally {
            this.rl.unlock();
        }
    }

    public void setPassword(String password) {
        this.rl.lock();
        try {
            this.password = password;
        }finally {
            this.rl.unlock();
        }
    }

    public Integer getRequestMType(Long requestid) throws RequestNotFoundException{
        this.rl.lock();
        try {
            if (this.requests.containsKey(requestid)) {

                Ticket t = this.requests.get(requestid);
                return t.getMachineType();

            } else if (this.machines.containsKey(requestid)) {

                Receipt r = this.machines.get(requestid);
                return r.getMachineType();
                //this.requests.remove(requestid);


            } else {

                throw new RequestNotFoundException();
            }
        }finally {
            this.rl.unlock();
        }
    }
    

    public String key() {
        return this.email;
    }

    public void request(Ticket request){
        this.rl.lock();
        try {
            this.requests.put(request.key(), request);
        }finally {
            this.rl.unlock();
        }

    }

    public String toString() {
        return this.email;
    }

    public Double getCredit() {
        this.rl.lock();
        try {
            double sum = this.credit;
            for(Receipt r : this.machines.values())
                sum += r.getDebt(LocalDateTime.now());
            return sum;
        }finally {
            this.rl.unlock();
        }
    }

    public boolean authenticate(String email, String password) {
        this.rl.lock();
        try {
            return (this.email.equals(email) && this.password.equals(password));
        }finally {
            this.rl.unlock();
        }
    }

    public void notify(Notification n) {
        this.notifications.queue(n);
        this.lastnotifications.queue(n);
    }

    public void acquire(Receipt request) {
        this.rl.lock();

        try {
            Ticket c = this.requests.remove(request.key());

            this.machines.put(request.key(), request);
            this.notify(new Notification("acquire " + this.key() + " " + request.key() + " " + request.getMachineType()));
        }finally {
            this.rl.unlock();
        }
    }

    public void forgo(Receipt request) throws RequestNotFoundException {
        this.rl.lock();
        try {
            Receipt c = this.machines.remove(request.key());
            if (c == null)
                throw new RequestNotFoundException();

            this.credit += c.getDebt(LocalDateTime.now());
            this.requests.put(request.key(),request.simplify());

            this.notify(new Notification("forgo " + this.key() + " " + request.key() + " " + c.getMachineType()));
        }finally {
            this.rl.unlock();
        }
    }

    public void abandon(Ticket request) throws RequestNotFoundException{
        this.rl.lock();
        try{
            if(this.requests.containsKey(request.key()))
                this.requests.remove(request.key());
            else
                throw new RequestNotFoundException();
        }finally {
            this.rl.unlock();
        }
    }

    public void listActiveMachines(){
        this.rl.lock();
        try {
            StringBuilder start = new StringBuilder("machines " + this.key() + " ");
            for (Receipt u : this.machines.values()) {
                start.append("| ").append(u.key()).append(" ").
                        append(u.getPrice()).append(" ").
                        append(u.getMachineType()).append(" ");

            }
            start.append("X");
            this.notify(new Notification(start.toString()));
        }finally {
            this.rl.unlock();
        }
    }

    public void listActiveBids(){
        this.rl.lock();
        try {
            StringBuilder start = new StringBuilder("bids " + this.key() + " ");
            for (Ticket u : this.requests.values()) {
                if (u instanceof AuctionTicket) {
                    start.append("| ").append(u.key()).append(" ").
                            append(u.getPrice()).
                            append(" ").append(u.getMachineType()).append(" ");
                }
            }
            start.append("X");
            this.notify(new Notification(start.toString()));
        }finally {
            this.rl.unlock();
        }
    }

    public void listActiveReservationRequests(){
        this.rl.lock();
        try {
            StringBuilder start = new StringBuilder("reservations " + this.key() + " ");
            for (Ticket u : this.requests.values()) {
                if (u instanceof ReservationTicket) {
                    start.append("| ").append(u.key()).append(" ").
                            append(u.getPrice()).
                            append(" ").append(u.getMachineType()).append(" ");
                }
            }
            start.append("X");
            this.notify(new Notification(start.toString()));
        }finally {
            this.rl.unlock();
        }
    }

    public List<Notification> recoverNotifications(){
        return this.notifications.recover();
    }

    public Notification getNotification() throws InterruptedException{
        return this.lastnotifications.waitedDequeue();
    }
}

