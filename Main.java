import java.io.*;
import java.util.*;

public class Main {

    private static String getMessage(){
        try{
            BufferedReader buffer = new BufferedReader(new FileReader("message.txt"));
            String line;
            if ((line = buffer.readLine()) == null) {
                System.exit(1);
            }
            buffer.close();
            return line;
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    public static void main(String[] args) {
        new StartFrame();
        // String message = getMessage();

        // Message parsedMessage = Transcriber.parse(message);

        // System.out.println(parsedMessage);
    }
}