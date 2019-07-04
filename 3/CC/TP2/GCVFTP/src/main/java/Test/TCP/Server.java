package Test.TCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7000);
        Socket socket = serverSocket.accept();
        String a = convertStreamToString(socket.getInputStream());
        String[] as = a.split("-");
        long l = Long.parseLong(as[0]);
        long s = System.currentTimeMillis();
        System.out.println(s-l);
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
