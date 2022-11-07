package ru.vsu.crypto;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MyHash {


    private static final long KEY = 472347326572784323L;
    private static final byte[] INIT_VECTOR_HASH = new byte[]{(byte) 0xFF, (byte) 0xA2, (byte) 0x92, (byte) 0xDA, (byte) 0xFF, (byte) 0xA2, (byte) 0x92, (byte) 0xDA};

    private static final long INIT_VECTOR = 875827358729837L;


//    private static final char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
//    'm', 'n', 'o', 'p', 'q', }

    public MyHash() {
    }

    public static byte[] hash(byte[] bytes, byte[] initVector) {
        List<byte[]> blocks = new ArrayList<>();
        int i = 0;
        for (i = 0; i < bytes.length - 4; i += 4) {
            blocks.add(new byte[]{bytes[i], bytes[i + 1], bytes[i + 2], bytes[i + 3]});
        }
        if (i < bytes.length) {
            blocks.add(new byte[bytes.length - i]);
            for (int j = 0; i < bytes.length; i++, j++) {
                blocks.get(blocks.size() - 1)[j] = bytes[i];
            }
        }
        byte[] h = FeistelUtils.encryptWithCBC(blocks.get(0), 1, bytesToLong(initVector), INIT_VECTOR);
        for (int c = 1; c < blocks.size(); c++) {
            h = FeistelUtils.encryptWithCBC(blocks.get(c), 1, bytesToLong(h), INIT_VECTOR);
            long longBlock = bytesToLong(blocks.get(c));
            long longEncryptedBlock = bytesToLong(h);
            h = longsToBytes(new long[]{longBlock ^ longEncryptedBlock});
        }
        return h;
    }


    private static byte[] longsToBytes(long[] longNumbers) {
        List<Byte> resultArray = new ArrayList<>();
        for (long longNumber : longNumbers) {
            for (int j = 56; j >= 0; j -= 8) {
                resultArray.add(((byte) (longNumber >>> j)));
            }
        }
        return ArrayUtils.toPrimitive(resultArray.toArray(new Byte[0]));
    }

    private static long bytesToLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            result <<= Byte.SIZE;
            if (i < bytes.length) {
                result |= (bytes[i] & 0xFF);
            }
        }
        return result;
    }

    public static Pair<byte[], byte[]> findCollisionBirthdayAttack() {
        long counter = 0L;
        int messageSize = 8;

        Map<Long, byte[]> pairs = new HashMap<>();

        while (true) {
            String random = RandomStringUtils.randomAlphabetic(messageSize);

            byte[] randomBytes = random.getBytes();

            byte[] hashed = hash(randomBytes, INIT_VECTOR_HASH);
            long hashOfHashedBytes = bytesToLong(hashed);
            if (pairs.containsKey(hashOfHashedBytes) && !Arrays.equals(pairs.get(hashOfHashedBytes), randomBytes)) {
                System.out.println("Count of iterations: " + counter);
                return Pair.of(randomBytes, pairs.get(hashOfHashedBytes));
            } else {
                pairs.put(hashOfHashedBytes, randomBytes);
            }
            counter++;
        }
    }


    public static byte[] findCollision(byte[] message, byte[] initVector) {
        SecureRandom random = new SecureRandom();
        long count = 0L;
//        byte[] hashMessage = hash(message, initVector);

        byte[] lastBlock = getLastBlock(message);
        byte[] lastKey = hash(findLastKey(message), initVector);
        byte[] hashedLastBlock = hash(lastBlock, lastKey);
        while (true) {
            byte[] randomBytes = new byte[8];
            random.nextBytes(randomBytes);
            byte[] messageRandom = Arrays.copyOf(randomBytes, randomBytes.length);

            ExecutorService executorService = Executors.newFixedThreadPool(8);
            executorService.submit(()-> {

            });
            executorService.submit(()->
            {
                
            });
            byte[] hashedRandomBytes = hash(randomBytes, lastKey);
            if (Arrays.equals(hashedLastBlock, hashedRandomBytes) && !Arrays.equals(lastBlock, messageRandom)) {
                System.out.println("Iteration amount: " + count);
                return ArrayUtils.addAll(findLastKey(message), messageRandom);
            }
            count++;
        }
    }

    private static byte[] findLastKey(byte[] message) {
        int lastBlockBegin = message.length % 8 == 0 ? message.length - 8 : message.length - message.length % 8;
        return Arrays.copyOfRange(message, 0, lastBlockBegin);
    }

    private static byte[] getLastBlock(byte[] message) {
        int lastBlockBegin = message.length % 8 == 0 ? message.length - 8 : message.length - message.length % 8;
        return Arrays.copyOfRange(message, lastBlockBegin, message.length);
    }


    public static void main(String[] args) throws IOException {


        System.out.println("-------------------------HASH FUNCTION----------------------------");
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[8];
        byte[] initVctor = new byte[8];

        random.nextBytes(bytes);
        random.nextBytes(initVctor);

        Pair<byte[], byte[]> result = findCollisionBirthdayAttack();
        System.out.println("Collision: '" + new String(result.getLeft()) + "' and '" + new String(result.getRight()) + "'");
        System.out.println("Hash of first message: " + new String(hash(result.getLeft(), INIT_VECTOR_HASH)));
        System.out.println("Hash of second message: " + new String(hash(result.getRight(), INIT_VECTOR_HASH)));

        BigInteger bigInteger = new BigInteger(bytes);
        long key = bigInteger.longValue();
        bigInteger = new BigInteger(initVctor);
        long initV = bigInteger.longValue();
        FileInputStream fileInputStream = new FileInputStream("/home/phoekoby/Documents/education/cryptomethods/Task_1/src/main/resources/for_collision.txt");
        byte[] message2 = fileInputStream.readAllBytes();
        fileInputStream.close();
        System.out.println("==============================MESSAGE=====================================");
        System.out.println(new String(message2));
        System.out.println("========================================================================================");
        byte[] hash = MyHash.hash(message2, initVctor);
        System.out.println("================================HASHED MESSAGE=======================================");
        System.out.println(new String(hash));
        System.out.println("============================COLISION=========================================");
        byte[] collisionMessage = findCollision(message2, initVctor);
        System.out.println(new String(collisionMessage));
        FileOutputStream fileOutputStream = new FileOutputStream("/home/phoekoby/Documents/education/cryptomethods/Task_1/src/main/resources/collised.txt");
        fileOutputStream.write(collisionMessage);
        fileOutputStream.close();

        System.out.println("========================================================================================");
        FileInputStream fileInputStreamRus = new FileInputStream("/home/phoekoby/Documents/education/cryptomethods/Task_1/src/main/resources/message-rus.txt");
        byte[] messageRus = fileInputStreamRus.readAllBytes();
        fileInputStreamRus.close();
        System.out.println("==============================MESSAGE=====================================");
        System.out.println(new String(messageRus));
        System.out.println("========================================================================================");
        byte[] hashed = MyHash.hash(messageRus, INIT_VECTOR_HASH);
        System.out.println("================================HASHED MESSAGE=======================================");
        System.out.println(new String(hashed));
        System.out.println("============================COLISION=========================================");
        collisionMessage = findCollision(messageRus, INIT_VECTOR_HASH);
        System.out.println(new String(collisionMessage));
    }
}
