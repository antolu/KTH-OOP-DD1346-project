import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypter {
    public static final String[] SUPPORTED_ENCRYPTIONS = {"AES"};

    public static byte[] encrypt(String type, String key, byte[] toEncrypt) {
        if (type.equals("caesar"))
            return encryptCaesar(key, toEncrypt);
        else if (type.equals("blowfish")) 
            return encryptBlowfish(key, toEncrypt);
        else if (type.equals("AES"))
            return encryptAES(key, toEncrypt);
        else if (type.equals("RSA"))
            return encryptRSA(key, toEncrypt);
        else 
            return toEncrypt;
    }

    public static byte[] decrypt(String type, String key, byte[] toDecrypt) {
        if (type.equals("caesar"))
            return decryptCaesar(key, toDecrypt);
        else if (type.equals("blowfish")) 
            return decryptBlowfish(key, toDecrypt);
        else if (type.equals("AES"))
            return decryptAES(key, toDecrypt);
        else if (type.equals("RSA"))
            return decryptRSA(key, toDecrypt);
        else 
            return toDecrypt;
    }
    
    private static byte[] decryptCaesar(String key, byte[] toDecrypt) {
        return toDecrypt;
    }

    private static byte[] decryptBlowfish(String key, byte[] toDecrypt) {
        return toDecrypt;
    }

    private static byte[] decryptAES(String key, byte[] toDecrypt) {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(plainText);
    }

    private static byte[] decryptRSA(String key, byte[] toDecrypt) {
        return toDecrypt;
    }

    private static byte[] encryptCaesar(String key, byte[] toEncrypt) {
        return toEncrypt;
    }

    private static byte[] encryptBlowfish(String key, byte[] toEncrypt) {
        return toEncrypt;
    }

    private static byte[] encryptAES(String key, byte[] toEncrypt) {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plainText);
    }

    private static byte[] encryptRSA(String key, byte[] toEncrypt) {
        return toEncrypt;
    }
}