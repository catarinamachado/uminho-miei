package main.server.imp;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


class SharedQueue<K> {

    private final LinkedList<K> stk;
    private int end=0;
    private final ReentrantLock rl = new ReentrantLock();
    private final Condition getc;


    public SharedQueue(){
        this.stk = new LinkedList<>();
        this.getc = this.rl.newCondition();
    }


    public void queue(K item) {
        this.rl.lock();
        try {

            if( item instanceof WorkLoad) {
                WorkLoad w = (WorkLoad) item;
                WorkLoad last = (WorkLoad) this.stk.peekLast();
                if ((last != null) && (w.sector() == last.sector())) {
                    last.increment();
                    return;
                }
            }

            if (item != null) {
                this.stk.add(item);
                this.getc.signal();
                this.end++;
            }
        }finally {
            this.rl.unlock();
        }

    }

    public K dequeue() throws NoSuchElementException {

        this.rl.lock();
        try{
            K l = this.stk.remove();
            this.end--;
            return l;
        }finally{
            this.rl.unlock();
        }

    }

    public List<K> recover(){
        this.rl.lock();
        try {
            return new LinkedList<>(this.stk);
        }finally {
            this.rl.unlock();
        }
    }

    public K waitedDequeue() throws InterruptedException{

        this.rl.lock();
        try {
            while (this.end == 0)
                this.getc.await(); // espera passiva que o balcão permita

            K l = this.stk.remove();
            this.end--;
            return l;
        }finally {
            this.rl.unlock();
        }
    }
}