import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.

public class FileHandler{
	
	/**
	 * if a file-progress already is taking place
	 */
	public volatile boolean isRunning; 
	public volatile boolean requestAnswered; 
	public Socket socket=null; 
	public File file=null;
	public User user = null;
	public String encr = "";
	public String key = null; 
	
	public FileHandler() {
		isRunning = true;
		requestAnswered = false;
	}
	
	/**
	 * Send a response for a file request
	 * 
	 * @param socket, the socket from which the communication goes through
	 * @param message, the accompaning message
	 * @param reply, yes/no (accept/decline the file request
	 */
	private static void SendResponse(Socket inSocket, String message, String reply) {
		socket = inSocket;
		//Skicka vidare till XML-creator, få ett response skicka ut
	}
	
	/**
	 * Creates a pop-up window with a file request
	 * @param filerequest, containing optional accompanning message, file size and file name
	 * @param socket, from  which socket the message came from
	 */
	public static void ShowFileRequest(FileRequest filerequest, Socket inSocket, User inUser) {
		
		user = inUser;
		
		JFrame fileRequest = new JFrame("File Request");
        fileRequest.setSize(400,300);
        
        String filesize = filerequest.getFileSize();
        String filename = filerequest.getFileName();
        String fileSender = user.getName();
        
        JLabel fileInfo = new JLabel("<html>You have gotten a request for a file transfer"+
        " from: "+fileSender+". Name of file: "+filename+". Filesize: "+filesize+". Accept?</html>", JLabel.CENTER);
        fileInfo.setBounds(50,20,300,80);

        JLabel messageText = new JLabel("<html>Optional message to accompany request answer: </html>", JLabel.CENTER);
        messageText.setBounds(100,140,200,80);
        
        JTextField message = new JTextField();
        message.setBounds(100,200,200,40);
        
        JButton accept = new JButton("Yes");
        JButton decline = new JButton("No");
        accept.setBounds(100,100,60,40);
        decline.setBounds(240,100,60,40);

        accept.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		SendResponse(inSocket, message.getText(), "Yes");
                fileRequest.dispose();          
        	}
        }); 
		
		decline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SendResponse(inSocket, message.getText(), "No");
		        fileRequest.dispose();
		    }
		});
		
		fileRequest.add(fileInfo);
		fileRequest.add(accept);
		fileRequest.add(decline);
		fileRequest.add(messageText);
		fileRequest.add(message);
		
		fileRequest.setLayout(null);
		fileRequest.setVisible(true);
	}
	
	/**
	 * Creates a progress bar showing the progress of the file transfer
	 */
	public static void ShowFileTransferProgress(String username) {
		
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
	public void sendFileRequest(String message, int port, File file, String inEncr, String inKey, User inUser) {
		
		user = inUser; 
		String username = user.getName();
		encr = inEncr;
		key = inKey;
		
		Thread t = new Thread(username){
            public void run(){
               	int countDown = 60;
               	
                while(true){
                    	
                	if(countDown<=0){
                		displayQueryError(user);
                    	isRunning = false;
                		socket.close();
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
		
		t.start();
		
	}
	
	/**
	 * If the file request was not approved, display why
	 * 
	 * @param user, the user that the file request was initially sent to 
	 */
	private void displayQueryError(User user) {
		
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
	public void handleResponse(String response) {
		
		requestAnswered = true;
		//
		//all the things used as file,socket etc needs to be saved somewhere before
		if(response=="yes") {
			
			showFileTransferProgress(user.getName());
			byte [] byteArray  = new byte [(int)file.length()];
			
			if(encr!=null) {
				byteArray = Encrypt.encrypt(encr, key, byteArray);
			}
			
			try {
		        FileInputStream fInputStream = new FileInputStream(file);
		        BufferedInputStream bInputStream = new BufferedInputStream(fInputStream);
		        bInputStream.read(byteArray,0,byteArray.length);
		        OutputStream os = socket.getOutputStream();
		        os.write(byteArray,0,byteArray.length);
		        os.flush();
			}catch(IOException e) {
				//Do something
			}
		}
		else {
			socket.close();
			
			JFrame requestResponse=new JFrame("Warning");
			
            JButton respondButton=new JButton("OK");  
            respondButton.setBounds(50,100,95,30);  
            
            JLabel myLabel = new JLabel("Your message request was denied");
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
	}

}
	
	
