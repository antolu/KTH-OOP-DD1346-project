import java.io.*;
import java.util.*;

import java.net.Socket;

import javax.swing.JFrame;

public class Main {

    // private static String getMessage(){
    //     try{
    //         BufferedReader buffer = new BufferedReader(new FileReader("message.txt"));
    //         String line;
    //         if ((line = buffer.readLine()) == null) {
    //             System.exit(1);
    //         }
    //         buffer.close();
    //         return line;
    //     }
    //     catch(IOException e){
    //         System.out.println(e.getMessage());
    //         System.exit(1);
    //     }
    //     return "";
    // }

    public static void main(String[] args) {
        // // Test ChatWindow
        // try {
        //     JFrame frame = new JFrame();
        //     // ChatWindow chatWindow = new ChatWindow();
        //     // frame.getContentPane().add(chatWindow);
        //     User user = new User("Anton", "ID", new Socket());
        //     ChatPane chatPane = new ChatPane(user, frame);
            
        //     frame.add(chatPane);

        //     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //     frame.pack();
        //     frame.setVisible(true);

        //     // Thread.sleep(4000);
        //     // Message msg = new Message("Eta 09.00", "0000FF", "08:46", "Elisabet");
        //     // chatWindow.addMessage(msg);
        // } catch(Exception e) {
        //     e.printStackTrace();
        //     System.exit(1);
        // }


        new StartFrame();
        // String message = getMessage();
        // Encrypter.initialize();
        // Query parsedMessage = Transcriber.parse(message);

        // System.out.println(parsedMessage);
    }

    public static void startMainFrame(ourStruct info) {
        new Backend(info);
    }
}