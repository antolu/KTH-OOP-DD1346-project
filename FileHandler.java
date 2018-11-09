import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.awt.event.*;


public class FileHandler{

    /**
     * if a file-progress already is taking place
     */
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
    private static void SendResponse(String message, String reply) {

        System.out.println("SendResponse");
        String responseMessage = Composer.composeFileResponse(message,reply);
        System.out.println("Sendresponse "+responseMessage);

            socketClient.send(responseMessage);
           /* PrintWriter out2 = new PrintWriter(client.getOutputStream(), true);
            out2.write(responseMessage);
            out2.write()*/
            System.out.println("Created out--writer");

    }

    /**
     * Creates a pop-up window with a file request
     * @param filerequest, containing optional accompanning message, file size and file name
     * @param socket, from  which socket the message came from
     */
    public static void ShowFileRequest(FileRequest filerequest, SocketClient messageSocket) {

        isRunning = true;
        socketClient = messageSocket;
        String host = filerequest.getIP();

        int port = Integer.parseInt(filerequest.getPort());

        try{
            mySocket = new Socket("localhost",port);
        }catch(IOException e){
            //do something
            System.out.println("Could not connect to port");
            return;
        }
        //System.out.println(host);
        //System.out.println(port);
        /*try {
            client = new Socket("localhost", port);
        }catch(IOException e) {
            //do something
        }*/

        JFrame fileRequestFrame = new JFrame("File Request");
        fileRequestFrame.setSize(400,300);
        System.out.println(filerequest.getFileSize());
        int fileSize = Integer.parseInt(filerequest.getFileSize());
        String filename = filerequest.getFileName();
        String fileSender = user.getName();

        JLabel fileInfo = new JLabel("<html>You have gotten a request for a file transfer"+
                " from: "+fileSender+". Name of file: "+filename+". Filesize: "+fileSize+". Accept?</html>", JLabel.CENTER);
        fileInfo.setBounds(50,20,300,80);

        JLabel messageText = new JLabel("<html>Optional message to accompany request answer: </html>", JLabel.CENTER);
        messageText.setBounds(100,140,200,80);

        JTextField message = new JTextField();
        message.setBounds(100,200,200,40);

        JButton accept = new JButton("Yes");
        JButton decline = new JButton("No");
        accept.setBounds(100,100,60,40);
        decline.setBounds(240,100,60,40);
       // BufferedInputStream in =null;

        accept.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                SendResponse(message.getText(), "Yes");

                File f = new File(filename);

                try{
                    InputStream in = null;
                    OutputStream out = null;

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
                    double percentageReceived=0;
                    int count;
                    double totalReceived=0.0;

                    JFrame frame = new JFrame("JFrame Example");
                    JButton button = new JButton();
                    JPanel panel = new JPanel(new FlowLayout());
                    panel.add(button);

                    button.setText("Press me");
                    button.setPreferredSize(new Dimension(100, 30));

                    frame.getContentPane().add(panel);
                    panel.setSize(300, 300);
                   // frame.setLocationRelativeTo(null);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                    frame.pack();
                    frame.setVisible(true);
                    fileRequestFrame.dispose();

                    /*JProgressBar progressBar = new JProgressBar();
                    JLabel progressLabel = new JLabel("Current kb of file to download from user "+user.getName());

                    progressBar.setMinimum(0);
                    progressBar.setMaximum(100);
                    progressBar.setValue(0);
                    //testFrame.add(progressBar);
                    testFrame.add(progressLabel);
                    testFrame.setLayout(new FlowLayout());
                    testFrame.setSize(200,200);
                    testFrame.setVisible(true);
                    System.out.println("created frame");*/

                    while ((count = in.read(bytes)) > 0) {


                        System.out.println(totalReceived);
                        totalReceived = totalReceived+count;
                        percentageReceived = totalReceived/fileSize*100.0;
                       // progressBar.setValue((int)percentageReceived);
                       // fileRequestFrame.repaint();
                        out.write(bytes, 0, count);


                        try{
                            Thread.sleep(50);
                        }catch(InterruptedException e4){
                            //do something
                        }
                    }

                    try{
                        Thread.sleep(2000);
                    }catch(InterruptedException e3){
                        //do something
                    }
                    frame.dispose();

                }catch(IOException e2 ) {
                    //do something
                }

                isRunning = false;
            }
        });

        decline.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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

        fileRequestFrame.add(fileInfo);
        fileRequestFrame.add(accept);
        fileRequestFrame.add(decline);
        fileRequestFrame.add(messageText);
        fileRequestFrame.add(message);

        fileRequestFrame.setLayout(null);
        fileRequestFrame.setVisible(true);
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
        //Create in and out-writer
        isRunning = true;

        Thread t = new Thread(username){
            public void run(){
                int countDown = 60;

                while(true){

                    if(countDown<=0){
                        displayQueryError();
                        isRunning = false;
                        try {
                            server.close();
                        }catch(IOException e) {
                            //do something
                        }

                        return;
                    }
                    if(requestAnswered) {
                        isRunning=false;
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

        String requestMessage = Composer.composeFileRequest(message, file.getName(), Long.toString(file.length()),
                Integer.toString(port), encr, inKey);

        messageSocket.send(requestMessage);

        t.start();
    }

    /**
     * If the file request was not approved, display why
     *
     * @param user, the user that the file request was initially sent to
     */
    private void displayQueryError() {

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

    public boolean getRunningStatus() {
        return isRunning;
    }


    /**
     * Called when a response is received. Sends file or shut socket down
     *
     * @param response,
     */
    public void handleResponse(FileResponse fileResponse) throws InterruptedException {


        requestAnswered = true;
        Socket clientSocket=null;

        String reply = fileResponse.getReply();
        String responseMessage = fileResponse.getMessage();

        System.out.println("THE REPLY: "+reply);
        System.out.println(reply.equals("Yes"));
        //all the things used as file,socket etc needs to be saved somewhere before
        if(reply.equals("Yes")) {
            System.out.println("in if-statement");

            try{
                clientSocket = server.accept();
            }catch(IOException e ) {
                //do something
            }

            InputStream in=null;
            OutputStream out= null;

            JFrame progressFrame = new JFrame();
            JProgressBar progressBar = new JProgressBar();
            JLabel progressLabel = new JLabel("<html>Sending file to "+ user.getName()+"<br>Current kb of file transfer</html>");
            progressBar.setMinimum(0);
            progressBar.setMaximum(100);
            progressFrame.add(progressBar);
            progressFrame.add(progressLabel);
            progressFrame.setLayout(new FlowLayout());
            progressFrame.setSize(200,200);

            // Get the size of the file
            long length = file.length();
            System.out.println(length);
            byte[] bytes = new byte[1024];

            try {
                in = new FileInputStream(file);
            }catch(FileNotFoundException e6){
                //do something
            }

            try{
                out =clientSocket.getOutputStream();
            }catch(IOException e7){
                //do something
            }

            double percentageSent=0;
            int count;
            double totalSent=0.0;

            try{
                progressFrame.setLocationRelativeTo(null);
                progressFrame.setVisible(true);

                while ((count = in.read(bytes)) > 0) {

                    System.out.println(totalSent);
                    totalSent = totalSent+count;
                    percentageSent = totalSent/length*100.0;
                    progressBar.setValue((int)percentageSent);
                    progressFrame.repaint();

                    out.write(bytes, 0, count);
                    try{
                        Thread.sleep(50);
                    }catch(InterruptedException e){
                        //do something
                    }
                }

            }catch(IOException e7){
                //do something
            }

            progressFrame.dispose();

            //os.flush();
            Thread.sleep(5000);
            try {
                clientSocket.close();
                server.close();
            }catch(IOException e) {
                //do something
            }

        }else {
            try{
                server.close();
              //  clientSocket.close();
            }catch(IOException e) {
                //do something
            }

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
	
	
