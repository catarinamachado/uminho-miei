package Transport.Unit.ControlPacketTypes;

import Transport.Common.BitManipulator;
import Transport.Unit.ControlPacket;

public class SUP extends ControlPacket {

    public static int size = ControlPacket.header_size;

    public SUP( BitManipulator extrator ) {
        super(ControlPacket.Type.SUP, extrator.getShort()/*extended*/);
    }

    public int size(){
        return size;
    }

    public SUP(short extendedtype){
        super(ControlPacket.Type.SUP,extendedtype);
    }

    public byte[] extendedSerialize( BitManipulator extractor ){

        return  extractor.array();

    }
}