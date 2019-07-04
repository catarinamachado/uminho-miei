package Transport.Common;

import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Interval<V> {

    private int min;
    private int max;
    private final ReadWriteLock wrl = new ReentrantReadWriteLock();

    private final LinkedList<V> l = new LinkedList<>();

    public Interval(int seq, V value){
        this.min = this.max = seq;
        l.add(value);
    }

    public int min(){
        wrl.readLock().lock();
        try {
            return min;
        }finally {
            wrl.readLock().unlock();
        }
    }

    public int max(){
        wrl.readLock().lock();
        try {
            return max;
        }finally {
            wrl.readLock().unlock();
        }
    }

    public boolean isSingleton(){
        wrl.readLock().lock();
        try {
            return this.max == this.min;
        }finally {
            wrl.readLock().unlock();
        }
    }

    public boolean less(Interval x){
        Interval a = ( this.toString().compareTo(x.toString()) < 0 ) ? this : x;
        Interval b = ( this.toString().compareTo(x.toString()) < 0 ) ? x : this;

        a.wrl.readLock().lock();
        b.wrl.readLock().lock();

        try{
            return (this.max < x.min());
        }finally{
            a.wrl.readLock().unlock();
            b.wrl.readLock().unlock();
        }

    }

    public boolean less(int x){
        /*argument is less than*/
        wrl.readLock().lock();
        try {
            return (this.min > x );
        }finally {
            wrl.readLock().unlock();
        }
    }

    public boolean intersects(Interval x){
        /*basta que um dos limites de x esteja no intervalo*/
        Interval a = ( this.toString().compareTo(x.toString()) < 0 ) ? this : x;
        Interval b = ( this.toString().compareTo(x.toString()) < 0 ) ? x : this;

        a.wrl.readLock().lock();
        b.wrl.readLock().lock();

        try{
            return (x.max() <= this.max && x.max() >= this.min()) || (x.min() <= this.max && x.min() >= this.min);
        }finally{
            a.wrl.readLock().unlock();
            b.wrl.readLock().unlock();
        }

    }

    public V take(){
        this.wrl.writeLock().lock();
        try{
            this.min++;
            return l.poll();
        }finally{
            this.wrl.writeLock().unlock();
        }
    }

    public LinkedList<V> getValues(){
        wrl.readLock().lock();
        try {
            return new LinkedList<>(this.l);
        }finally{
            wrl.readLock().unlock();
        }
    }

    public int merge( Interval<V> x ){

        Interval a = ( this.toString().compareTo(x.toString()) < 0 ) ? this : x;
        Interval b = ( this.toString().compareTo(x.toString()) < 0 ) ? x : this;

        a.wrl.writeLock().lock();
        b.wrl.writeLock().lock();

        try{

            if ( x.min() == this.max + 1 ) {/*se são contiguos integra*/
                this.max = x.max();

                this.l.addAll( x.getValues() );
                return 1;
            }else if( x.max() + 1 == this.min ){
                this.min = x.min();

                this.l.addAll( 0, x.getValues() );
                return -1;
            }

            if( (x.min() >= this.min) && (x.max() <= this.max) ){
                /* está contido */
                return 0;
            }

            return 4;
            /*indica se o merge foi feito*/
        }finally{
            a.wrl.writeLock().unlock();
            b.wrl.writeLock().unlock();

        }
    }

    @Override
    public boolean equals(Object obj) {
        if( ! (obj instanceof Interval) )
            return false;

        Interval other = (Interval)obj;

        return (other.min == this.min) && (other.max == this.max);
    }
}
