package main.server.imp;


import main.exception.*;

import main.server.imp.scheduler.exceptions.RequestNotFoundException;


import java.util.List;
import java.util.Map;

public class SharedServerState {

    private final ConcurrentMap<String,Account> users = new ConcurrentMap<>();

    private final InstanceType[] types;

    private final ServerEmployee[] worker;

    private final int workers;

    public SharedServerState(Map<String, Double> machinetypes, List<List<String>> amount, int workers) {
        int n = machinetypes.size();
        this.types = new InstanceType[n];

        int i = 0;
        this.workers = workers;

        for (Map.Entry<String, Double> s : machinetypes.entrySet()) {

            this.types[i] = new InstanceType(i, s.getValue(), s.getKey());
            for(String a : amount.get(i)) {
                this.types[i].supply(new Instance(a));
            }

            i++;
        }

        this.worker = new ServerEmployee[this.workers];
        for(i = 0; i< this.workers; i++){
            this.worker[i] = new ServerEmployee(this.types);
            this.worker[i].start();
        }
    }

    public void login(String email, String password) throws InvalidUserCredentialsException {

        try {
            Account l = this.users.get(email);
            if (!l.authenticate(email, password))
                throw new InvalidUserCredentialsException();

        } catch (UserNotFoundException e) {
            throw new InvalidUserCredentialsException();
        }

    }

    public void register(String email, String password) throws UserAlreadyExistsException {
        Account a = new Account(email, password);
        this.users.put(email,a);

    }

    public void reserve(String email,Integer serverTypeId)
            throws UserNotFoundException, InvalidMachineTypeException {
        Account l = this.users.get(email);

        try {
            InstanceType t = this.types[serverTypeId];
            t.reserve(l);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidMachineTypeException();
        }

    }

    public void bid(String email, Integer serverTypeId, Double value)
            throws UserNotFoundException, InvalidMachineTypeException  {
        Account l = this.users.get(email);

        try {
            InstanceType t = this.types[serverTypeId];
            t.bid(l,value);

        } catch (IndexOutOfBoundsException e) {
            throw new InvalidMachineTypeException();
        }

    }

    public void release(String email,Long requestid) throws UserNotFoundException, RequestNotFoundException {
        Account l = this.users.get(email);
        Integer tp = l.getRequestMType(requestid);

        this.types[tp].release(requestid);
    }

    public void balance(String email) throws UserNotFoundException{
        Account l = this.users.get(email);

        l.notify(new Notification("balance " + l.key() + " " + l.getCredit()));
    }

    public void listActiveMachines(String email) throws UserNotFoundException{
        Account l = this.users.get(email);

        l.listActiveMachines();
    }

    public void listActiveBids(String email) throws UserNotFoundException{
        Account l = this.users.get(email);

        l.listActiveBids();
    }

    public void listActiveReservationRequests(String email) throws UserNotFoundException{
        Account l = this.users.get(email);

        l.listActiveReservationRequests();
    }

    public List<Notification> getNotifications(String email) throws UserNotFoundException{
        Account l = this.users.get(email);

        return l.recoverNotifications();
    }

    public Notification getNotification(String email)  throws UserNotFoundException, InterruptedException {
        Account l = this.users.get(email);
        return l.getNotification();
    }

    public synchronized void stop(){

        for(int i=0; i< this.workers; i++)
            this.worker[i].interrupt();
    }

}
