package Transport.Unit;

import Transport.Common.BitManipulator;
import Transport.Unit.ControlPacketTypes.*;

public abstract class ControlPacket extends Packet {

    public enum Type{
        HI, /*syn*/
        OK, /*ack*/
        SURE, /*ack2*/
        NOPE, /*shouldSendNope*/
        BYE, /*fin*/
        FORGETIT, /*message drop*/
        SUP
    }

    public static int header_size = 4 + 8;

    public static ControlPacket parseControl(byte[] data, int offset){
        BitManipulator extractor = new BitManipulator(data).skip(offset);
        Type ctype = Type.values()[extractor.flip().getShort()];

        switch(ctype){
            case HI:return new HI(extractor);
            case OK:return new OK(extractor);
            case SURE:return new SURE(extractor);
            case BYE:return new BYE(extractor);
            case FORGETIT:return new FORGETIT(extractor);
            case NOPE:return new NOPE(extractor);
            case SUP: return new SUP(extractor);
        }

        return null;
    }

    private final Type type; /* control message type*/
    private short extendedtype; /*para a aplicação*/

    protected ControlPacket(Type t, short extendedtype){
        this.type = t;
        this.extendedtype=extendedtype;
    }

    public void setExtendedType(short extendedtype){ this.extendedtype = extendedtype; }

    public ControlPacket.Type getType() { return this.type; }

    public short getExtendedtype() { return this.extendedtype; }

    public byte[] serialize(BitManipulator buffer){
        return  this.extendedSerialize(
                buffer.
                flip().put((short)this.type.ordinal()).
                put(this.extendedtype)
        );

    }

    protected abstract byte[] extendedSerialize(BitManipulator extractor);

    @Override
    public boolean equals(Object obj) {
        if( !( obj instanceof ControlPacket ) )
            return false;

        ControlPacket cp = (ControlPacket)obj;

        boolean acc= true;

        return (this.getType().equals(cp.getType())) &&
                (cp.getExtendedtype() == this.extendedtype);

    }

    @Override
    public String toString(){
        return "x-----------x-----------x--------x-------x----x--x--x-x-x-x--x \n" +
                "type " + this.getType() +
                "extcode " + this.getExtendedtype();
    }
}
