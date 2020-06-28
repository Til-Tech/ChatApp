import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatAppServer {

    static ServerSocket caServerSocket;
    static Socket nSocket;
    static String s;

    public static void main(String[] args) {

        try {

            // Defining/ Initialising new ServerSocket

            caServerSocket = new ServerSocket(5000);
            System.out.println("Waiting for Client...");
            nSocket = caServerSocket.accept();
            System.out.println("Client connected.");

            inputMessages.start();
            inputMessages.join();
            outputMessages.start();
            outputMessages.join();

        } catch (IOException iOE) {
            iOE.printStackTrace();
        } catch (InterruptedException iE) {
            iE.printStackTrace();
        }
    }

    static Thread inputMessages = new Thread(new Runnable() {

        @Override
        public void run() {

            try {

                // Setting up InputStream for the ServerSocket to receive the message String

                DataInputStream clientMessages = new DataInputStream(nSocket.getInputStream());
                s = clientMessages.readLine();

                // Testing if the Input String got received
                System.out.println(s);
            } catch (IOException iOE) {
                iOE.printStackTrace();
            }
        }
    });

    static Thread outputMessages = new Thread(new Runnable() {

        @Override
        public void run() {

            try {

                while (s != null) {
                    // Setting up OutputStream to send back the String

                    DataOutputStream clientMessagesBack = new DataOutputStream(nSocket.getOutputStream());

                    clientMessagesBack.writeBytes(s);

                    clientMessagesBack.flush();
                }

                caServerSocket.close();
            }

            catch (IOException ioE) {
                ioE.printStackTrace();
            }
        }
    });

}
