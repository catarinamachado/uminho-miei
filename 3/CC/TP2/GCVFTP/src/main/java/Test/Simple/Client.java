package Test.Simple;

import Common.Connection;
import Transport.GCVConnection;
import Transport.GCVSocket;

import java.util.Arrays;

public class Client {
    public static void main(String[] args) throws Exception {
        System.out.println("Launching client");
        GCVSocket cs = new GCVSocket(GCVConnection.send_buffer_size,true,6969);
        System.out.println("Connecting");
        cs.connect(args[0], 7220);
        System.out.println("Connection established");

        Connection.send(cs, "ola".getBytes());
        System.out.println("sent test 1");

        char[] chars = new char[10000000];
        Arrays.fill(chars, 'a');
        String str = new String(chars);
        Connection.send(cs, str.getBytes());
        System.out.println("sent test 2");

        Connection.send(cs, "¿Ç÷û".getBytes());
        System.out.println("sent test 3");

        for (int i = 1000000; i >= 1000; i/=10) {
            System.out.println("\nbuilding " + i);
            char[] c = new char[i];
            Arrays.fill(c, 'a');
            String a = System.currentTimeMillis() + "-" + i + "-";
            String s = new String(c);
            Connection.send(cs, (a + s).getBytes());
            System.out.println("sent " + i);
        }
    }
}
