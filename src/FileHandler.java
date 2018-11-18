import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.*;

import java.io.*;
import java.io.IOException;

import java.net.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Class that takes care of all things related to the file transfer
 */
public class FileHandler{

    private static volatile boolean isRunning=false;
    private volatile boolean requestAnswered=false;
    private ServerSocket server=null;
    private static Socket mySocket = null;
    private static SocketClient socketClient= null;
    private File file=null;
    private static User user = null;
    private String encr = "";
    private String key = "";
    private PrintWriter out = null;
    private byte[] bytes=null;

    /**
     * Constructor that maps the file handler to the relevant user
     * @param inUser
     */
    public FileHandler(User inUser) {
        this.isRunning = false;
        this.requestAnswered = false;
        this.user = inUser;
    }

    /**
     * Send a response for a file request
     *
     * @param message, the accompaning message
     * @param reply, yes/no (accept/decline the file request
     */
    private void SendResponse(String message, String reply) {

        String responseMessage = Composer.composeFileResponse(message,reply);
        socketClient.send(responseMessage);
    }

    /**
     * Creates a pop-up window with a file request and either downloads file or declines
     * @param filerequest, containing optional accompanning message, file size and file name
     * @param socket, from  which socket the message came from
     */
    public void ShowFileRequest(FileRequest fileRequest, SocketClient messageSocket) {

        isRunning = true;
        socketClient = messageSocket;
        String host = fileRequest.getIP();
        host = host.replaceAll("/", "").trim();

        int port = Integer.parseInt(fileRequest.getPort());
        int fileSize = Integer.parseInt(fileRequest.getFileSize());
        String filename = fileRequest.getFileName();
        String fileSender = user.getName();
        String requestMessage = fileRequest.getMessage();

        final String encryptionType = fileRequest.getEncryptionType();
        final String encryptionKey=fileRequest.getEncryptionKey();
        boolean isEncrypted = fileRequest.isEncrypted();
        System.out.println("Encryption: "+encryptionType+" "+encryptionKey+" is encrypted: "+isEncrypted);

       /* if(host.contains("localhost")){
            host="localhost";
        }*/


        //Creates a socket to communicate through
        try{
            mySocket = new Socket(host,port);
        }catch(IOException e){
            //do something
            System.out.println("Could not connect to port");
            return;
        }

        //Create file request window
        JFrame fileRequestFrame = new JFrame("File Request");
        fileRequestFrame.setLocationRelativeTo(null);
        fileRequestFrame.setSize(400,300);

        JLabel fileInfo = new JLabel("<html>You have gotten a request for a file transfer"+
                " from: "+fileSender+". Name of file: "+filename+". Filesize: "+fileSize+". <br>" +
                "Message: "+requestMessage+" Accept?</html>", JLabel.CENTER);
        fileInfo.setBounds(50,20,300,80);

        JLabel encryptionInfo= new JLabel("",JLabel.CENTER);
        if(isEncrypted){
            System.out.println("isencrypted");
            encryptionInfo.setText("<html>The file is encrypted with "+encryptionType+"</html>");
        }


        encryptionInfo.setBounds(50,5,300,20);

        JLabel messageText = new JLabel("<html>Optional message to accompany request answer: </html>", JLabel.CENTER);
        messageText.setBounds(100,140,200,80);

        JTextField message = new JTextField();
        message.setBounds(100,200,200,40);

        JButton accept = new JButton("Yes");
        JButton decline = new JButton("No");
        accept.setBounds(100,100,60,40);
        decline.setBounds(240,100,60,40);

        fileRequestFrame.add(fileInfo);
        fileRequestFrame.add(encryptionInfo);
        fileRequestFrame.add(accept);
        fileRequestFrame.add(decline);
        fileRequestFrame.add(messageText);
        fileRequestFrame.add(message);

        fileRequestFrame.setLayout(null);
        fileRequestFrame.setVisible(true);

        //Create progress bar
        JFrame progressFrame = new JFrame("Progress");
        JProgressBar progress=new JProgressBar(JProgressBar.HORIZONTAL,0,100);
        JLabel progressLabel = new JLabel("Downloading from user "+user.getName());

        final String AMOUNTLABEL = "Amount downloaded: ";
        JLabel amountReceived = new JLabel(AMOUNTLABEL);
        progress.setMinimum(0);
        progress.setMaximum(100);
        progress.setValue(0);
        progressFrame.add(progressLabel);
        progressFrame.add(amountReceived);
        progressFrame.add(progress);
        progressFrame.setLayout(new FlowLayout());
        progressFrame.setSize(300,200);

        //Add action listener to the accept-button
        accept.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                new Thread(new Runnable() {
                    public void run() {
                        fileRequestFrame.dispose();

                        //Send reply
                        SendResponse(message.getText(), "Yes");

                        File f = new File(filename);

                        try {
                            InputStream in = null;
                            OutputStream out = null;

                            //Create input and output streams
                            try {
                                in = mySocket.getInputStream();
                                System.out.println("Accept action listener: client created");
                            } catch (IOException ex) {
                                System.out.println("Can't get socket input stream. ");
                            }

                            try {
                                out = new FileOutputStream(filename);
                                System.out.println("Accept action listener: File output stream created");
                            } catch (FileNotFoundException ex) {
                                System.out.println("File not found. ");
                            }


                            byte[] bytes = new byte[1024];
                            double percentageReceived = 0;
                            int count;
                            double totalReceived = 0.0;

                            byte[] bigBytes=new byte[0];
                            //Read in file
                            while ((count = in.read(bytes)) > 0) {

                                totalReceived = totalReceived + count;
                                percentageReceived = totalReceived / fileSize * 100.0;

                                final int TMPPERCENT = (int) percentageReceived;
                                final int TMPRECEIVED = (int)totalReceived;
                                final int TMPFILE = fileSize;

                                //Update progress bar
                                SwingUtilities.invokeLater(new Runnable(){
                                    public void run(){
                                        progressFrame.setVisible(true);
                                        progress.setValue(TMPPERCENT);
                                        amountReceived.setText(AMOUNTLABEL+Integer.toString(TMPRECEIVED));
                                        progressFrame.repaint();

                                        if(TMPRECEIVED>TMPFILE){
                                            progressLabel.setText("<html>File bigger than anticipated, <br> continuing to downloading</html>");
                                        }
                                    }
                                });

                                if(count!=bytes.length){
                                    bigBytes = concatenateByteArra(bigBytes,bytes,bigBytes.length,count);
                                }
                                else {
                                    bigBytes = addByteArrays(bigBytes, bytes);
                                }

                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e4) {
                                    //do something
                                }
                            }

                            if(isEncrypted){
                                bigBytes = Encrypter.decrypt(encryptionType, encryptionKey, bigBytes);
                                out.write(bigBytes);
                                out.close();
                            }
                            else{
                                out.write(bigBytes);
                                out.close();
                            }



                            //When done, dispose and close socket
                            SwingUtilities.invokeLater(new Runnable(){
                                public void run(){
                                    progressFrame.dispose();
                                    isRunning = false;

                                    try{
                                        mySocket.close();
                                    }catch(IOException e1){
                                        //do something
                                    }
                                }
                            });

                        } catch (IOException e2) {
                            //do something
                        }
                    }
                }).start();
            }
        });

        decline.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                //Send response
                SendResponse(message.getText(), "No");
                fileRequestFrame.dispose();

                try{
                    mySocket.close();
                }catch(IOException e1) {
                    //do something
                }

                isRunning = false;
            }
        });
    }

    public byte[] addByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public byte[] concatenateByteArra(byte[] a, byte[] b, int aLength, int bLength) {
        byte[] result = new byte[aLength + bLength];
        System.arraycopy(a, 0, result, 0, aLength);
        System.arraycopy(b, 0, result, aLength, bLength);
        return result;
    }

    /**
     * Sends a file request to a client
     *
     * @param message, the (possibly empty) message accompaning the request
     * @param port, the port through which the file will be sent on
     * @param file, the file to be sent
     * @param encr, the (optional) encryption to be used
     * @param key, the key corresponding to the encryption
     * @param user, the object/person which the file is to be sent to
     */
    public void sendFileRequest(ServerSocket inServer, SocketClient messageSocket, String message,
                                int port, File inFile, String inEncr, String inKey) {

        String username = user.getName();
        encr = inEncr;
        key = inKey;
        file = inFile;

        server = inServer;
        setRunningStatus(true);
        InputStream in=null;

        try {
            in = new FileInputStream(file);
        }catch(FileNotFoundException e6){
            //do something
        }

        long length = file.length();
        bytes = new byte[(int)length];

        try {
            in.read(bytes);
        }catch(IOException e2){
            System.out.println("File unavailable, try again");
        }

        if(!encr.equals("")){
            bytes = Encrypter.encrypt(encr, key, bytes);
        }

        //Send request message
        String requestMessage = Composer.composeFileRequest(message, file.getName(), Integer.toString(bytes.length),
                Integer.toString(port), encr, inKey);

        messageSocket.send(requestMessage);
        try{
            in.close();
        }catch(IOException e4){
            //do something
        }
        //Wait for message for 60 seconds
        Thread t = new Thread(username){
            public void run(){
                int countDown = 60;

                while(true){

                    //If no answer has been received within 60 seconds
                    if(countDown<=0){
                        isRunning = false;
                        bytes=null;
                        displayQueryError();
                        try {
                            server.close();
                        }catch(IOException e) {
                            //do something
                        }

                        return;
                    }
                    if(requestAnswered) {
                        return;
                    }

                    countDown--;

                    try{
                        sleep(1000);
                    }catch(InterruptedException e){
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        t.start();
    }

    /**
     * If the file request was not approved, display why
     *
     * @param user, the user that the file request was initially sent to
     */
    private void displayQueryError() {

        //Create warning-window
        JFrame requestResponse=new JFrame("Warning");

        JButton respondButton=new JButton("OK");
        respondButton.setBounds(50,100,95,30);

        JLabel myLabel = new JLabel("Timeout for your request to "+user.getName());
        myLabel.setSize(250,50);

        requestResponse.add(respondButton);
        requestResponse.add(myLabel);
        requestResponse.setSize(250,200);
        requestResponse.setLayout(null);
        requestResponse.setVisible(true);

        respondButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                requestResponse.dispose();
            }
        });
    }

    /**
     * If a file is being processed at the moment
     * @return boolean isRunning
     */
    public boolean getRunningStatus() {
        return isRunning;
    }

    public void setRunningStatus(boolean inRunning){
        isRunning =inRunning;
    }


    /**
     * Called when a response is received. Sends file or shut socket down
     *
     * @param response,
     */
    public void handleResponse(FileResponse fileResponse) throws InterruptedException {

        //Stops the thread
        requestAnswered = true;
        Socket clientSocket=null;

        String reply = fileResponse.getReply();
        String responseMessage = fileResponse.getMessage();
        //Check if process is still happening
      //  System.out.println(getRunningStatus());
        if(!getRunningStatus()){
         //   System.out.println("running status");
            return;
        }
        //Send file
        if(reply.equals("Yes")) {

            try{
                clientSocket = server.accept();
            }catch(IOException e ) {
                //do something
            }

            OutputStream out= null;

            //Create frame and progress bar
            JFrame progressFrame = new JFrame();
            JProgressBar progressBar = new JProgressBar();
            JLabel userInfo = new JLabel("<html>Response: "+responseMessage+"<br>Sending file to "+ user.getName()+"</html>");
            String pInfo = "Current amout of kb transfered: ";
            JLabel progressInfo = new JLabel();
            progressBar.setMinimum(0);
            progressBar.setMaximum(100);
            progressFrame.add(userInfo);
            progressFrame.add(progressBar);
            progressFrame.add(progressInfo);
            progressFrame.setLayout(new FlowLayout());
            progressFrame.setSize(300,200);

            try{
                out =clientSocket.getOutputStream();
            }catch(IOException e7){
                //do something
            }

            double percentageSent=0.0;
            int count;
            double totalSent=0.0;

            try{
                progressFrame.setLocationRelativeTo(null);
                progressFrame.setVisible(true);

              //  if(!encr.equals("")){
             //       bytes = Encrypter.encrypt(encr, key, bytes);
             //   }

                progressInfo.setText(pInfo);

                double j = 0.0;

                for(int i =0; i<bytes.length;i++){
                    out.write(bytes,i,1);

                    j=i+1.0;
                    percentageSent = j / bytes.length * 100.0;
                   // System.out.println(percentageSent);
                    if(i%1024==0){
                        progressBar.setValue((int)percentageSent);
                        progressInfo.setText(pInfo+(int)j);
                        progressFrame.repaint();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e4) {
                            //do something
                        }
                    }

                    if(percentageSent==100){
                        progressBar.setValue(100);

                    }
                }

                //Start sending file
             /*   while ((count = in.read(bytes)) > 0) {

                    totalSent = totalSent+count;
                    percentageSent = totalSent / length * 100.0;
                    if(!encr.equals("")){
                        bytes = Encrypter.encrypt(encr, key, bytes);
                    }

                    //Update progress bar
                    progressBar.setValue((int)percentageSent);
                    progressInfo.setText(pInfo+(int)totalSent);
                    progressFrame.repaint();
                    out.write(bytes, 0, bytes.length);

                    try{
                        Thread.sleep(50);
                    }catch(InterruptedException e){
                        //do something
                    }
                }*/

            }catch(IOException e7){
                //do something
            }

            progressInfo.setText("The file has been transfered!");

            Thread.sleep(5000);

            progressFrame.dispose();

            //os.flush();

            try {
                clientSocket.close();
                server.close();
                bytes = null;
            }catch(IOException e) {
                //do something
            }

        }else {
            try{
                server.close();
                bytes = null;
              //  clientSocket.close();
            }catch(IOException e) {
                //do something
            }

            //Create decline-window
            JFrame requestResponse=new JFrame("Warning");

            JButton respondButton=new JButton("OK");
            respondButton.setBounds(50,100,95,30);

            JLabel myLabel = new JLabel("Your message request was denied, with message: "+responseMessage);
            myLabel.setSize(250,50);

            requestResponse.add(respondButton);
            requestResponse.add(myLabel);
            requestResponse.setSize(250,200);
            requestResponse.setLayout(null);
            requestResponse.setVisible(true);

            respondButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    requestResponse.dispose();
                }
            });
        }
        isRunning = false;
    }
}
	
	
