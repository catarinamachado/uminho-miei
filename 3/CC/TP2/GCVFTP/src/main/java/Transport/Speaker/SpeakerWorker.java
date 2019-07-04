package Transport.Speaker;

import Common.Debugger;
import Transport.Executor;
import Transport.TransportChannel;
import Transport.Unit.DataPacket;

import java.io.IOException;
import java.io.NotActiveException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


public class SpeakerWorker extends TimerTask {

    private final SendBuffer sendBuffer;
    private final TransportChannel channel;
    private final Timer sendTimer;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final SpeakerProperties properties;

    public SpeakerWorker(TransportChannel ch, SendBuffer send, long period, SpeakerProperties properties){
        this.sendBuffer = send;
        this.channel = ch;
        this.sendTimer = new Timer();
        this.sendTimer.scheduleAtFixedRate( this, 0, period);
        this.properties = properties;
    }

    public void stop(){
        this.active.set(false);
        sendTimer.cancel();
    }

    public void run(){
        try {
            Executor.add(Executor.ActionType.SYN);
            Debugger.log("rate " + this.channel.window().uploadSpeed() + " Mb/s" );
            if( active.get() ) {
                int it =this.channel.window().congestionWindowValue();
                for(int i = 0; i< it ; i++){
                    DataPacket packet = sendBuffer.poll();
                    if( packet != null) {
                        //Debugger.log(">>>>>>>>>>> seq : " + packet.getSeq());
                        //Debugger.log("##################" + packet.getFlag().toString() + "###############");

                        channel.sendPacket(packet);
                    }
                }

            }
        }catch (NotActiveException other){
            active.set(false);
        } catch ( InterruptedException| IOException e){
            e.printStackTrace();
        }
    }
}
