package main.server.imp.scheduler.types;

import main.server.contract.Unique;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;


public abstract class Request<Partaker,Resource>  implements Comparable<Request<Partaker,Resource>>, Unique<Long> {

    private final Partaker item;
    private static Long count = (long) 0;
    private static final ReentrantLock srl = new ReentrantLock();
    private final Long id;
    private final int priority;
    private final LocalDateTime datapedido = LocalDateTime.now();

    private static Long getUniqueKey() {
        Request.srl.lock();
        try {
            Request.count++;
            return Request.count;
        }finally {
            Request.srl.unlock();
        }
    }

    protected Request(Partaker item, int priority){
        this.id = Request.getUniqueKey();
        this.item = item;
        this.priority = priority;
    }


    public Integer getPriority(){
        return priority;
    }

    public abstract FulfilledRequest<Partaker,Resource> fulfill(Resource r);

    public abstract void abandon();

    public Long key() {
        return this.id;
    }

    public Partaker getPartaker(){
        return this.item;
    }

    public LocalDateTime getRequestTime(){
            return this.datapedido;
    }



    @Override
    public boolean equals(Object obj) {

        if( obj instanceof Request) {
            Request r = (Request) obj;
            return r.key().equals(this.key());
        }else {
            return super.equals(obj);
        }

    }

    public int compareTo(Request<Partaker,Resource>  other){
        return this.getPriority().compareTo(other.getPriority());
    }

}