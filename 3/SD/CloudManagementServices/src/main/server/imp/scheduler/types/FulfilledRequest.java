package main.server.imp.scheduler.types;

import main.server.contract.Unique;

import java.time.LocalDateTime;

public abstract class FulfilledRequest<Partaker,Resource> implements Comparable<FulfilledRequest<Partaker,Resource>>, Unique<Long> {

    private final LocalDateTime datarecolhido = LocalDateTime.now();
    private final Request<Partaker,Resource>  r;
    private final Resource res;

    protected FulfilledRequest(Request<Partaker, Resource> req, Resource res) {
        this.r = req;
        this.res = res;
    }

    public int getPriority(){
        return r.getPriority();
    }

    public Partaker getPartaker(){ return r.getPartaker(); }

    public Long key() {
        return this.r.key();
    }

    public Request<Partaker,Resource>  simplify(){
        return this.r;
    }

    public Resource getResource(){
        return this.res;
    }


    public LocalDateTime getStartTime(){
        return this.datarecolhido;
    }

    public abstract Resource surrender();

    public int compareTo(FulfilledRequest<Partaker,Resource>  other){
        return (-1)*this.r.compareTo(other.simplify());
    }


}
