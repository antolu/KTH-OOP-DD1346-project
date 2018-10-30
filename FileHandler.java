public class FileHandler{
	
	/**
	 * if a file-progress already is taking place
	 */
	public volatile boolean isRunning = false; 
	
	public FileHandler() {
		
	}
	
	/**
	 * Send a response for a file request
	 * 
	 * @param socket, the socket from which the communication goes through
	 * @param message, the accompaning message
	 * @param reply, yes/no (accept/decline the file request
	 */
	private static void SendResponse(Socket socket, String message, String reply) {
	 //	socket.write(reply+message);
	}
	
	/**
	 * Creates a pop-up window with a file request
	 * @param filerequest, containing optional accompanning message, file size and file name
	 * @param socket, from  which socket the message came from
	 */
	public static void ShowFileRequest(FileRequest filerequest, Socket socket) {
		
	}
	
	/**
	 * Creates a progress bar showing the progress of the file transfer
	 */
	public static void ShowFileTransferProgress(parent) {
		
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
	public void sendFileRequest(String message, int port, File file, String encr, String key, User user) {
		
		//Create thread, count down 60 sec. call displayQueryError, return if reach zero, 
		//isRunning = false;
		
	}
	
	/**
	 * If the file request was not approved, display why
	 * 
	 * @param user, the user that the file request was initially sent to 
	 */
	private void displayQueryError(User user) {
		
	}
	
	/**
	 * Called when a response is received. Sends file or shut socket down
	 * 
	 * @param response, 
	 */
	public void handleResponse(String response) {
		// if response is yes: send file
		// if response is no: socket.close() and pop up window, unable to send
	}
	
	
	
}
