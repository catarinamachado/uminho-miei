package Transport.Unit.ControlPacketTypes;

import Transport.Common.BitManipulator;
import Transport.Unit.ControlPacket;

public class FORGETIT extends ControlPacket {

    public static int size = ControlPacket.header_size + 4;

    public FORGETIT( BitManipulator extrator ) {
        super(Type.FORGETIT, extrator.getShort()/*extended*/);
        streamid = extrator.getInt();
    }

    private int streamid;

    public FORGETIT(short extendedtype, int stream){
        super(Type.FORGETIT,extendedtype);
        this.streamid = stream;
    }

    public int getStream(){
        return this.streamid;
    }

    protected int size(){
        return size;
    }

    public byte[] extendedSerialize( BitManipulator extractor ){

        return  extractor.put(streamid).array();

    }
}
