package Test.TCP;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(args[0], 7000);

        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append("-");
        for (int i = 0; i < 1000000; i++) {
            sb.append("a");
        }

        socket.getOutputStream().write(sb.toString().getBytes());
    }
}
