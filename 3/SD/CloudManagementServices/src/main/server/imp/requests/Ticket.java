package main.server.imp.requests;

import main.server.contract.Appropriator;
import main.server.contract.Machine;
import main.server.imp.scheduler.exceptions.RequestNotFoundException;
import main.server.imp.scheduler.types.Request;

public abstract class Ticket extends Request<Appropriator, Machine<String>> {

    private final Double price;
    private final Integer mtype;

    Ticket(Appropriator acc, Integer priority , Double price, Integer mtype){
        super(acc,priority);
        this.price = price;
        this.mtype = mtype;
    }

    @Override
    public Receipt fulfill(Machine<String> r) {
        Receipt ans = new Receipt(this,r);
        this.getPartaker().acquire(ans);
        return ans;
    }

    @Override
    public void abandon(){
        try {
            this.getPartaker().abandon(this);
        }catch (RequestNotFoundException ignore){
        }
    }

    public Integer getMachineType(){ return this.mtype; }

    public Double getPrice() {
        return this.price;
    }

}
