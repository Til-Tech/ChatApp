import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;

public class ConnectionSocketHandler extends Thread {

    private ServerSocket clientConnectionCheckServerSocket;
    private Socket connectedClient;
    private DataInputStream dis;
    private String isConnectedCode;

    @Override
    public void run() {

        try {

            clientConnectionCheckServerSocket = new ServerSocket(4999);
            connectedClient = clientConnectionCheckServerSocket.accept();

            dis = new DataInputStream(connectedClient.getInputStream());

            for (; true;) {

                isConnectedCode = dis.readUTF();

                if (isConnectedCode.equals("0")) {

                    System.exit(0);

                }

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}