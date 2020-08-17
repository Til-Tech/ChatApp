import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ReceiveUserInf extends Thread {

    private String userName;
    private ServerSocket srvSocket;
    private Socket socket;
    private DataInputStream dis;
    private ObjectOutputStream oos;
    private ArrayList<String> nameList;
    private ArrayList<Socket> clientList;

    @Override
    public void run() {

        try {

            srvSocket = new ServerSocket(5001);
            nameList = new ArrayList<String>();
            clientList = new ArrayList<Socket>();

            for (; true;) {

                clientList.add(socket = srvSocket.accept());
                System.out.println("Client auf UserInf-Server accepted.");

                dis = new DataInputStream(socket.getInputStream());

                userName = dis.readUTF();

                nameList.add(userName);

                for (Socket element : clientList) {

                    oos = new ObjectOutputStream(element.getOutputStream());
                    oos.writeObject(nameList);

                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}