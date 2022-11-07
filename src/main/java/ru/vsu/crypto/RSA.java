package ru.vsu.crypto;

import org.apache.commons.lang3.tuple.Pair;

//import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.valueOf;

public class RSA {
    //    17) n = 1814354438978629     e = 11119
//    ШТ = 607333454985473748183881750546921891989337248193427278679982905285071060323
//            1301937207488228
    public static void main(String[] args) {
        long n = 1814354438978629L;
        long e = 11119L;
        List<Long> c = strToLongs("6073334549854737481838817505469218919893372481934272786799829052850710603231301937207488228", n);
        List<BigInteger> message = new ArrayList<>();
        Pair<BigInteger, BigInteger> pq = primeFactors(BigInteger.valueOf(n));
        BigInteger q = pq.getLeft();
        BigInteger p = pq.getRight();
        BigInteger d = findKeyD(BigInteger.valueOf(e), eylerFunction(q, p));
        for (long value : c) {
            BigInteger tmp = new BigInteger(String.valueOf(value));
            message.add(tmp.modPow(d, BigInteger.valueOf(n)));
        }
        StringBuilder result = new StringBuilder();
        for (BigInteger bigInteger : message) {
            result.append(bigInteger.toString());
        }
        System.out.println(stringNumbersToNormalString(result.toString()));

    }

    @SuppressWarnings("all")
    private static Pair<BigInteger, BigInteger> primeFactors(BigInteger number) {
        BigInteger n = number;
        BigInteger i;
        for (i = BigInteger.TWO; i.compareTo(number) <= 0; i = i.add(BigInteger.ONE)) {
            if (number.mod(i).equals(BigInteger.ZERO)) {
                number = number.divide(i);
                i = i.subtract(BigInteger.ONE);
            }
        }
        return Pair.of(i, n.divide(i));
    }

    private static BigInteger eylerFunction(BigInteger q, BigInteger p) {
        return q.subtract(BigInteger.ONE).multiply(p.subtract(BigInteger.ONE));
    }

    private static BigInteger findKeyD(BigInteger e, BigInteger eyler) {
//        System.out.println(eyler.longValue());
        AtomicLong x = new AtomicLong(), y = new AtomicLong();
        long gcd = extended_gcd(e.longValue(), eyler.longValue(), x, y);
        return BigInteger.valueOf(x.get()).mod(eyler).add(eyler).mod(eyler);
    }
    public static long extended_gcd(long a, long b, AtomicLong x, AtomicLong y) {
        if (a == 0) {
            x.set(0);
            y.set(1);
            return b;
        }

        AtomicLong _x = new AtomicLong(), _y = new AtomicLong();
        long gcd = extended_gcd(b % a, a, _x, _y);

        x.set(_y.get() - (b / a) * _x.get());
        y.set(_x.get());

        return gcd;
    }

    public static List<Long> strToLongs(String num, long x) {
        List<Long> out = new ArrayList<>();
        char[] n = num.toCharArray();
        StringBuilder tmp1 = new StringBuilder();
        StringBuilder tmp2 = new StringBuilder();
        for (char c : n) {
            tmp1.append(c);
            if (Long.parseLong(valueOf(tmp1)) > x) {
                out.add(Long.parseLong(valueOf(tmp2)));
                tmp1 = new StringBuilder();
                tmp2 = new StringBuilder();
                tmp1.append(c);
                tmp2.append(c);
            } else {
                tmp2.append(c);
            }
        }
        out.add(Long.parseLong(valueOf(tmp2)));
        return out;
    }

    public static String stringNumbersToNormalString(String numbers) {
        char[] str = numbers.toCharArray();
        byte[] b = new byte[str.length / 2];
        for (int i = 0; i < b.length; i++) {
            b[i] = Byte.parseByte((String.valueOf(str[i * 2]) + String.valueOf(str[i * 2 + 1])));
        }

        return new String(b);
    }
}
