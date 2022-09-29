package ru.vsu.crypto;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;

public class GeneratorUtils {
    public static void main(String[] args) {
        GeneratorUtils generatorUtils = new GeneratorUtils();
        List<Double> randomList = Arrays.stream(generatorUtils.randomArray(10000000)).boxed().collect(Collectors.toList());
        System.out.println("Математическое ожидание: " + GeneratorUtils.mathExpect(randomList));
        System.out.println("Дисперсия: :" + GeneratorUtils.dispersion(randomList));
        System.out.println("Энтропия: " + GeneratorUtils.entropy(randomList));
    }

    private static final List<Integer> list = new Random()
            .ints(58, 0, (int) Math.pow(2, 30))
            .boxed()
            .collect(Collectors.toList());

//private static final List<Integer> list = IntStream.range(0,58).boxed().collect(Collectors.toList());

    private static final int M = (int) Math.pow(2, 30);
//    private static final int M = (int) 16;

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

    public double periodOfGenerator() {
        List<Integer> periods = new ArrayList<>();
        for (int i = 0; i <= 100; i++) {
            Set<Double> randoms = new HashSet<>();
            while (true) {
                double random = this.random();
                if (randoms.contains(random)) {
                    periods.add(randoms.size());
                    break;
                }
                randoms.add(random);
            }
        }
        return periods.stream().mapToDouble(Integer::intValue).sum() / periods.size();
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
        return - map.values().stream().map(integer -> {
            double ver = (double) integer / listSize;
            return ver * Math.log(ver) / Math.log(2);
        }).mapToDouble(Double::doubleValue).sum();
    }
}

/// X(n) = (X(n-19) + X(n-58))mod(m)
