package Transport.Listener;

import Common.Debugger;
import Transport.GCVConnection;
import Transport.TransportChannel;
import Transport.Unit.ControlPacket;
import Transport.Unit.DataPacket;

import java.util.List;

public class ListenerGate {

    private final ReceiveBuffer receiveBuffer;

    private final ListenerWorker worker;

    private final ListenerProperties properties;

    public ListenerGate(ListenerProperties me, TransportChannel ch, int seq){
        Debugger.log("ListenerGate created");
        this.properties = me;
        this.receiveBuffer = new ReceiveBuffer(
            (int)(GCVConnection.controlBufferFactor * me.transmissionChannelBufferSize()),
            me.transmissionChannelBufferSize(),
            seq
        );

        this.worker = new ListenerWorker(ch, receiveBuffer, me, GCVConnection.number_of_receive_workers);

    }

    public List<Integer> getLossList(){
        return receiveBuffer.getLossList();
    }

    public ControlPacket control() throws InterruptedException{
        return this.receiveBuffer.getControlPacket();
    }

    public DataPacket data() throws InterruptedException{
        return this.receiveBuffer.getDataPacket();
    }

    public int getWindowSize(){
        return this.receiveBuffer.getWindowSize();
    }

    public void close(){
        Debugger.log("ListenerGate closed");
        this.worker.stop();
        this.receiveBuffer.terminate();
    }

    public void prepareRetransmition(){
        this.receiveBuffer.clear();
    }

}
