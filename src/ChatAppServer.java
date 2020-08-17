import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatAppServer {

    private ServerSocket caServerSocket;
    private ArrayList<Socket> nSocket;
    private DataInputStream clientMessages;
    private String message = ".";
    private DataOutputStream clientMessagesBack;
    private int clientCounter = 0;
    private ConnectionSocketHandler csh;
    private ReceiveUserInf ruf;

    public static void main(String[] args) {

        ChatAppServer cas = new ChatAppServer();

        cas.connectingClients();

    }

    public Thread connectingClients = new Thread(new Runnable() {

        @Override
        public void run() {

            try {

                caServerSocket = new ServerSocket(5000);

                ruf = new ReceiveUserInf();
                ruf.start();

                nSocket = new ArrayList<Socket>();

                for (; true;) {

                    System.out.println("Waiting for Client...");
                    Socket newClient = new Socket();

                    nSocket.add(newClient = caServerSocket.accept());
                    System.out.println("Client connected.");

                    csh = new ConnectionSocketHandler();
                    csh.start();

                    ChatAppServer casThread = new ChatAppServer(clientCounter, nSocket, csh);
                    casThread.processMessages.start();
                    clientCounter++;

                }

            } catch (IOException ioe) {
                ioe.printStackTrace();

            }

        }
    });

    public void connectingClients() {
        connectingClients.setName("Connecting Clients Thread");
        connectingClients.start();
    }

    private Thread processMessages = new Thread(new Runnable() {

        @Override
        public void run() {

            try {

                clientMessages = new DataInputStream(nSocket.get(clientCounter).getInputStream());

                while (!nSocket.get(clientCounter).isClosed()) {

                    message = clientMessages.readUTF();

                    for (Socket element : nSocket) {

                        clientMessagesBack = new DataOutputStream(element.getOutputStream());

                        clientMessagesBack.writeUTF(message);
                    }
                    clientMessagesBack.flush();

                    System.out.println(nSocket.size());

                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
    });

    ChatAppServer() {

    }

    ChatAppServer(int clientCounter, ArrayList<Socket> nSocket, ConnectionSocketHandler csh) {
        this.clientCounter = clientCounter;
        this.nSocket = nSocket;
        this.csh = csh;

    }
}
