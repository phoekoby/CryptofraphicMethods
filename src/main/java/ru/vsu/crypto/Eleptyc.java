package ru.vsu.crypto;

import org.apache.commons.lang3.tuple.Pair;
import java.math.BigInteger;
import java.util.Random;

public class Eleptyc {
//    private final static String hexP = "fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f";
    private final static String hexP = "6277101735386680763835789423207666416083908700390324961279";
//    private final static BigInteger p = new BigInteger(hexP, 16);
    private final static BigInteger p = new BigInteger(hexP, 10);
//    private final static BigInteger a = BigInteger.ZERO;
    private final static BigInteger a = BigInteger.valueOf(-3);
//    private final static BigInteger b = BigInteger.valueOf(7);

    private final static String hexB = "5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b";
    private final static BigInteger b = new BigInteger(hexB, 16);
//    private final static String hexXg = "79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798";
    private final static String hexXg = "6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296";
    private final static BigInteger Xg = new BigInteger(hexXg, 16);
//    private final static String hexYg = "483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8";
    private final static String hexYg = "4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5";
    private final static BigInteger Yg = new BigInteger(hexYg, 16);
//    private final static String hexN = "fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141";
    private final static String hexN = "ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551";
    private final static BigInteger n = new BigInteger(hexN, 16);
    private final static BigInteger h = BigInteger.ONE;

    private final static Random random = new Random();

    public static void main(String[] args) {
        PairOfKeys alisaKeys = makeKeyPair();
        PairOfKeys bobKeys = makeKeyPair();

        Pair<BigInteger, BigInteger> aliceSharedKey = scalarMult(alisaKeys.privateKey, bobKeys.publicKey);
        Pair<BigInteger, BigInteger> bobSharedKey = scalarMult(bobKeys.privateKey, alisaKeys.publicKey);
        System.out.println("Is keys Equals: " + aliceSharedKey.equals(bobSharedKey));

        System.out.printf("Common Secret: (0x%s, 0x%s)", aliceSharedKey.getLeft().toString(16), aliceSharedKey.getRight().toString(16));
    }


    public static PairOfKeys makeKeyPair() {
        BigInteger privateKey = new BigInteger(1024, random);
        Pair<BigInteger, BigInteger> publicKey = scalarMult(privateKey, Pair.of(Xg, Yg));
        return new PairOfKeys(privateKey, publicKey);
    }

    private static boolean isOnCurve(BigInteger x, BigInteger y) {
        return (y.multiply(y)
                .subtract(x.multiply(x).multiply(x))
                .subtract(a.multiply(x))
                .subtract(b))
                .mod(p)
                .equals(BigInteger.ZERO);
    }

    private static Pair<BigInteger, BigInteger> pointAdd(Pair<BigInteger, BigInteger> point1, Pair<BigInteger, BigInteger> point2) {
        if (point1 == null) {
            return point2;
        }
        if (point2 == null) {
            return point1;
        }
        BigInteger x1 = point1.getLeft();
        BigInteger y1 = point1.getRight();

        BigInteger x2 = point2.getLeft();
        BigInteger y2 = point2.getRight();

        if (x1.equals(x2) && !y1.equals(y2)) {
            return null;
        }

        BigInteger m;
        if (x1.equals(x2)) {
            m = (BigInteger.valueOf(3).multiply(x1).multiply(x1).add(a)).multiply(inverseMod(BigInteger.TWO.multiply(y1), p));
        } else {
            m = (y1.subtract(y2)).multiply(inverseMod(x1.subtract(x2), p));
        }

        BigInteger x3 = m.multiply(m).subtract(x1).subtract(x2);
        BigInteger y3 = y1.add(m.multiply(x3.subtract(x1)));

        return Pair.of(x3.mod(p), y3.negate().mod(p));
    }

    private static BigInteger inverseMod(BigInteger k, BigInteger p) {
        if (k.equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("Zre division error");
        }
        if (k.compareTo(BigInteger.ZERO) < 0) {
            return p.subtract(inverseMod(k.negate(), p));
        }

        BigInteger s = BigInteger.ZERO;
        BigInteger oldS = BigInteger.ONE;

        BigInteger t = BigInteger.ONE;
        BigInteger oldT = BigInteger.ZERO;

        BigInteger r = p;
        BigInteger oldR = k;

        while (!r.equals(BigInteger.ZERO)) {
            BigInteger quontient = oldR.divide(r);
            BigInteger tmp = r;
            r = oldR.subtract(quontient.multiply(r));
            oldR = tmp;

            tmp = s;

            s = oldS.subtract(quontient.multiply(s));
            oldS = tmp;

            tmp = t;
            t = oldT.subtract(quontient.multiply(t));
            oldT = tmp;
        }

        BigInteger gcd = oldR;
        BigInteger x = oldS;
        BigInteger y = oldT;
        return x.mod(p);
    }

    private static Pair<BigInteger, BigInteger> scalarMult(BigInteger k, Pair<BigInteger, BigInteger> point) {
        BigInteger x = point.getLeft();
        BigInteger y = point.getRight();

        if (k.mod(n).equals(BigInteger.ZERO) || point == null) {
            return null;
        }

        if (k.compareTo(BigInteger.ZERO) < 0) {
            return scalarMult(k.negate(), pointNeg(point));
        }

        Pair<BigInteger, BigInteger> result = null;
        Pair<BigInteger, BigInteger> addend = point;

        while (k.compareTo(BigInteger.ZERO) != 0) {
            if (k.and(BigInteger.ONE).compareTo(BigInteger.ZERO) != 0) {
                result = pointAdd(result, addend);
            }
            addend = pointAdd(addend, addend);
            k = k.shiftRight(1);
        }
        return result;
    }

    private static Pair<BigInteger, BigInteger> pointNeg(Pair<BigInteger, BigInteger> point) {
        if (point == null) {
            return null;
        }

        return Pair.of(point.getLeft(), point.getRight().negate().mod(p));
    }

    public static class PairOfKeys {
        private final BigInteger privateKey;
        private final Pair<BigInteger, BigInteger> publicKey;

        public PairOfKeys(BigInteger privateKey, Pair<BigInteger, BigInteger> publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }
    }

}
