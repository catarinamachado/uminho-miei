package Transport.Unit.ControlPacketTypes;

import Transport.Common.BitManipulator;
import Transport.Unit.ControlPacket;

public class BYE extends ControlPacket {

    public static int size = ControlPacket.header_size ;

    public BYE( BitManipulator extrator ) {
        super(ControlPacket.Type.BYE, extrator.getShort()/*extended*/);
    }

    public BYE(short extendedtype){
        super(ControlPacket.Type.BYE,extendedtype);
    }

    public byte[] extendedSerialize( BitManipulator extractor ){

        return  extractor.array();

    }

    protected int size(){ return size; }

}
