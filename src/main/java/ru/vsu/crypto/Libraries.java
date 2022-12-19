package ru.vsu.crypto;

import lombok.SneakyThrows;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.jcajce.provider.digest.SHA1;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

public class Libraries {

    /*
Вариант 19
m = disadvantage
h = d204231bab17d8207330498c1c3d66ac
*/

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        String m = "disadvantage";
        String h = "d204231bab17d8207330498c1c3d66ac";
        System.out.println("-------------------------TASK 1-------------------------------");
        System.out.println(findAlgorithm(m.getBytes(StandardCharsets.UTF_8), h.getBytes(StandardCharsets.UTF_8)));
        System.out.println("--------------------------------------------------------------");
        System.out.println("-------------------------TASK 2-------------------------------");
        System.out.println(findKey(m));
        System.out.println("--------------------------------------------------------------");

        System.out.println("-------------------------TASK 3-------------------------------");
        System.out.println("Encrypting message: " + m + " with Key: " + h);
        byte[] encrypted = encryptMessage(m.getBytes(StandardCharsets.UTF_8), h.getBytes(StandardCharsets.UTF_8));
        System.out.println("Encrypted message: " + new String(encrypted, StandardCharsets.UTF_8));
        System.out.println("Decrypted message: " + new String(decryptMessage(encrypted, h.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        System.out.println("--------------------------------------------------------------");

        task4();

    }

    @SneakyThrows
    private static String findAlgorithm(byte[] m, byte[] h) {
        Set<String> algorithms = Security.getAlgorithms("MessageDigest");
        for (String algorithm : algorithms) {
            try {
                MessageDigest md = MessageDigest.getInstance(algorithm);
                byte[] hash = md.digest(m);
                if (Arrays.equals(Hex.toHexString(hash).getBytes(), h)) {

                    return algorithm;
                }
            } catch (IllegalStateException ignored) {
            }

        }
        return null;
    }

    @SneakyThrows
    private static String findKey(String password) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keyspec = new PBEKeySpec(password.toCharArray(),
                formater.format(Date.from(Instant.now())).getBytes(),
                4096, 256);
        Key key = factory.generateSecret(keyspec);
        return new String(key.getEncoded(), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    private static byte[] encryptMessage(byte[] message, byte[] keyBytes) {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Key key = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
        return cipher.doFinal(message);
    }

    @SneakyThrows
    private static byte[] decryptMessage(byte[] encryptedMessage, byte[] keyBytes) {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Key key = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
        return cipher.doFinal(encryptedMessage);
    }

    @SneakyThrows
    public static void task4(){
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(8192);
        KeyPair pair = generator.generateKeyPair();

        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        try(InputStream startMessage = Libraries.class.getClassLoader().getResourceAsStream("message.txt")) {
            byte[] fileBytes = startMessage != null ? startMessage.readAllBytes() : new byte[0];

            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedFileBytes = encryptCipher.doFinal(fileBytes);
            FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/encrypted-message.txt");
            fileOutputStream.write(encryptedFileBytes);
            fileOutputStream.close();


            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedFileBytes = decryptCipher.doFinal(encryptedFileBytes);
            FileOutputStream fileOutputStreamDecrypted = new FileOutputStream("src/main/resources/decrypted-message.txt");
            fileOutputStreamDecrypted.write(decryptedFileBytes);
            fileOutputStreamDecrypted.close();
            System.out.println(new String(decryptedFileBytes));
        }



    }

}
