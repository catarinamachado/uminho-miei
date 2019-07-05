package main.server.imp;

class WorkLoad {

    private final Integer s;
    private int f = 0;
    private final RWLock rl = new RWLock();

    private static final SharedQueue<WorkLoad> workqueue = new SharedQueue<>();

    public static void queue(Integer mtype){
        WorkLoad.workqueue.queue(new WorkLoad(mtype));
    }

    public static WorkLoad dequeue() throws InterruptedException{
        return WorkLoad.workqueue.waitedDequeue();
    }

    private WorkLoad(Integer s){
        this.s = s;
        this.f++;
    }

    public int sector(){
        return s;
    }

    public void increment(){
        this.rl.writeLock();try{
            this.f++;
        }finally {
            this.rl.writeUnlock();
        }
    }

    public int getStepCount(){
        this.rl.readLock();
        try {
            return this.f;
        }finally {
            this.rl.readUnlock();
        }
    }
}
