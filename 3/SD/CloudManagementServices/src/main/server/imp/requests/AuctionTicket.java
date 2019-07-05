package main.server.imp.requests;

import main.server.contract.Appropriator;
import main.server.contract.Machine;
import main.server.imp.scheduler.types.Request;

public class AuctionTicket extends Ticket {


    public AuctionTicket(Appropriator acc, Double price, Integer mtype) {
        super(acc, 1,price,mtype);
        acc.request(this);

    }

    public int compareTo(Request<Appropriator,Machine<String>> other) {
        int priority = super.compareTo(other);

        if( priority == 0){

            AuctionTicket t = (AuctionTicket)other;
            int ans = (-1)*this.getPrice().compareTo(t.getPrice());
                return ans;

        }else{
            return priority;
        }


    }

}     