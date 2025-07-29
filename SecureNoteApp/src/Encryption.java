import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * 
 * This class handles AES encryption and decryption.
 * @author Sanchith Velmurugan
 * @version 1.1
 * 
 */
public class Encryption {
    private static final String ALGO = "AES";
    private static final int KEY_SIZE = 16;

    /**
     * This method encrypts text using AES with a key derived from the password.
     * @param String data
     * @param String password
     * @return the encrypted byte array
     * @throws Exception if encryption fails
     * 
     */
    public static byte[] encrypt(String data, String password) throws Exception {
        SecretKeySpec key = getKey(password);
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data.getBytes("UTF-8"));
    }

    /**
     * This method decrypts encrypted data using the original password.
     * @param byte[] encryptedData
     * @param String password
     * @return String original decrypted text
	 * @throws Exception if encryption fail
     */
    public static String decrypt(byte[] encryptedData, String password) throws Exception {
        SecretKeySpec key = getKey(password);
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(encryptedData);
        return new String(decrypted, "UTF-8");
    }

    /**
     * This method converts a password into a AES key.
     * @param String password
     * @return SecretKeySpec key based on the given password
     * @throws Exception if key generation fail
     */
    private static SecretKeySpec getKey(String password) throws Exception {
        byte[] keyBytes = Arrays.copyOf(password.getBytes("UTF-8"), KEY_SIZE);
        return new SecretKeySpec(keyBytes, ALGO);
    }
}
