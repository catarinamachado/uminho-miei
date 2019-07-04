package Transport.Speaker;

import Common.Debugger;
import Transport.Unit.DataPacket;

import java.io.NotActiveException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.DatagramSocket;
import java.util.*;
import java.util.concurrent.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/* structure for 'accounting' traveling packets*/
class SendBuffer {

    private final LinkedBlockingQueue<DataPacket> uncounted;
    /*
     * List containing the packets in order that haven't been acked or are waiting to be sent.
     *
     * TODO : Make it a circular List.
     * */
    private final LinkedBlockingDeque<DataPacket> sending = new LinkedBlockingDeque<>();
    /*
     * Packet Sending queue.
     *      the elements of the queue are either :
     *      - datapackets.
     *      - null when refering to the element in the top.
     *
     * obs: why null? we want to keep the order in control packets.
     * */
    /*
     * List of control packets waiting to be sent.
     * */
    private final AtomicBoolean ative = new AtomicBoolean(true);
    /*
     * true : if the accountant is active.
     * false : otherwise.
     * */

    private final AtomicInteger seq;/* TODO : make sentSure 0 > the max numbers */
    /*
     * Current sequence number.
     *  */

    SendBuffer(int stock, int seq){
        this.uncounted = new LinkedBlockingQueue<>(stock);
        this.seq = new AtomicInteger(seq);
    }

    void data( DataPacket value) throws InterruptedException{

        /* espera pela oportunidade para colocar o pacote no sistema*/

        value.setSeq(this.seq());
        uncounted.put(value);

        /* atribui um número de sequência ao datapacket */

        sending.put(value);
        /* põe o datapacket na fila de envio */
    }

    void ok(int x) throws NotActiveException, InterruptedException{
        if( !this.ative.get() )
            throw new NotActiveException();
        Debugger.log("accountat release " + x);
        if( !this.uncounted.isEmpty() ){
            try {
                LinkedList<DataPacket> l = new LinkedList<>();

                while (this.uncounted.peek().getSeq() <= x)
                    this.uncounted.take();

            }catch (NullPointerException e){

            }
            /* decrementa o número de pacotes em falta do stream*/
            /* TODO: Assegurar que é suportada ordem circular */
        }

    }

    void nope(List<Integer> missing) throws InterruptedException, NotActiveException {

        if( !this.ative.get() )
            throw new NotActiveException();

        //this.sending.clear();

        //for( Integer i : missing )
           // Debugger.log("::> " + i + "::||");

        Iterator<DataPacket> it = this.uncounted.iterator();

        Stack<DataPacket> st = new Stack<>();

        int count=0;
        int max = missing.size()-1;

        for( Integer mss : missing ) {
            if( max != count) {
                while (it.hasNext()) {
                    DataPacket packet = it.next();
                    if (packet.getSeq() == mss) {
                        st.push(packet);
                        count++;

                        break;
                    } else if (mss > packet.getSeq()) {
                        break;
                    }
                }
            }
        }

        while (!st.empty())
           this.sending.putFirst(st.pop());

       //while( it.hasNext() )
        // this.sending.put( it.next() );

    }

    void terminate(){
        /*deactivates the accountant*/
        ative.set(false);
        this.uncounted.clear();
        this.sending.clear();
    }

    private int seq(){
        /*TODO: assegurar que é um lista circular */
        return this.seq.accumulateAndGet(0,
                (x,y) -> Integer.max(++x % Integer.MAX_VALUE, y)
        );
    }

    DataPacket poll() throws NotActiveException{
        /* método para o sendworker */
        if( !this.ative.get() )
            throw new NotActiveException();

        // procura o index
        return sending.poll();
    }

    void retransmit(){

        sending.clear();
        sending.addAll(this.uncounted);

    }

}
