package ru.vsu.crypto;

import org.apache.commons.lang3.ArrayUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        testFeistel();
    }

    public static void testFeistel() throws IOException {
        System.out.println("-------------------------FEISTEL TEST----------------------------");
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[8];

        random.nextBytes(bytes);
        BigInteger bigInteger = new BigInteger(bytes);
        long key = bigInteger.longValue();
        int N = 16;
        System.out.println("ROUNDS: " + N);
        FileInputStream fileInputStream = new FileInputStream("/home/phoekoby/Documents/education/cryptomethods/Task_1/src/main/resources/message.txt");
        byte[] message2 = fileInputStream.readAllBytes();
        fileInputStream.close();
        System.out.println("==============================NOT ENCRYPTED MESSAGE=====================================");
        System.out.println(new String(message2));
        System.out.println("========================================================================================");
        byte[] encryptedMessage1 = FeistelUtils.encrypt(message2, N, key);
        System.out.println("================================ENCRYPTED MESSAGE=======================================");
        System.out.println(new String(encryptedMessage1));
        System.out.println("========================================================================================");
        byte[] decryptedMessage1 = FeistelUtils.decrypt(encryptedMessage1, N, key);
        System.out.println("================================DECRYPTED MESSAGE=======================================");
        System.out.println(new String(decryptedMessage1));
        System.out.println("========================================================================================");
        System.out.println("Is equals: " + Arrays.equals(message2, decryptedMessage1));


        FileInputStream fileInputStreamImage = new FileInputStream("/home/phoekoby/Documents/education/cryptomethods/Task_1/src/main/resources/cat.jpg");
        byte[] imageMessage = fileInputStreamImage.readAllBytes();
        fileInputStreamImage.close();
        byte[] encryptedMessage = FeistelUtils.encrypt(imageMessage,N,key);
        byte[] decryptedImage = FeistelUtils.decrypt(encryptedMessage,N,key);
        FileOutputStream fileOutputStream = new FileOutputStream("/home/phoekoby/Documents/education/cryptomethods/Task_1/src/main/resources/decrypted-cat.jpg");
        fileOutputStream.write(decryptedImage);
        fileOutputStream.close();

    }
}
