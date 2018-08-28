package net.lightapi.config.server.common.crypto;

import com.networknt.utility.Constants;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import static com.networknt.utility.Decryptor.CRYPT_PREFIX;

public class AESConfigSecurity implements ConfigSecurity {

    private static final int ITERATIONS = 65536;
    private static final int KEY_SIZE = 128;
    private static final byte[] SALT = { (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0 };
    private static final String STRING_ENCODING = "UTF-8";
    private SecretKeySpec secret;
    private Cipher cipher;
    private BASE64Encoder base64Encoder;
    private BASE64Decoder base64Decoder;

    public AESConfigSecurity(String key) {
        try {
            /* Derive the key, given password and salt. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec;

            spec = new PBEKeySpec(key.toCharArray(), SALT, ITERATIONS, KEY_SIZE);
            SecretKey tmp = factory.generateSecret(spec);
            secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            // CBC = Cipher Block chaining
            // PKCS5Padding Indicates that the keys are padded
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // For production use commons base64 encoder
            base64Encoder = new BASE64Encoder();

            base64Decoder = new BASE64Decoder();
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize", e);
        }
    }



    public String decrypt(String input) {
        if (!input.startsWith(CRYPT_PREFIX))
        {
            throw new RuntimeException("Unable to decrypt, input string does not start with 'CRYPT'");
        }

        try
        {
            byte[] data = base64Decoder.decodeBuffer(input.substring(6, input.length()));
            int keylen = KEY_SIZE / 8;
            byte[] iv = new byte[keylen];
            System.arraycopy(data, 0, iv, 0, keylen);
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            return new String(cipher.doFinal(data, keylen, data.length - keylen), STRING_ENCODING);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to decrypt.", e);
        }
    }


    public String ecrypt(String input) {
        try
        {
            byte[] inputBytes = input.getBytes(STRING_ENCODING);
            // CBC = Cipher Block chaining
            // PKCS5Padding Indicates that the keys are padded
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] ciphertext = cipher.doFinal(inputBytes);
            byte[] out = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, out, 0, iv.length);
            System.arraycopy(ciphertext, 0, out, iv.length, ciphertext.length);
            return CRYPT_PREFIX + ":" + base64Encoder.encode(out);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("Unable to encrypt", e);
        } catch (BadPaddingException e) {
            throw new RuntimeException("Unable to encrypt", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Unable to encrypt", e);
        } catch (InvalidParameterSpecException e) {
            throw new RuntimeException("Unable to encrypt", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to encrypt", e);
        }
    }
}
