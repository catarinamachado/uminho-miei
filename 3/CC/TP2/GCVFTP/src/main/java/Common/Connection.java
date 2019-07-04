package Common;

import Transport.GCVSocket;

import java.io.IOException;
import java.io.OutputStream;

public class Connection {
    public static String receive(GCVSocket cs)  {
        try {
            return new String(CompressionUtils.decompress(cs.receive()));
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static void send(GCVSocket cs, byte[] bytes) {
        try {
            Debugger.log("Start writing");

            OutputStream pout = cs.send();

            pout.write(CompressionUtils.compress(bytes));
            pout.flush();
            pout.close();

            Debugger.log("############ Sent ################");
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
