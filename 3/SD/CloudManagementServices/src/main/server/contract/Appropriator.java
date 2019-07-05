package main.server.contract;


import main.server.imp.requests.Receipt;
import main.server.imp.requests.Ticket;
import main.server.imp.scheduler.exceptions.RequestNotFoundException;


public interface Appropriator {

    void request(Ticket request );
    void acquire(Receipt receipt );
    void forgo(Receipt receiptid ) throws RequestNotFoundException;
    void abandon(Ticket request) throws RequestNotFoundException;
}