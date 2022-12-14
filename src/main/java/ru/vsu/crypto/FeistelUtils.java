package ru.vsu.crypto;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeistelUtils {
    private FeistelUtils() {
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

    private static long[] bytesToLongs(byte[] bytes) {
        int length = bytes.length % 8 != 0 ? bytes.length / 8 + 1 : bytes.length / 8;
        long[] longs = new long[length];
        byte[] number = new byte[8];
        for (int i = 0, j = 0; i < bytes.length; i++) {
            if ((i % 8) == 0) {
                if (i >= 8) {
                    longs[j] = bytesToLong(number);
                    number = new byte[(bytes.length - i) / 8 > 0 ? 8 : (bytes.length - i) % 8];
                    j++;
                }
            }
            number[i % 8] = bytes[i];
        }
        longs[longs.length - 1] = bytesToLong(number);
        return longs;
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

    private static byte[] longsToBytesWithoutZeros(long[] longNumbers) {
        List<Byte> resultArray = new ArrayList<>();
        for (int i = 0; i < longNumbers.length - 1; i++) {
            for (int j = 56; j >= 0; j -= 8) {
                resultArray.add(((byte) (longNumbers[i] >>> j)));
            }
        }
        for (int j = 56; j >= 0; j -= 8) {
            if (((byte) (longNumbers[longNumbers.length - 1] >>> j)) != (byte) 0)
                resultArray.add(((byte) (longNumbers[longNumbers.length - 1] >>> j)));
        }
        return ArrayUtils.toPrimitive(resultArray.toArray(new Byte[0]));
    }


    public static byte[] encryptWithCBC(byte[] message, int n, long key, long initVector) {
        validateByteArray(message);
        long[] longMessage = bytesToLongs(message);
        long[] encryptedBlock = new long[longMessage.length];
        encryptedBlock[0] = encrypt(longMessage[0] ^ initVector, n, key);
        for (int i = 1; i < encryptedBlock.length; i++) {
            encryptedBlock[i] = encrypt(longMessage[i] ^ encryptedBlock[i - 1], n, key);
        }
        return longsToBytes(encryptedBlock);
    }

    public static byte[] encrypt(byte[] message, int n, long key) {
        validateByteArray(message);
        long[] longMessage = bytesToLongs(message);
        long[] encryptedBlock = new long[longMessage.length];
        for (int j = 0; j < longMessage.length; j++) {
            encryptedBlock[j] = encrypt(longMessage[j], n, key);
        }
        return longsToBytes(encryptedBlock);
    }

    private static long encrypt(long message, int n, long key) {
        short[] blocks = breakToBlocks(message);
        short[] resultBlocks = Arrays.copyOf(blocks, blocks.length);
        for (int i = 0; i < n; i++) {
            short keyI = keysGenerator(key, i);
            resultBlocks[0] = (short) (function(blocks[0], blocks[1], keyI) ^ blocks[3]);
            resultBlocks[1] = (short) (blocks[2] ^ resultBlocks[0]);
            resultBlocks[2] = blocks[0];
            resultBlocks[3] = (short) (blocks[1] ^ blocks[0]);
            blocks = Arrays.copyOf(resultBlocks, resultBlocks.length);
            keyI = 0;
        }
        return convertShortArrayToLong(blocks);
    }

    public static byte[] decrypt(byte[] encryptedMessage, int n, long key) {
        validateByteArray(encryptedMessage);
        long[] longMessage = bytesToLongs(encryptedMessage);
        long[] decryptedMessage = new long[longMessage.length];
        for (int j = 0; j < longMessage.length; j++) {
            decryptedMessage[j] = decrypt(longMessage[j], n, key);
        }
        return longsToBytesWithoutZeros(decryptedMessage);
    }

    public static byte[] decryptWithCBC(byte[] encryptedMessage, int n, long key, long initVector) {
        validateByteArray(encryptedMessage);
        long[] longMessage = bytesToLongs(encryptedMessage);
        long[] decryptedMessage = new long[longMessage.length];
        decryptedMessage[0] = decrypt(longMessage[0], n, key) ^ initVector;
        for (int i = 1; i < decryptedMessage.length; i++) {
            decryptedMessage[i] = decrypt(longMessage[i], n, key) ^ longMessage[i - 1];
        }
        return longsToBytesWithoutZeros(decryptedMessage);
    }

    private static long decrypt(long encryptedMessage, int n, long key) {
        short[] blocks = breakToBlocks(encryptedMessage);
        short[] resultBlocks = Arrays.copyOf(blocks, blocks.length);
        for (int i = 0; i < n; i++) {
            short keyI = keysGenerator(key, n - 1 - i);
            resultBlocks[0] = blocks[2];
            resultBlocks[1] = (short) (blocks[3] ^ blocks[2]);
            resultBlocks[2] = (short) (blocks[0] ^ blocks[1]);
            resultBlocks[3] = (short) (function(resultBlocks[0], resultBlocks[1], keyI) ^ blocks[0]);
            blocks = Arrays.copyOf(resultBlocks, resultBlocks.length);
            keyI = 0;
        }
        return convertShortArrayToLong(blocks);
    }

    private static long convertShortArrayToLong(short[] array) {
        long value = 0L;
        for (short a : array) {
            value = (value << 16) + (a & 0xffff);
        }
        return value;
    }

    private static short function(short m0, short m2, short key) {
        return (short) (cyclingShitLeft(m0, 2) ^ (~(cyclingShitRight(m2, 9))) ^ key);
    }

    private static short keysGenerator(long key, int i) {
        return (short) ((short) ((cyclingShitLeft(key, i * 3))/* >>> 48*/) ^ (short) ((~key)/* >>> 48*/));
    }

    private static short[] breakToBlocks(long message) {
        short[] blocks = new short[4];
        for (int i = 0; i < 4; i++) {
            blocks[i] = (short) (message >>> (3 - i) * 16);
        }
        return blocks;
    }

    private static long cyclingShitLeft(long value, int n) {
        long left = value << n;
        long right = value >>> (Long.toBinaryString(value).length() - n);
        return left | right;
    }

    private static long cyclingShitRight(long value, int n) {
        long left = value >>> n;
        long right = value << Long.toBinaryString(value).length() - n;
        return left | right;
    }

    private static void validateByteArray(byte[] array) {
        if (array == null || array.length == 0)
            throw new IllegalArgumentException("Array should not be empty");
    }
}
