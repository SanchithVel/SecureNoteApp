import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class Encryption {
    private static final String ALGO = "AES";
    private static final int KEY_SIZE = 16;
	
    public static byte[] encrypt(String data, String password) throws Exception {
        SecretKeySpec key = getKey(password);
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data.getBytes("UTF-8"));
    }
	
    public static String decrypt(byte[] encryptedData, String password) throws Exception {
        SecretKeySpec key = getKey(password);
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(encryptedData);
        return new String(decrypted, "UTF-8");
    }

    private static SecretKeySpec getKey(String password) throws Exception {
        byte[] keyBytes = Arrays.copyOf(password.getBytes("UTF-8"), KEY_SIZE);
        return new SecretKeySpec(keyBytes, ALGO);
    }
}
