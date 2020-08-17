import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Parent;
import javafx.scene.text.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;
import java.util.ArrayList;

public class ChatAppClient extends Application {

   private TextArea messages = new TextArea();
   private TextField input;
   private TextField name;
   private Button process;
   private static String userName = "unnamed soul";
   private Text addName;
   private static String ipAdress = null;
   private DataInputStream dis;
   private DataOutputStream dos;
   private static Socket client;
   private String reveivedMessage;
   private ProcessUserInf pui;
   private Stage mainStage;
   private ArrayList<String> nameList = new ArrayList<String>();

   public Parent enterNameScene() {
      name = new TextField();
      process = new Button();
      process.setText("Los gehts!");

      HBox firstScene = new HBox(20, name, process);
      firstScene.setPrefSize(400, 400);

      process.setOnMouseClicked(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent arg0) {
            userName = name.getText();
            pui = new ProcessUserInf();
            pui.start();
            try {
               pui.join(1000);
            } catch (InterruptedException ie) {
               ie.printStackTrace();
            }
            mainStage.setScene(new Scene(chatChoiceScene()));
         }
      });

      return firstScene;
   }

   public Parent chatChoiceScene() {

      VBox chatListe = new VBox();
      chatListe.setPrefSize(400, 400);

      System.out.println(pui.getConnectedClientList().toString());

      Button groupChat = new Button();
      groupChat.setText("to all");
      chatListe.getChildren().add(groupChat);

      Button clientsUpdate = new Button();
      clientsUpdate.setText("View Connected Clients (Update)");
      chatListe.getChildren().add(clientsUpdate);

      groupChat.setOnMouseClicked(new EventHandler<MouseEvent>() {

         @Override
         public void handle(MouseEvent arg0) {
            try {

               mainStage.setScene(new Scene(groupScene()));
            } catch (UnknownHostException uhe) {
               uhe.printStackTrace();
            }

            catch (IOException iOE) {
               iOE.printStackTrace();
            }
         }
      });

      clientsUpdate.setOnMouseClicked(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent arg0) {

            String tmp = "placeholder";

            for (int i = 0; i < pui.getConnectedClientList().size(); i++) {

               if (nameList.size() == 0) {
                  Button userChat = new Button();
                  userChat.setText(pui.getConnectedClientList().get(i));
                  nameList.add(pui.getConnectedClientList().get(i));
                  chatListe.getChildren().add(userChat);
               } else {

                  if (pui.getConnectedClientList().get(i).equals(nameList.get(i))) {

                  } else {

                     Button userChat = new Button();
                     userChat.setText(pui.getConnectedClientList().get(i));
                     nameList.add(pui.getConnectedClientList().get(i));
                     chatListe.getChildren().add(userChat);
                  }
               }
            }
         }
      });

      return chatListe;
   }

   public Parent groupScene() throws UnknownHostException, IOException {

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

      listeningMessages.start();

      // Defining JavaFx Application-Layout

      messages.setPrefHeight(220);
      input = new TextField();
      addName = new Text(userName);

      VBox root = new VBox(20, messages, input);
      root.setPrefSize(400, 400);

      System.out.println(pui.getConnectedClientList().toString());

      // Setting up action for Textfield when pressing Enter

      input.setOnAction(new EventHandler<ActionEvent>() {

         @Override
         public void handle(ActionEvent event) {

            try {

               // Defining and initialising Input-String

               String message = userName + ": ";
               message += input.getText();

               // Opening OutputStream for String to send it to the server
               dos = new DataOutputStream(client.getOutputStream());

               // Giving the String to the OutputStream
               dos.writeUTF(message);

               // Clearing the Textfield from Input-Text
               input.clear();

            } catch (IOException ioE) {
               ioE.printStackTrace();
            }
         }
      });

      return root;

   }

   public String getUserName() {
      return userName;
   }

   public String getIPAdress() {
      return ipAdress;
   }

   Thread listeningMessages = new Thread(new Runnable() {
      @Override
      public void run() {

         try {
            // Setting up InputStream for the Socket to receive the String back from the
            // server

            dis = new DataInputStream(client.getInputStream());

            // Reading String from InputStream
            while (true) {
               reveivedMessage = dis.readUTF();

               // Closing In-and OutputStreams + Socket

               // Showing the String in the TextArea "messages"

               messages.appendText(reveivedMessage + "\n");

            }
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
      }
   });

   public void start(Stage mainStage) {

      this.mainStage = mainStage;

      mainStage.setTitle("ChatApp");
      mainStage.setScene(new Scene(enterNameScene()));
      mainStage.show();

   }

   public static void main(String[] args) {
      try {
         launch(args);
         client.close();

         if (client.isClosed()) {

            client = new Socket(ipAdress, 4999);
            System.out.println("Connected");

            DataOutputStream connectionStateStreamOut = new DataOutputStream(client.getOutputStream());

            String notConnected = "0";

            connectionStateStreamOut.writeUTF(notConnected);

         }
      } catch (IOException ioE) {
         ioE.printStackTrace();
      }
   }

}