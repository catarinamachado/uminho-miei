package Transport.Common;

import java.nio.ByteBuffer;

import static java.nio.ByteBuffer.wrap;

public class BitManipulator {

    private final ByteBuffer view;
    private final byte[] cache;
    private boolean mod1 = false;
    private boolean mod2 = false;

    public static boolean msb(byte[] q, int byteindex){

        return ( wrap(q).get(byteindex) < 0 );
    }

    public static BitManipulator allocate(int n){

        return new BitManipulator(n);
    }

    public static boolean[] msb2(byte[] q, int byteindex){


        ByteBuffer bb = ByteBuffer.wrap(q);

        char[] bitss = Integer.toBinaryString(bb.getInt(byteindex)).toCharArray();

        StringBuilder sbuilder = new StringBuilder();

        for(int i = bitss.length; i < 32; i++)
            sbuilder.append('0');

        sbuilder.append(bitss);

        boolean[] ansb = new boolean[2];

        ansb[0] = (sbuilder.charAt(0) == '1');
        ansb[1] = (sbuilder.charAt(1) == '1');

        return ansb;
    }

    public BitManipulator(byte[] raw){

        this.cache = raw;
        this.view = wrap(raw);
    }

    private BitManipulator(int n){

        this.cache = new byte[n];
        this.view = wrap(cache);
    }

    public int getInt(){

        int ans =  this.view.getInt();

        if( this.mod2){

            char[] bitss = Integer.toBinaryString(ans).toCharArray();

            StringBuilder sbuilder = new StringBuilder();

            for(int i = bitss.length; i < 32; i++)
                sbuilder.append('0');

            sbuilder.append(bitss);


            sbuilder.setCharAt(0,'0');
            sbuilder.setCharAt(1,'0');

            ans = Integer.parseInt(sbuilder.toString(),2);

        }

        if( this.mod1){
            if(ans==Integer.MIN_VALUE)
                ans = 0;
            else
                ans = (-1)*ans;
        }

        this.mod1 = false;
        this.mod2 = false;

        return ans;
    }

    public short getShort(){

        short ans =  this.view.getShort();

        if( this.mod2){

            short q = (short)Math.pow(2,14);
            short a = (short)Math.abs(ans);

            if( a >= q )
            {
                ans = (short)(a - q);
            }else{
                ans =  a;
            }
        }

        if( this.mod1) {
            if( ans == Short.MIN_VALUE)
                ans = 0;
            else
                ans = (short) ((-1) * ans);
        }

        this.mod1 = false;
        this.mod2 = false;

        return ans;
    }

    public BitManipulator skip(int num){
        for(int i=0;i<num;i++)
                view.get();
        return this;
    }

    public byte getByte(){

        byte ans =  this.view.get();

        if( this.mod2){

            byte q = (byte)Math.pow(2,6);
            byte a = (byte)Math.abs(ans);

            if( a >= q )
            {
                ans = (byte)(a - q);
            }else{
                ans =  a;
            }
        }

        if( this.mod1) {
            if(ans==Byte.MIN_VALUE)
                ans = 0;
            else
                ans = (byte) ((-1) * ans);
        }

        this.mod1 = false;
        this.mod2 = false;

        return ans;
    }

    public BitManipulator put(int value){

        if( this.mod1) {
            if(value == 0)
                value = Integer.MIN_VALUE;
            else
                value = (-1) * value;
        }


        this.mod1 = false;
        this.mod2 = false;

        //System.out.println(congestionWindowValue);

        this.view.putInt(value);

        return this;
    }

    public BitManipulator put(short value){

        if( this.mod1) {
            if(value == 0)
                value = Short.MIN_VALUE;
            else
                value = (short) ((-1) * value);
        }

        this.mod1 = false;
        this.mod2 = false;

        //System.out.println(congestionWindowValue);

        this.view.putShort(value);

        return this;
    }

    public BitManipulator put(byte value){

        if( this.mod1) {
            if(value == 0)
                value = Byte.MIN_VALUE;
            else
                value = (byte) ((-1) * value);
        }

        this.mod1 = false;
        this.mod2 = false;

        this.view.put(value);

        return this;
    }

    public BitManipulator put(byte[] value){

        this.view.put(value);

        this.mod1 = false;
        this.mod2 = false;
        return this;
    }

    public BitManipulator flip(){

        this.mod1 = !this.mod1;
        return this;
    }

    public BitManipulator flip2(){

        this.mod2 = !this.mod2;
        return this;
    }

    public BitManipulator put(int value, boolean[] booleans){

        this.mod1 = false;
        this.mod2 = false;
        int s=0;

        char[] bitss = Integer.toBinaryString(value).toCharArray();

        StringBuilder sbuilder = new StringBuilder();

        for(int i = bitss.length; i < 32; i++)
            sbuilder.append('0');

        sbuilder.append(bitss);

        if(booleans[1])
            sbuilder.setCharAt(1,'1');
        else
            sbuilder.setCharAt(1,'0');

        value = Integer.parseInt( sbuilder.toString() ,2);

        if(booleans[0])
            value += -2147483648;

        this.view.putInt( value );

        return this;
    }

    public byte[] array(){

        return this.cache;
    }

    public BitManipulator rewind(){

        this.view.rewind();
        return this;
    }

}
