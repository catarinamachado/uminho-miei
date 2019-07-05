package main.server.imp;

class ServerEmployee extends Thread {

    private final InstanceType[] types;

    public ServerEmployee(InstanceType[] types) {
        this.types = types;
    }

    @Override
    public void run() {

        while (true) {
            try {
                WorkLoad tp = WorkLoad.dequeue();
                int f = tp.getStepCount();
                for(int i = 0; i < f; i++)
                    this.types[tp.sector()].organize();
            }catch(InterruptedException e){
                return;
            }
        }

    }
}