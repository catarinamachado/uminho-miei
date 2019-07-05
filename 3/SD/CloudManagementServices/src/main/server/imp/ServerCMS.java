package main.server.imp;

import main.CMS;
import main.exception.*;
import main.server.imp.scheduler.exceptions.RequestNotFoundException;

import java.util.List;

public class ServerCMS implements CMS {

    private final SharedServerState shared;
    private String email;
    private boolean active=false;
    private final RWLock rwl = new RWLock();

    public ServerCMS(SharedServerState a){
        this.shared = a;
        this.deactivate();

    }

    private void verifylogin() throws UserNotSignedException{
        this.rwl.readLock();
        try {
            if (!this.active)
                throw new UserNotSignedException();
        }finally {
            this.rwl.readUnlock();
        }
    }


    private void activate(String email){

        this.rwl.writeLock();
        try {
            this.email = email;
            this.active = true;
        }finally {
            this.rwl.writeUnlock();
        }
    }

    private void deactivate(){
        this.rwl.writeLock();
        try {
            this.email = null;
            this.active = false;
        }finally {
            this.rwl.writeUnlock();
        }
    }

    public void login(String email, String password) throws InvalidUserCredentialsException{
        this.shared.login(email,password);
        this.activate(email);

    }

    public void logout() throws UserNotSignedException{
        this.verifylogin();
        this.deactivate();
    }

    public void register(String email, String password) throws UserAlreadyExistsException{
        this.shared.register(email,password);
    }

    public void reserve(Integer serverTypeId) throws UserNotSignedException, InvalidMachineTypeException{
        this.verifylogin();

        try{
            this.shared.reserve(this.email,serverTypeId);
        }catch (UserNotFoundException e){
            throw new UserNotSignedException();
        }
    }

    public void bid(Integer serverTypeId, Double value) throws UserNotSignedException, InvalidMachineTypeException{
        this.verifylogin();
        try{
            this.shared.bid(this.email,serverTypeId,value);
        }catch (UserNotFoundException e){
            throw new UserNotSignedException();
        }

    }

    public void release(Long requestId) throws UserNotSignedException, RequestNotFoundException {
        this.verifylogin();
        try{
            this.shared.release(this.email,requestId);
        }catch (UserNotFoundException e){
            throw new UserNotSignedException();
        }
    }

    public void balance() throws UserNotSignedException{
        this.verifylogin();
        try{
            this.shared.balance(this.email);
        }catch (UserNotFoundException e){
            throw new UserNotSignedException();
        }
    }

    public void listActiveMachines() throws UserNotSignedException{
        this.verifylogin();
        try{
            this.shared.listActiveMachines(this.email);
        }catch (UserNotFoundException e){
            throw new UserNotSignedException();
        }
    }

    public void listActiveBids() throws UserNotSignedException{
        this.verifylogin();
        try{
            this.shared.listActiveBids(this.email);
        }catch (UserNotFoundException e){
            throw new UserNotSignedException();
        }
    }

    public void listActiveReservationRequests() throws UserNotSignedException{
        this.verifylogin();
        try{
            this.shared.listActiveReservationRequests(this.email);
        }catch (UserNotFoundException e){
            throw new UserNotSignedException();
        }
    }

    public Notification getNotification()  throws UserNotSignedException, InterruptedException {
        this.verifylogin();
        try{
            return this.shared.getNotification(this.email);
        }catch (UserNotFoundException e){
            throw new UserNotSignedException();
        }
    }

    public List<Notification> getNotifications() throws UserNotSignedException {
        this.verifylogin();
        try{
            return this.shared.getNotifications(this.email);
        }catch (UserNotFoundException e){
            throw new UserNotSignedException();
        }
    }

    public void stop(){
        this.shared.stop();
    }
}
