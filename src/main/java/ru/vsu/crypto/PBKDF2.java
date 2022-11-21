package ru.vsu.crypto;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class PBKDF2 {
    public static void main(String[] args) {
        System.out.println(PBKDF2(8, "password", "asdnjn", 10, 32).length());
    }
    public static String PBKDF2(int hLen, String pass, String salt, int c, int dkLen) {
        StringBuilder DK = new StringBuilder();
        int l = (int) Math.ceil(dkLen / hLen);
        int r = dkLen - (l - 1) * hLen;

        for (int i = 0; i < l - 1; i++) {
            DK.append(func(pass, salt, c, i));
        }
        byte[] last = func(pass, salt, c, l).getBytes();

        for (int i = 0; i < r; i += 2) {
            byte[] t = new byte[]{last[i], last[i + 1]};
            DK.append(new String(t));
        }

        return DK.toString();
    }

    public static String func(String pass, String salt, int c, int l) {
        String[] U = new String[c];

        U[0] = new String(MyHash.hash(pass.getBytes(), longToBytes(bytesToLong(salt.getBytes()) + (long) l)));
        for (int i = 1; i < c; i++) {
            U[i] = new String(MyHash.hash(pass.getBytes(), U[i - 1].getBytes()));
        }

        StringBuilder out = new StringBuilder();
        for (String s : U) {
            out.append(s);
        }
        return out.toString();
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

    private static byte[] longToBytes(long longNumber) {
        List<Byte> resultArray = new ArrayList<>();
        for (int j = 56; j >= 0; j -= 8) {
            resultArray.add(((byte) (longNumber >>> j)));
        }
        return ArrayUtils.toPrimitive(resultArray.toArray(new Byte[0]));
    }
}
