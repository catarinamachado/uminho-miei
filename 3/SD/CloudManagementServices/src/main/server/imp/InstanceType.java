package main.server.imp;


import main.server.contract.Appropriator;
import main.server.contract.Machine;
import main.server.contract.MachineType;

import main.server.imp.requests.AuctionTicket;
import main.server.imp.requests.Receipt;
import main.server.imp.requests.ReservationTicket;
import main.server.imp.requests.Ticket;
import main.server.imp.scheduler.ResourceScheduler;
import main.server.imp.scheduler.exceptions.RequestNotFoundException;

public class InstanceType implements MachineType<Long> {

    private final Integer typeid;
    private final Double fixedprice;
    private final String name;
    private final ResourceScheduler<Appropriator,Machine<String>> auction = new ResourceScheduler<>();

    public InstanceType(Integer typeid, Double fixedprice, String name) {
        this.typeid = typeid;
        this.fixedprice = fixedprice;
        this.name = name;
    }

    public void organize(){
        this.auction.step();
    }

    public Integer key() {
        return this.typeid;
    }

    public String getName() {
        return this.name;
    }

    public void bid(Appropriator user, Double amount) {

        this.auction.request( new AuctionTicket(user,amount,this.typeid) );
        WorkLoad.queue(this.typeid);

    }

    public void reserve(Appropriator user) {

        this.auction.request(new ReservationTicket(user, this.fixedprice, this.typeid));
        WorkLoad.queue(this.typeid);

    }

    public void release(Long requestid) throws RequestNotFoundException {
        this.auction.cancel(requestid);
        WorkLoad.queue(this.typeid);

    }

    public void supply(Machine<String> machine) {

        this.auction.supply(machine);
        WorkLoad.queue(this.typeid);
    }

}