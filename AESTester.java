
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.util.Base64;

public class AESTester {
    public AESTester() throws Exception {
        Encrypter.initialize();

        String toEncrypt = "Hello world!";
        System.out.println("To encrypt: " + toEncrypt);

        System.out.println("Encrypting...");

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        SecretKey aesKey = kgen.generateKey();
        String stringKey = Base64.getEncoder().encodeToString(aesKey.getEncoded());

        System.out.println("Encryption key: " + stringKey);

        String encrypted = Transcriber.byteToString(Encrypter.encrypt("AES", stringKey, Transcriber.stringToByte(toEncrypt)));

        System.out.println("Encrypted string: " + encrypted);

        String decrypted = Transcriber.byteToString(Encrypter.decrypt("AES", stringKey, Transcriber.stringToByte(encrypted)));

        System.out.println("Decrypted string: " + decrypted);
    }

    public static void main(String[] args) throws Exception {
        new AESTester();
    }
}