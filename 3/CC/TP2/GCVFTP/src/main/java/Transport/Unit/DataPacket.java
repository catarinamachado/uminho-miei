package Transport.Unit;

import Transport.Common.BitManipulator;

import java.nio.ByteBuffer;

public class DataPacket extends Packet {

    public enum Flag{
        MIDDLE,
        LAST,
        FIRST,
        SOLO;

        boolean[] mark( Flag s){

            boolean[] b = new boolean[2];

            switch( s ){
                case MIDDLE:
                    b[0] = false; b[1] = false;
                    break;
                case LAST:
                    b[0] = false; b[1] = true;
                    break;
                case FIRST:
                    b[0] = true; b[1] = false;
                    break;
                case SOLO:
                    b[0] = true; b[1] = true;
                    break;
            }

            return b;
        }

        Flag parse(boolean[] b){

            int sum=0;

            if ( b[0] ){
                sum+=2;
            }

            if( b[1] ){
                sum++;
            }
            return Flag.values()[sum];
        }

    }

    public static int header_size = 8 + 8;

    private int seq=0;
    private final int streamNumber;
    private final byte[] information;
    private final Flag flag;


    DataPacket( byte[] data, int offset){

        BitManipulator extrator = new BitManipulator(data).skip(offset);

        this.seq = extrator.getInt();
        this.flag = Flag.SOLO.parse(BitManipulator.msb2(data,offset + 4));
        this.streamNumber = extrator.flip2().getInt();
        this.information = new byte[data.length - DataPacket.header_size];

        ByteBuffer.wrap(data,DataPacket.header_size,data.length - DataPacket.header_size).
                get(this.information,
                0,
                data.length  - DataPacket.header_size );
    }

    public DataPacket(byte[] data, int datalen, int streamNumber, DataPacket.Flag flag){
        this.streamNumber = streamNumber;
        this.flag = flag;
        this.information = new byte[datalen];
        ByteBuffer.wrap(data).get(this.information,0,datalen);
    }

    public DataPacket(byte[] data, int streamNumber, DataPacket.Flag flag){
        this(data,data.length, streamNumber,flag);
    }

    public void setSeq(int seq){
        this.seq = seq;
    }

    public byte[] getData() {
        return information;
    }

    public int getSeq() {
        return seq;
    }

    public int getMessageNumber() {
        return streamNumber;
    }

    public DataPacket.Flag getFlag() {
        return this.flag;
    }

    public byte[] serialize(BitManipulator buffer){

        return  buffer.
                put(this.seq).
                put(this.streamNumber, this.flag.mark(this.flag)).
                put(this.information).
                array();
    }

    protected int size(){
        return DataPacket.header_size + this.information.length;
    }

    @Override
    public boolean equals(Object obj) {
        if( !( obj instanceof DataPacket ) )
            return false;

        DataPacket cp = (DataPacket)obj;

        boolean acc= true;

        int min = ( cp.getData().length < this.information.length) ?
                cp.getData().length:
                this.information.length;

        for(int i=0; i< min; i++)
            acc = acc && (cp.getData()[i] == this.information[i]);

        return acc &&
                (cp.getFlag().equals(this.flag)) &&
                (cp.getSeq() == this.seq) &&
                (cp.getMessageNumber() == this.streamNumber);

    }

    @Override
    public String toString(){
        return "x-----------x-----------x--------x-------x----x--x--x-x-x-x--x \n" +
                "flag " + this.getFlag() + "\n" +
                "seq " + this.getSeq() + "\n" +
                "streamid " + this.getMessageNumber() + "\n" +
                new String(this.getData()) + "\n";
    }
}
