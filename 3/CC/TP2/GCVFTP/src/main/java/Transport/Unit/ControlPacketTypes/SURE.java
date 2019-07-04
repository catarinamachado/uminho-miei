package Transport.Unit.ControlPacketTypes;

import Transport.Common.BitManipulator;
import Transport.Unit.ControlPacket;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

public class SURE extends ControlPacket {

    public static int size = ControlPacket.header_size + 4;

    public static final short ack_hi = 239;
    public static final short ack_ok = 400;

    private int ok = -1;

    public SURE( BitManipulator extrator ) {
        super(ControlPacket.Type.SURE, extrator.getShort()/*extended*/);
        try {
            this.ok = extrator.getInt();
        }catch(BufferOverflowException| BufferUnderflowException e){
            this.ok =-1;
        }
    }

    protected int size(){
        return ( ok == -1 )? ControlPacket.header_size: ControlPacket.header_size + 4;
    }

    public SURE(short extendedtype){
        super(ControlPacket.Type.SURE,extendedtype);
    }

    public SURE(short extendedtype, int ok_seq){
        super(ControlPacket.Type.SURE,extendedtype);
        this.ok = ok_seq;
    }

    public int getOK(){
        return ok;
    }

    public byte[] extendedSerialize( BitManipulator extractor ){

        if(this.ok == -1)
            return extractor.array();
        else
            return extractor.put(this.ok).array();
    }
}
