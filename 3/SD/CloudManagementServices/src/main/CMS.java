package main;

import main.exception.*;
import main.server.imp.Notification;
import main.server.imp.scheduler.exceptions.RequestNotFoundException;

import java.util.List;

import java.io.IOException;

public interface CMS {
    void login(String email, String password) throws InvalidUserCredentialsException, IOException;

    void logout() throws UserNotSignedException, IOException;

    void register(String email, String password) throws UserAlreadyExistsException, IOException;

    void reserve(Integer serverTypeId) throws UserNotSignedException, InvalidMachineTypeException, IOException ;

    void bid(Integer serverTypeId, Double value) throws UserNotSignedException, InvalidMachineTypeException, IOException;

    void release(Long requestId) throws UserNotSignedException, RequestNotFoundException, IOException;

    void balance() throws UserNotSignedException, IOException;

    void listActiveMachines() throws UserNotSignedException, IOException;

    void listActiveBids() throws UserNotSignedException, IOException;

    void listActiveReservationRequests() throws UserNotSignedException, IOException;

    Notification getNotification()  throws UserNotSignedException, InterruptedException, IOException;

    List<Notification> getNotifications() throws UserNotSignedException,IOException;

}
