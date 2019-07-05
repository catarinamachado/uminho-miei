package main.server.imp.requests;

import main.server.contract.Appropriator;
import main.server.contract.Machine;
import main.server.imp.scheduler.exceptions.RequestNotFoundException;
import main.server.imp.scheduler.types.FulfilledRequest;
import main.server.imp.scheduler.types.Request;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Receipt extends FulfilledRequest<Appropriator, Machine<String>> {


    Receipt(Ticket ticket, Machine<String> machine){
        super(ticket,machine);
    }

    public Machine<String> surrender(){
        try {
            this.getPartaker().forgo(this);
            return this.getResource();
        }catch( RequestNotFoundException e){
            return this.getResource();
        }

    }

    @Override
    public Ticket simplify(){
        return (Ticket)super.simplify();
    }

    public Integer getMachineType(){
        return this.simplify().getMachineType();
    }

    public Double getPrice(){
        return this.simplify().getPrice();
    }

    public Double getDebt(LocalDateTime end){
        long l = this.getStartTime().until(end, ChronoUnit.MILLIS);
        double f = ((double)l)/((double)60*60*1000);
        return (f * this.getPrice());
    }

}
