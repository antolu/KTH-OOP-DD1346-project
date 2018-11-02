import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.lang.Integer;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.Character;

import java.util.HashMap;

public class Encrypter {
    public static final String[] SUPPORTED_ENCRYPTIONS = {"AES", "Caesar"};
    private static final char[] lowercase = {'a', 'b', 'c', 'd', 'e', 'f', 'g',
        'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
        'v', 'w', 'x', 'y', 'z', 'a', 'a', 'o'};

    private static final char[] uppercase = {'A', 'B', 'C', 'D', 'E', 'F', 'G',
        'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
        'V', 'W', 'X', 'Y', 'Z', 'A', 'A', 'O'};

    private static final HashMap<Character, Integer> lowercaseMap = new HashMap<>();
    private static final HashMap<Character, Integer> uppercaseMap = new HashMap<>();

    public static void initialize() {

        for (int i = 0; i < lowercase.length; i++) {
            lowercaseMap.put(lowercase[i], i);
            uppercaseMap.put(uppercase[i], i);
        }
    }

    public static byte[] encrypt(String type, String key, byte[] toEncrypt) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            return toEncrypt;
        }
    }

    public static byte[] decrypt(String type, String key, byte[] toDecrypt) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            return toDecrypt;
        }
    }
    
    private static byte[] decryptCaesar(String key, byte[] toDecrypt) {
        try {
            int secretKey = Integer.parseInt(key);
            String string = Transcriber.byteToString(toDecrypt);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append(uppercase[(uppercaseMap.get(c) - secretKey) % lowercase.length]);
                }
                else if (Character.isLowerCase(c)) {
                    sb.append(lowercase[(lowercaseMap.get(c) - secretKey) % lowercase.length]);
                }
                else {
                    sb.append(c);
                }
            }

            return Transcriber.stringToByte(sb.toString());
        } catch (NumberFormatException e) {
            return toDecrypt;
        }
    }

    private static byte[] decryptBlowfish(String key, byte[] toDecrypt) {
        return toDecrypt;
    }

    private static byte[] decryptAES(String key, byte[] toDecrypt) throws Exception {
        // byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        // SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(toDecrypt);
    }

    private static byte[] decryptRSA(String key, byte[] toDecrypt) {
        return toDecrypt;
    }

    private static byte[] encryptCaesar(String key, byte[] toEncrypt) {
        try {
            int secretKey = Integer.parseInt(key);
            String string = Transcriber.byteToString(toEncrypt);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append(uppercase[(uppercaseMap.get(c) + secretKey) % lowercase.length]);
                }
                else if (Character.isLowerCase(c)) {
                    sb.append(lowercase[(lowercaseMap.get(c) + secretKey) % lowercase.length]);
                }
                else {
                    sb.append(c);
                }
            }

            return Transcriber.stringToByte(sb.toString());
        } catch (NumberFormatException e) {
            return toEncrypt;
        }
    }

    private static byte[] encryptBlowfish(String key, byte[] toEncrypt) {
        return toEncrypt;
    }

    private static byte[] encryptAES(String key, byte[] toEncrypt) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(toEncrypt);
    }

    private static byte[] encryptRSA(String key, byte[] toEncrypt) {
        return toEncrypt;
    }
}