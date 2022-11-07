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
        byte[] initVctor = new byte[8];

        random.nextBytes(bytes);
        random.nextBytes(initVctor);
        BigInteger bigInteger = new BigInteger(bytes);
        long key = bigInteger.longValue();
        bigInteger = new BigInteger(initVctor);
        long initV = bigInteger.longValue();
        int N = 16;
        System.out.println("ROUNDS: " + N);
        FileInputStream fileInputStream = new FileInputStream("/home/phoekoby/Documents/education/cryptomethods/Task_1/src/main/resources/message.txt");
        byte[] message2 = fileInputStream.readAllBytes();
        fileInputStream.close();
        System.out.println("==============================NOT ENCRYPTED MESSAGE=====================================");
        System.out.println(new String(message2));
        System.out.println("========================================================================================");
        byte[] encryptedMessage1 = FeistelUtils.encryptWithCBC(message2, N, key, initV);
        System.out.println("================================ENCRYPTED MESSAGE=======================================");
        System.out.println(new String(encryptedMessage1));
        System.out.println("========================================================================================");
        byte[] decryptedMessage1 = FeistelUtils.decryptWithCBC(encryptedMessage1, N, key, initV);
        System.out.println("================================DECRYPTED MESSAGE=======================================");
        System.out.println(new String(decryptedMessage1));
        System.out.println("========================================================================================");
        System.out.println("Is equals: " + Arrays.equals(message2, decryptedMessage1));
        System.out.println("========================================================================================");

        System.out.println("========================================================================================");
        FileInputStream fileInputStreamRus = new FileInputStream("/home/phoekoby/Documents/education/cryptomethods/Task_1/src/main/resources/message-rus.txt");
        byte[] messageRus = fileInputStreamRus.readAllBytes();
        fileInputStreamRus.close();
        System.out.println("==============================NOT ENCRYPTED MESSAGE=====================================");
        System.out.println(new String(messageRus));
        System.out.println("========================================================================================");
        byte[] encryptedMessageRus = FeistelUtils.encryptWithCBC(messageRus, N, key, initV);
        System.out.println("================================ENCRYPTED MESSAGE=======================================");
        System.out.println(new String(encryptedMessageRus));
        System.out.println("========================================================================================");
        byte[] decryptedMessageRus = FeistelUtils.decryptWithCBC(encryptedMessageRus, N, key, initV);
        System.out.println("================================DECRYPTED MESSAGE=======================================");
        System.out.println(new String(decryptedMessageRus));
        System.out.println("========================================================================================");
        System.out.println("Is equals: " + Arrays.equals(messageRus, decryptedMessageRus));

        /* Картинка */
        FileInputStream fileInputStreamImage = new FileInputStream("/home/phoekoby/Documents/education/cryptomethods/Task_1/src/main/resources/cat.jpg");
        byte[] imageMessage = fileInputStreamImage.readAllBytes();
        fileInputStreamImage.close();
        byte[] encryptedMessage = FeistelUtils.encryptWithCBC(imageMessage, N, key, initV);
        byte[] decryptedImage = FeistelUtils.decryptWithCBC(encryptedMessage, N, key, initV);
        FileOutputStream fileOutputStream = new FileOutputStream("/home/phoekoby/Documents/education/cryptomethods/Task_1/src/main/resources/decrypted-cat.jpg");
        fileOutputStream.write(decryptedImage);
        fileOutputStream.close();

    }
}



/*

10) n = 471145798109971     e = 12971
ШТ = 334795697842808423513191687578172510862135388341954737406265
p = 2432251 q = 193707721

ф(n) = (p-1) * (q-1) = 2432250 * 193707720 = 471145601970000

 */