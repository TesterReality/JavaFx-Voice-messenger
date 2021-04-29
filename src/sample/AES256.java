package sample;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AES256 {
    private SecretKey secretKey;
    public AES256(byte[] key) {
        // decode the base64 encoded string
        byte[] decodedKey = key;
// rebuild key using SecretKeySpec
        this.secretKey= new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public byte[] makeAes(byte[] rawMessage, int cipherMode){
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, this.secretKey);
            byte [] output = cipher.doFinal(rawMessage);
            return output;
        } catch (BadPaddingException e){
            System.out.println("[КЛИЕНТ] Не удалось расшифровать (вероятнее всего QR)");
            return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        System.out.println("[КЛИЕНТ] Возникла ошибка при расшифровке");
        return null;

    }
}
