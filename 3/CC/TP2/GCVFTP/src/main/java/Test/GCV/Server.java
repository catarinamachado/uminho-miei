package Test.GCV;

import Common.Connection;
import Transport.GCVConnection;
import Transport.GCVSocket;

import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("-");
        GCVSocket cs = new GCVSocket(GCVConnection.send_buffer_size,true, 7220);
        System.out.println("listening");
        cs = cs.listen();
        System.out.println("receiving");
        while(true) {
            String s = Connection.receive(cs);

            if(s.startsWith("-")) {
                String sa = s.replaceFirst("-", "");
                long receivingTime = System.currentTimeMillis();
                long sentTime = Long.parseLong(sa);
                System.out.println(receivingTime - sentTime);
            }

            //System.out.println(s);
        }
    }
}
