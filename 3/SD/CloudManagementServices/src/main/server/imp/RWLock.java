package main.server.imp;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RWLock {
    ReentrantLock rl = new ReentrantLock();
    Condition writer = rl.newCondition();
    Condition reader = rl.newCondition();
    int readers = 0;
    int writers = 0;
    int pitycounter = 0;
    boolean readermode = true;

    public void readLock() {
        try {
            rl.lock();
            readers++;
            while (!readermode && writers!=0){
                reader.await();
            }
        }catch(InterruptedException e){
            e.getStackTrace();
        }
        finally {
            rl.unlock();
        }
    }

    public void readUnlock(){
        try {
            rl.lock();
            readers--;
            pitycounter++;
            if(pitycounter % 10 == 0){
                readermode = false;
                pitycounter = 0;
            }
            writer.signal();
        }
        finally {
            rl.unlock();
        }
    }

    public void writeLock() {
        rl.lock();
        try{
            writers++;
            while (readermode && readers!=0){
                writer.await();
            }
            readermode = true;
        }catch(InterruptedException e){
            e.getStackTrace();
        }

    }

    public void writeUnlock(){
        reader.signalAll();
        writers--;
        rl.unlock();
    }
}

/*
public class RWLock {

    private final ReentrantReadWriteLock rl = new ReentrantReadWriteLock(true);


    public void writeLock(){
        this.rl.writeLock().lock();
    }

    public void writeUnlock(){
        this.rl.writeLock().unlock();
    }

    public void readLock(){
        this.rl.readLock().lock();
    }

    public void readUnlock(){
        this.rl.readLock().unlock();
    }
}
*/