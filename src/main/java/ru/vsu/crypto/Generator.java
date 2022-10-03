package ru.vsu.crypto;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class Generator {
    public static void main(String[] args) {
        Generator generator = new Generator();
        List<Double> randomList = Arrays.stream(generator.randomArray(10000000)).boxed().collect(Collectors.toList());
        System.out.println("Математическое ожидание: " + Generator.mathExpect(randomList));
        System.out.println("Дисперсия: " + Generator.dispersion(randomList));
        System.out.println("Энтропия: " + Generator.entropy(randomList));
        System.out.println("Период: " + Generator.periodOfGenerator(generator));
    }

    private static final int M = 4;

    private static final List<Integer> list = new Random()
            .ints(58, 0, M)
            .boxed()
            .collect(Collectors.toList());
    private static final int FIRST_INDEX = 39;
    private static final int SECOND_INDEX = 0;


    //Чтобы избежать переполнения памяти будем просто удалять первый и будем добавлять новое в конец
    public double random() {
        int random = (list.get(FIRST_INDEX) + list.get(SECOND_INDEX)) % M;
        list.remove(0);
        list.add(random);
        return (double) random / M;
    }

    public double[] randomArray(int arraySize) {
        double[] array = new double[arraySize];
        for (int i = 0; i < arraySize; i++) {
            array[i] = this.random();
        }
        return array;
    }

    public static int periodOfGenerator(Generator generator) {
        int count = 10;
        List<Double> firstLine = DoubleStream
                .generate(generator::random)
                .limit(count)
                .boxed()
                .collect(Collectors.toList());
        List<Double> secondLine = new ArrayList<>(List.copyOf(firstLine));
        secondLine.add(generator.random());
        secondLine.remove(0);
        int i = 1;

        while (!firstLine.equals(secondLine)) {
            secondLine.add(generator.random());
            secondLine.remove(0);
            i++;
        }
        return i;
    }

    public static <T extends Number> double mathExpect(List<T> list) {
        return list.stream().mapToDouble(T::doubleValue).sum() / list.size();
    }

    public static <T extends Number> double dispersion(List<T> list) {
        double mathExpect = Generator.mathExpect(list);
        return Generator.mathExpect(list
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
