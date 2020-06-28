import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;

public class ChatAppClient extends Application {

   ChatAppClient newCAC;
   private TextArea messages = new TextArea();
   private TextField input;
   private static String ipAdress = null;
   private static String streamRead = null;
   private DataInputStream dis;
   private DataOutputStream dos;
   private static Socket client;

   public Parent mainScene() throws UnknownHostException, IOException {

      // Getting IP-Adress from a different file as a String

      String userDir = System.getProperty("user.dir");
      File file = new File(userDir + "\\..\\IPAdress.txt");
      RandomAccessFile raFile = new RandomAccessFile(file, "rw");
      raFile.seek(0);
      ipAdress = raFile.readLine();
      raFile.close();

      // Initialising new Socket

      client = new Socket(ipAdress, 5000);
      System.out.println("Connected to Server.");

      // Defining JavaFx Application-Layout

      messages.setPrefHeight(220);
      input = new TextField();

      VBox root = new VBox(20, messages, input);
      root.setPrefSize(400, 400);

      // Setting up action for Textfield when pressing Enter

      input.setOnAction(new EventHandler<ActionEvent>() {

         @Override
         public void handle(ActionEvent event) {

            try {

               outputMessages.start();
               outputMessages.join();
               inputMessages.start();

            } catch (InterruptedException iE) {
               iE.printStackTrace();
            }
         }
      });

      return root;

   }

   public void start(Stage mainStage) {

      try {
         mainStage.setScene(new Scene(mainScene()));
         mainStage.show();

      } catch (UnknownHostException uHE) {
         uHE.printStackTrace();
      } catch (IOException iOE) {
         iOE.printStackTrace();
      }

   }

   Thread outputMessages = new Thread(new Runnable() {

      @Override
      public void run() {

         try {

            // Defining and initialising Input-String

            String message = "Client: ";
            message += input.getText();

            // Opening OutputStream for String to send it to the server
            dos = new DataOutputStream(client.getOutputStream());

            // Giving the String to the OutputStream
            dos.writeBytes(message);

            // Clearing the Textfield from Input-Text
            input.clear();
         } catch (IOException ioE) {
            ioE.printStackTrace();
         }
      }
   });

   Thread inputMessages = new Thread(new Runnable() {

      @Override
      public void run() {
         try {

            // Setting up InputStream for the Socket to receive the String back from the
            // server
            System.out.println("TEEEEST");

            dis = new DataInputStream(client.getInputStream());

            // Reading String from InputStream

            streamRead = dis.readLine();
            System.out.println(streamRead);

            // Closing In-and OutputStreams + Socket

            dos.close();

            // Showing the String in the TextArea "messages"
            if (streamRead != null) {
               messages.appendText(streamRead + "\n");
            } else {
               System.out.println("No Message to print!");
            }

         } catch (IOException iOE) {
            iOE.printStackTrace();
         }
      }
   });

   public static void main(String[] args) {
      try {
         launch(args);
         client.close();
      } catch (IOException ioE) {
         ioE.printStackTrace();
      }
   }

}