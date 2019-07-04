package Test.Simple;

import Common.Connection;
import Transport.GCVConnection;
import Transport.GCVSocket;

public class Server {
    private static GCVSocket cs;
    private static int correct = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("Launching server");
        cs = new GCVSocket(GCVConnection.send_buffer_size,true, 7220);
        System.out.println("Listening");
        cs = cs.listen();
        System.out.println("Connection established");

        test1();
        test2();
        test3();

        System.out.println("\n\norder:");
        test4();
    }

    private static void test1() {
        if(Connection.receive(cs).equals("ola")) {
            correct++;
            System.out.println("Simple test passed - " + correct + "/3");
        } else {
            System.out.println("Simple test failed - " + correct + "/3");
        }
    }

    private static void test2() {
        String s = Connection.receive(cs);
        if(s.matches("a{10000000}")) {
            correct++;
            System.out.println("Big input (10000000 chars) test passed - " + correct + "/3");
        } else {
            System.out.println("Big input (10000000 chars) test failed - " + correct + "/3");
        }
    }

    private static void test3() {
        if(Connection.receive(cs).equals("¿Ç÷û")) {
            correct++;
            System.out.println("Uncommon Charsets characters passed - " + correct + "/3");
        } else {
            System.out.println("Uncommon Charsets characters - " + correct + "/3");
        }
    }

    private static void test4() {
        for (int i = 1000000; i >= 1000; i/=10) {
            String s = Connection.receive(cs);
            System.out.println("got " + i);
            long receivingTime = System.currentTimeMillis();
            String[] ss = s.split("-");
            long sentTime = Long.parseLong(ss[0]);
            System.out.println(ss[1] + " -> sent at " + sentTime + ", received at " + receivingTime);
            System.out.println(i + " chars: " + (receivingTime - sentTime) + " ms\n");
        }
    }
}
