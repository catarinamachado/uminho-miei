package Transport;

import Transport.Unit.ControlPacketTypes.NOPE;
import Transport.Unit.ControlPacketTypes.OK;
import Transport.Unit.ControlPacketTypes.SURE;
import Transport.Unit.DataPacket;

public interface Window {

    void syn();

    int congestionWindowValue();

    int rtt();
    int rttVar();
    float uploadSpeed();

    void addRtt(int sampleRtt);

    int connectionTime();

    boolean shouldSendOk();
    boolean shouldSendNope();
    boolean shouldKeepAlive();
    boolean shouldRetransmitData();
    boolean shouldSendSure();
    boolean hasTimeout();

    int lastDataReceived();
    int lastOkSent();
    int lastOkReceived();

    void sentTransmission();
    void receivedTransmission();

    void sentSure(SURE packet);
    void sentNope(NOPE packet);
    void sentOk(OK packet);

    void receivedSure(SURE packet);
    void receivedNope(NOPE packet);
    void receivedOk(OK packet);
    void receivedData(DataPacket packet);

    void boot(int theirInitialSeq, int ourInitialSeq, int startTime);

}
