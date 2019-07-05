package main.server.imp.scheduler;

import main.server.imp.scheduler.exceptions.RequestNotFoundException;
import main.server.imp.scheduler.exceptions.ResourceNotAvailableException;
import main.server.imp.scheduler.types.FulfilledRequest;
import main.server.imp.scheduler.types.Request;

import java.util.*;

public class ResourceScheduler<Partaker, Resource> {

    /*
     * hierarquia de pedidos à espera Cada uma das PriorityQueue estás ordenado
     * crescentemente pelo fator de comparação.
     */
    private final PriorityQueue<Request<Partaker, Resource>> requestHierarchy;
    /*
     * hierarquia de pedidos em uso Cada uma das PriorityQueue estás ordenado
     * decrescentemente pelo fator de comparação.
     */
    private final PriorityQueue<FulfilledRequest<Partaker, Resource>> usingHierarchy;

    /*
     * stack com os recursos livres.
     *
     */
    private final Stack<Resource> resources = new Stack<>();
    /*
     * Indica se o nível da hierarquia de pedidos em usos está ou não em uso.
     */
    //private final boolean[] usingInfo;

    /*
     * Indica se o nível da hierarquia de pedidos está ou não em uso.
     */
    //private final boolean[] requestInfo;

    private final Map<Long,Request<Partaker, Resource>> rmap = new HashMap<>();
    private final Map<Long,FulfilledRequest<Partaker, Resource>> frmap = new HashMap<>();


    /*
     * número de níveis da hierarquia.
     *
     */
    //private final int capacity;

    /*
     * Coleção que contém todos os pedidos do sistema.
     */
    //private final HashMap<Long, Request<Partaker, Resource>> inthesystem = new HashMap<>();


    public ResourceScheduler() {
        this.requestHierarchy = new PriorityQueue<>();
        this.usingHierarchy = new PriorityQueue<>();
    }

    public synchronized void request(Request<Partaker, Resource> r) {
        this.rmap.put(r.key(),r);
        this.requestHierarchy.add(r);
    }

    public synchronized void cancel(Long requestid) throws RequestNotFoundException{
        if( this.rmap.containsKey(requestid)){
            Request<Partaker, Resource> r = this.rmap.remove(requestid);
            this.cancel(r);
        }else if ( this.frmap.containsKey(requestid) ){
            FulfilledRequest<Partaker, Resource> fr = this.frmap.remove(requestid);
            this.cancel(fr);
        }else{
             throw new RequestNotFoundException();
        }

    }

    public synchronized void supply(Resource r) {
        this.resources.push(r);
    }

    public synchronized void step() {
        if (this.existRequest()) {
            Request<Partaker, Resource> p = this.requestHierarchy.remove();
            Resource r;
            if (this.existResources(p)) {
                if( !this.resources.isEmpty() ){
                    r = this.resources.pop();
                }else{
                    FulfilledRequest<Partaker, Resource> fr = this.usingHierarchy.remove();
                    this.request(fr.simplify());
                    r =  fr.surrender();
                    this.frmap.remove(fr.key());
                }

                FulfilledRequest<Partaker, Resource> fr = p.fulfill(r);
                this.usingHierarchy.add(fr);
                this.frmap.put(fr.key(),fr);
                this.rmap.remove(p.key());

            } else {
                this.request(p);
            }
        }

    }

    private boolean existRequest() {
        return !this.requestHierarchy.isEmpty();
    }

    private boolean existResources(Request<Partaker, Resource> inc) {
        boolean b = !this.resources.isEmpty();
        if(b)
            return b;
        if(this.usingHierarchy.isEmpty())
            return false;

        Request<Partaker, Resource> worst = this.usingHierarchy.peek().simplify();

        return (inc.compareTo(worst) < 0);


    }

    private synchronized void cancel(Request<Partaker, Resource> r) throws RequestNotFoundException {
        boolean b = this.requestHierarchy.remove(r);

        if( !b )
            throw new RequestNotFoundException();

        r.abandon();
    }

    private synchronized void cancel(FulfilledRequest<Partaker, Resource> fr) throws RequestNotFoundException {
        boolean b = this.usingHierarchy.remove(fr);

        if( !b )
            throw new RequestNotFoundException();

        this.resources.push(fr.surrender());
        fr.simplify().abandon();
    }

}
