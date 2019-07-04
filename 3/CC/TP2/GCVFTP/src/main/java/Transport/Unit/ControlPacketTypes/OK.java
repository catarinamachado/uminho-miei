package Transport.Unit.ControlPacketTypes;

import Transport.Common.BitManipulator;
import Transport.Unit.ControlPacket;

public class OK extends ControlPacket {

    public static int size = ControlPacket.header_size + 4 * 4 ;

    private final int seq;
    private final int window;
    private final int rtt;
    private final int rttvar;

    public OK( BitManipulator extrator ) {
        super(ControlPacket.Type.OK, extrator.getShort()/*extended*/);
        this.seq = extrator.getInt();
        this.window = extrator.getInt();
        this.rtt = extrator.getInt();
        this.rttvar = extrator.getInt();
    }

    public OK(short extendedtype, int seq, int freebuffer, int rtt, int rttvar){
        super(ControlPacket.Type.OK,extendedtype);
        this.seq = seq;
        this.rtt = rtt;
        this.rttvar = rttvar;
        this.window = (freebuffer > 1) ? freebuffer: 1;
    }

    public int getSeq(){
        return this.seq;
    }

    public int getRtt() { return this.rtt;}

    public int getRttVar() { return this.rttvar;}

    public int getWindow(){ return this.window;}

    protected int size(){
        return size;
    }

    public byte[] extendedSerialize( BitManipulator extractor ){

        return  extractor.put(seq).put(window).put(rtt).put(rttvar).array();

    }
}
