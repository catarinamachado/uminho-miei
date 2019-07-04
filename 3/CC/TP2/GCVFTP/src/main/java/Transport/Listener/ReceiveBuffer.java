package Transport.Listener;

import Common.Debugger;
import Transport.Common.Interval;
import Transport.Common.IntervalChain;
import Transport.Executor;
import Transport.GCVConnection;
import Transport.Unit.ControlPacket;
import Transport.Unit.DataPacket;
import Transport.Unit.Packet;

import java.io.NotActiveException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ReceiveBuffer {

    private final LinkedBlockingQueue<ControlPacket> control;
    private final LinkedBlockingQueue<DataPacket> data = new LinkedBlockingQueue<>();
    private final IntervalChain<DataPacket> uncounted;
    private final AtomicInteger lastOkedSeq;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final int maxDataBufferSize;
    private final ReadWriteLock wrl = new ReentrantReadWriteLock();


    ReceiveBuffer(int maxControlBufferSize, int maxDataBufferSize, int seq){
        Debugger.log(">>>>> theirs " + seq + "<<<<<<<");
        this.control = new LinkedBlockingQueue<>(maxControlBufferSize);
        this.uncounted =  new IntervalChain<DataPacket>(maxDataBufferSize);
        this.lastOkedSeq = new AtomicInteger(seq);
        this.maxDataBufferSize = maxDataBufferSize;
    }

    int getWindowSize(){
        this.wrl.readLock().lock();
        try {
            return this.maxDataBufferSize - data.size() - uncounted.size();
        }finally {
            this.wrl.readLock().unlock();
        }
    }

    void supply(Packet packet) throws InterruptedException, NotActiveException{
        if( !this.active.get() )
            throw new NotActiveException();

        if( packet instanceof ControlPacket)
            this.control((ControlPacket)packet);

        else if( packet instanceof DataPacket)
            this.data((DataPacket)packet);

    }

    int getLastOk(){
        return lastOkedSeq.get();
    }

    private void data(DataPacket packet) throws NotActiveException{
        this.wrl.writeLock().lock();
        try {
            if (!this.active.get())
                throw new NotActiveException();
            Debugger.log("raw data : " + packet.getSeq() +  "lastok " +this.lastOkedSeq.get() );
            if (packet.getSeq() > this.lastOkedSeq.get()) {
                Debugger.log(" lastoked " + this.lastOkedSeq.get() + " minseq " + uncounted.minSeq());
                uncounted.add(packet.getSeq(),packet);

                /* verificar se posso tirar acks */

                if (lastOkedSeq.get() + 1 == uncounted.minSeq()) {
                    Interval<DataPacket> p = uncounted.take();

                    lastOkedSeq.set(p.max());
                    Debugger.log(": setok " + p.max());

                    List<DataPacket> lisp = p.getValues();

                    lisp.forEach(
                            lisppacket ->
                            {
                                try {
                                    this.data.put(lisppacket);
                                    Executor.add(Executor.ActionType.DATA);
                                } catch (Exception e) {
                                    e.getStackTrace();
                                }
                            }
                    );
                }

            }
        }finally {
            this.wrl.writeLock().unlock();
        }
    }

    private void control(ControlPacket packet) throws InterruptedException, NotActiveException{
        if( !this.active.get() )
            throw new NotActiveException();

        Executor.add(Executor.ActionType.CONTROL);

        control.put(packet);
    }

    public void clear(){
        this.uncounted.clear();
    }

    ControlPacket getControlPacket() throws InterruptedException{
        return this.control.take();
    }

    DataPacket getDataPacket() throws InterruptedException{
        return this.data.take();
    }

    List<Integer> getLossList(){
        this.wrl.readLock().lock();
        try {

            return uncounted.dual(this.lastOkedSeq.get() + 1, GCVConnection.max_loss_list_size);

        }finally {
            this.wrl.readLock().unlock();

        }
    }

    void terminate(){
        this.wrl.writeLock().lock();
        try {
            this.active.set(false);

            control.clear();
            uncounted.clear();

        }finally {
            this.wrl.writeLock().unlock();
        }
    }
}
