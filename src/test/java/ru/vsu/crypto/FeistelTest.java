package ru.vsu.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Objects;

public class FeistelTest {
    private final int N = 16;
    private long key;

    @BeforeEach
    void setup() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[8];

        random.nextBytes(bytes);
        BigInteger bigInteger = new BigInteger(bytes);
        key = bigInteger.longValue();
    }

    @Test
    public void test1() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(Objects.requireNonNull(this.getClass().getClassLoader().getResource("message-test-1.txt")).getFile());
        byte[] messageByte = fileInputStream.readAllBytes();
        fileInputStream.close();
        byte[] encryptedMessage = FeistelUtils.encrypt(messageByte, N, key);
        byte[] decryptedMessage = FeistelUtils.decrypt(encryptedMessage,N,key);
        Assertions.assertArrayEquals(messageByte,decryptedMessage);
    }


    @Test
    public void test2() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(Objects.requireNonNull(this.getClass().getClassLoader().getResource("message-test-2.txt")).getFile());
        byte[] messageByte = fileInputStream.readAllBytes();
        fileInputStream.close();
        byte[] encryptedMessage = FeistelUtils.encrypt(messageByte, N, key);
        byte[] decryptedMessage = FeistelUtils.decrypt(encryptedMessage,N,key);
        Assertions.assertArrayEquals(messageByte,decryptedMessage);
    }



//    @Test
//    public void test3() throws IOException {
//        FileInputStream fileInputStream = new FileInputStream(Objects.requireNonNull(this.getClass().getClassLoader().getResource("cat.jpg")).getFile());
//        byte[] messageByte = fileInputStream.readAllBytes();
//        fileInputStream.close();
//        byte[] encryptedMessage = FeistelUtils.encrypt(messageByte, N, key);
//        byte[] decryptedMessage = FeistelUtils.decrypt(encryptedMessage,N,key);
//        FileOutputStream fileOutputStream = new FileOutputStream(Objects.requireNonNull(this.getClass().getClassLoader().getResource("results/decrypted-test-cat.jpg")).getFile());
//        fileOutputStream.write(decryptedMessage);
//        fileOutputStream.close();
//        Assertions.assertArrayEquals(messageByte,decryptedMessage);
//    }
}
