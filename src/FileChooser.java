import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.filechooser.FileSystemView;

import java.net.ServerSocket;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 *  Class where user chooses file to send
 */
public class FileChooser{

    /**
     *
     * @param currentEncryption the type of encryption used
     * @param encrKey the encryption key
     * @param users list of the users
     * @param fileHandler the filehandler that will transfer the file etc
     */
    public FileChooser(String currentEncryption, String encrKey, List<User> users, FileHandler fileHandler){

        //Create file chooser
        final JFileChooser jfc = new JFileChooser();
        int returnValue = jfc.showOpenDialog(null);

        //Wait for a file to be selected
        if(returnValue == JFileChooser.APPROVE_OPTION){

            File selectedFile;
            selectedFile = jfc.getSelectedFile();

            //Create message-input frame
            JFrame messageFrame = new JFrame();
            JTextField sendMessageText = new JTextField();
            sendMessageText.setPreferredSize(new Dimension(200,40));
            JLabel messageLabel = new JLabel("Write a message to accompany the file");
            JButton sendMessageB = new JButton("Send");

            messageFrame.add(sendMessageText);
            messageFrame.add(messageLabel);
            messageFrame.add(sendMessageB);

            messageFrame.setLayout(new FlowLayout());
            messageFrame.setSize(300,200);
            messageFrame.setVisible(true);

            //Add action listener to the send-button
            sendMessageB.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){

                    int portNumber=0;
                    int max = 8000;
                    int min = 4000;
                    boolean foundPort = false;
                    ServerSocket fileSocket= null;

                    Random randNumbr = new Random();

                    //Create a socket with random port number for the outgoing file-request
                    while(!foundPort){
                        portNumber = randNumbr.nextInt((max-min)+1)+min;

                        try{
                            fileSocket = new ServerSocket(portNumber);
                            System.out.println(portNumber);
                            foundPort = true;
                        }catch(IOException e1) {
                            System.out.println("Failed to bind to port");
                        }
                    }

                    messageFrame.dispose();

                    //Call method where the file is sent
                    fileHandler.sendFileRequest(fileSocket, users.get(0).getClientSocket(), sendMessageText.getText(),
                            portNumber, selectedFile, currentEncryption, encrKey);
                }
            });
        }
    }
}
