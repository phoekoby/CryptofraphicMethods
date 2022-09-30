package ru.vsu.crypto;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GeneratorUtils {
    public static void main(String[] args) {
        GeneratorUtils generatorUtils = new GeneratorUtils();
        List<Double> randomList = Arrays.stream(generatorUtils.randomArray(10000000)).boxed().collect(Collectors.toList());
        System.out.println("Математическое ожидание: " + GeneratorUtils.mathExpect(randomList));
        System.out.println("Дисперсия: " + GeneratorUtils.dispersion(randomList));
        System.out.println("Энтропия: " + GeneratorUtils.entropy(randomList));
        System.out.println("Период: " + generatorUtils.periodOfGenerator());
    }

    private static final int M = 8;

    private static final List<Integer> list = new Random()
            .ints(58, 0, M)
            .boxed()
            .collect(Collectors.toList());
    private static final int FIRST_INDEX = 39;
    private static final int SECOND_INDEX = 0;


    //Чтобы избежать переполнения памяти будем просто удалять первый и будем добавлять новое в конец
    public int random() {
        int random = (list.get(FIRST_INDEX) + list.get(SECOND_INDEX)) % M;
        list.remove(0);
        list.add(random);
        return random;
    }

    public double[] randomArray(int arraySize) {
        double[] array = new double[arraySize];
        for (int i = 0; i < arraySize; i++) {
            array[i] = this.random();
        }
        return array;
    }

    public int periodOfGenerator() {
        int count = 10;
        List<Integer> firstLine = IntStream
                .generate(this::random)
                .limit(count)
                .boxed()
                .collect(Collectors.toList());
        this.random();
        int i = 1;
        while (!firstLine.equals(list.subList(list.size() - count, list.size()))) {
            this.random();
            i++;
        }
        return i;
    }

    public static <T extends Number> double mathExpect(List<T> list) {
        return list.stream().mapToDouble(T::doubleValue).sum() / list.size();
    }

    public static <T extends Number> double dispersion(List<T> list) {
        double mathExpect = GeneratorUtils.mathExpect(list);
        return GeneratorUtils.mathExpect(list
                .stream()
                .map(t -> (t.doubleValue() - mathExpect) * (t.doubleValue() - mathExpect))
                .collect(Collectors.toList()));
    }

    public static <T extends Number> double entropy(List<T> list) {
        Map<T, Integer> map = new HashMap<>();
        list.forEach(
                t -> {
                    if (map.containsKey(t)) {
                        map.put(t, map.get(t) + 1);
                    } else {
                        map.put(t, 1);
                    }
                }
        );
        int listSize = list.size();
        return -map.values().stream().map(integer -> {
            double ver = (double) integer / listSize;
            return ver * Math.log(ver) / Math.log(2);
        }).mapToDouble(Double::doubleValue).sum();
    }
}

/// X(n) = (X(n-19) + X(n-58))mod(m)
