import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.util.Base64;

import java.lang.Integer;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.Character;

import java.util.HashMap;

/**
 * Static methods to do all encryptions
 */
public class Encrypter {
    public static final String[] SUPPORTED_ENCRYPTIONS = {"AES", "Caesar"};
    private static final char[] LOWERCASE = {'a', 'b', 'c', 'd', 'e', 'f', 'g',
        'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
        'v', 'w', 'x', 'y', 'z'};

    private static final char[] UPPERCASE = {'A', 'B', 'C', 'D', 'E', 'F', 'G',
        'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
        'V', 'W', 'X', 'Y', 'Z'};

    private static final HashMap<Character, Integer> LOWERCASE_MAP = new HashMap<>();
    private static final HashMap<Character, Integer> UPPERCASE_MAP = new HashMap<>();

    /**
     * Initializes the dictionaries recuired for Caesar encryption to function
     */
    public static void initialize() {
        for (int i = 0; i < LOWERCASE.length; i++) {
            LOWERCASE_MAP.put(LOWERCASE[i], i);
            UPPERCASE_MAP.put(UPPERCASE[i], i);
        }
    }

    /**
     * Public method to handle all encryptions. Returns an encrypted byte array
     * if the encryption type is supported.
     * @param type The encryption type
     * @param key The encryption key
     * @param toEncrypt The byte array to be encrypted
     * @return An encrypted byte array
     */
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

    /**
     * Public method to handle all decryptions. Returns a decrypted byte array
     * if the encryption type is supported.
     * @param type The encryption type
     * @param key The encryption key
     * @param toEncrypt The byte array to be decrypted
     * @return A decrypted byte array
     */
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
    
    /**
     * Decrypts caesar encoded byte array by first converting it to
     * string, decrypting, and lastly returning it as a byte array.
     * @param key The encryption key
     * @param toDecrypt The byte array to be decrypted
     * @return A decrypted byte array
     */
    private static byte[] decryptCaesar(String key, byte[] toDecrypt) {
        try {
            int secretKey = Integer.parseInt(key);
            String string = Transcriber.byteToString(toDecrypt);
            StringBuilder sb = new StringBuilder();

            /* Actually shift encrypt */
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if (Character.isUpperCase(c)) {
                    int idx = (LOWERCASE.length + UPPERCASE_MAP.get(c) - secretKey) % LOWERCASE.length;
                    sb.append(UPPERCASE[idx]);
                }
                else if (Character.isLowerCase(c)) {
                    sb.append(LOWERCASE[(LOWERCASE.length + LOWERCASE_MAP.get(c) - secretKey) % LOWERCASE.length]);
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

    /**
     * Decrypts Blowfish encoded byte array
     * @param key The encryption key
     * @param toDecrypt The byte array to be decrypted
     * @return A decrypted byte array
     */
    private static byte[] decryptBlowfish(String key, byte[] toDecrypt) {
        return toDecrypt;
    }

    /**
     * Decrypts AES encoded byte array
     * @param key The encryption key
     * @param toDecrypt The byte array to be decrypted
     * @return A decrypted byte array
     */
    private static byte[] decryptAES(String key, byte[] toDecrypt) throws Exception {
        try {
            /* First decode the byte array from Base&¤ */
            byte[] decodedBytes = Base64.getDecoder().decode(toDecrypt);

            /* Decode key from Base64 */
            byte[] decodedKey = Base64.getDecoder().decode(key);
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 

            Cipher cipher = Cipher.getInstance("AES");

            /* Actually decrypt */
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(decodedBytes);
        } catch (Exception e) {
            return toDecrypt;
        }
    }

    /**
     * Decrypts RSA encoded byte array
     * @param key The encryption key
     * @param toDecrypt The byte array to be decrypted
     * @return A decrypted byte array
     */
    private static byte[] decryptRSA(String key, byte[] toDecrypt) {
        return toDecrypt;
    }

    /**
     * Encrypts byte array with Caesar
     * @param key The encryption key
     * @param toDecrypt The byte array to be encrypted
     * @return An encrypted byte array
     */
    private static byte[] encryptCaesar(String key, byte[] toEncrypt) {
        try {
            int secretKey = Integer.parseInt(key);
            String string = Transcriber.byteToString(toEncrypt);

            StringBuilder sb = new StringBuilder();

            /* Actually decrypt */
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if (Character.isUpperCase(c)) { 
                    int idx = (LOWERCASE.length + UPPERCASE_MAP.get(c) + secretKey) % LOWERCASE.length;
                    sb.append(UPPERCASE[idx]);
                }
                else if (Character.isLowerCase(c)) {
                    sb.append(LOWERCASE[(LOWERCASE.length + LOWERCASE_MAP.get(c) + secretKey) % LOWERCASE.length]);
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

    /**
     * Encrypts byte array with Blowfish
     * @param key The encryption key
     * @param toDecrypt The byte array to be encrypted
     * @return An encrypted byte array
     */
    private static byte[] encryptBlowfish(String key, byte[] toEncrypt) {
        return toEncrypt;
    }

    /**
     * Encrypts byte array with AES
     * @param key The encryption key
     * @param toDecrypt The byte array to be encrypted
     * @return An encrypted byte array
     */
    private static byte[] encryptAES(String key, byte[] toEncrypt) throws Exception {
        try{
            /* First decode the key from Base64 */
            byte[] decodedKey = Base64.getDecoder().decode(key);

            /* Retrieve the AES key from byte form */
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 

            Cipher cipher = Cipher.getInstance("AES");

            /* Return Base64 encoded string */
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encode(cipher.doFinal(toEncrypt));
        } catch (Exception e) {
            return toEncrypt;
        }
    }

    /**
     * Encrypts byte array with RSA
     * @param key The encryption key
     * @param toDecrypt The byte array to be encrypted
     * @return An encrypted byte array
     */
    private static byte[] encryptRSA(String key, byte[] toEncrypt) {
        return toEncrypt;
    }
}