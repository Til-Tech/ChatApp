import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ProcessUserInf extends Thread {

    private ChatAppClient cac = new ChatAppClient();
    private String userName;
    private DataOutputStream dos;
    private ObjectInputStream ois;
    private Socket nameSocket;
    private ArrayList<String> connectedClientsList = new ArrayList<String>();

    @Override
    public void run() {
        userName = cac.getUserName();

        try {

            nameSocket = new Socket(cac.getIPAdress(), 5001);
            System.out.println("Connected to UserInf-Server.");

            dos = new DataOutputStream(nameSocket.getOutputStream());

            dos.writeUTF(userName);
            dos.flush();

            for (; true;) {

                ois = new ObjectInputStream(nameSocket.getInputStream());
                connectedClientsList = (ArrayList<String>) ois.readObject();

                System.out.println(connectedClientsList.toString());

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    public ArrayList<String> getConnectedClientList() {
        return connectedClientsList;
    }

}