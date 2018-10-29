public class SocketClient{
	
	
	Socket myClient;
	
	/**
	 * Constructor that gets a "pointer" to the backend, and the socket that was initialized in the backend
	 * @param socket, through all communication with the client is happening
	 * @param backend, to be able to "talk" with the backend
	 */
	public SocketClient(Socket socket, Backend backend) {

	}
	
	/**
	 * 
	 */
	public void notifyObservers(Object obj) {
		
	}
	
	/**
	 * Where the socket receives messages and sends them to the parser and to notify observers
	 */
	public void receive() {
		
	}
	
	/**@param
	 * Where the socket sends messages 
	 * @param String msg, the message to be sent 
	 */
	public void Send(String msg) {
		
	}
	
}
