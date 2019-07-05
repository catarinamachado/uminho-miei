package main.server.imp.requests;

import main.server.contract.Appropriator;
import main.server.contract.Machine;

import main.server.imp.scheduler.types.Request;

public class ReservationTicket extends Ticket {

    public ReservationTicket(Appropriator acc, Double price, Integer mtype) {
        super(acc, 0,price,mtype);
        acc.request(this);
    }

    public int compareTo(Request<Appropriator,Machine<String>> other) {

        int priority = super.compareTo(other);

        if( priority == 0) {
            return this.getRequestTime().compareTo(other.getRequestTime());
        }else{
            return priority;
        }
    }
}
