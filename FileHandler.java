// import javax.swing.JFrame;
// import javax.swing.JOptionPane;

// import 

// public class FileHandler{
	
// 	/**
// 	 * if a file-progress already is taking place
// 	 */
// 	public volatile boolean isRunning= false; 
// 	public volatile boolean requestAnswered = false; 
// 	private Socket socket; 
	
// 	public Thread t;
	
// 	public FileHandler() {
		
// 	}
	
// 	/**
// 	 * Send a response for a file request
// 	 * 
// 	 * @param socket, the socket from which the communication goes through
// 	 * @param message, the accompaning message
// 	 * @param reply, yes/no (accept/decline the file request
// 	 */
// 	private void SendResponse(Socket inSocket, String message, String reply) {
// 		socket = inSocket;
// 	 //	socket.write(reply+message);
// 	}
	
// 	/**
// 	 * Creates a pop-up window with a file request
// 	 * @param filerequest, containing optional accompanning message, file size and file name
// 	 * @param socket, from  which socket the message came from
// 	 */
// 	public static void ShowFileRequest(FileRequest filerequest, Socket socket) {
		
// 	}
	
// 	/**
// 	 * Creates a progress bar showing the progress of the file transfer
// 	 */
// 	public static void ShowFileTransferProgress(JFrame parent) {
		
// 	}
	
// 	/**
// 	 * Sends a file request to a client
// 	 * 
// 	 * @param message, the (possibly empty) message accompaning the request
// 	 * @param port, the port through which the file will be sent on
// 	 * @param file, the file to be sent
// 	 * @param encr, the (optional) encryption to be used
// 	 * @param key, the key corresponding to the encryption
// 	 * @param user, the object/person which the file is to be sent to
// 	 */
// 	public void sendFileRequest(String message, int port, File file, String encr, String key, User user) {
		
// 		username = user.getName();
		
// 		t = new Thread(username){
//             public void run(){
//                	int countDown = 60;
//                	isRunning = true;
//                	requestAnswered = false;
               	
//                 while(true){
                    	
//                 	if(countDown<=0){
//                 		displayQueryError();
//                     	//isRunning = false;
//                         return;
//                     }
//                 	if(requestAnswered==true) {
//                 		isRunning=false;
//                 		return;
//                 	}

//                     countDown--;

//                     try{
//                     	sleep(1000);
//                     }catch(InterruptedException e){
//                     	throw new RuntimeException(e);
//                     }
//                 }
//             }       
// 		};
		
// 		t.start();
		
		
// 	}
	
// 	/**
// 	 * If the file request was not approved, display why
// 	 * 
// 	 * @param user, the user that the file request was initially sent to 
// 	 */
// 	private void displayQueryError(User user) {
		
// 	}
	
// 	public boolean getRunningStatus() {
// 		return isRunning;
// 	}
	
	
// 	/**
// 	 * Called when a response is received. Sends file or shut socket down
// 	 * 
// 	 * @param response, 
// 	 */
// 	public void handleResponse(String response) {
		
// 		requestAnswered = true;
// 		//all the things used as file,socket etc needs to be saved somewhere before
// 		// if(response=="yes") {
// 		// 	showFileTransferProgress();
// 		// 	byte [] byteArray  = new byte [(int)file.length()];
// 	    //     fInputStream = new FileInputStream(file);
// 	    //     bInputStream = new BufferedInputStream(fInputStream);
// 	    //     bInputStream.read(byteArray,0,byteArray.length);
// 	    //     OutputStream os = socket.getOutputStream();
// 	    //     os.write(byteArray,0,byteArray.length);
// 	    //     os.flush();
// 		// }
// 		// else {
// 		// 	socket.close();
// 		// 	 // create a jframe
// 		//     JFrame requestDialog = new JFrame("JOptionPane showMessageDialog example");
		    
// 		//     // show a joptionpane dialog using showMessageDialog
// 		//     JOptionPane.showMessageDialog(requestDialog,
// 		//         "Your request to send file was denied");
		    
// 		//     //exit
// 		//     }
// 		}

// 	}
