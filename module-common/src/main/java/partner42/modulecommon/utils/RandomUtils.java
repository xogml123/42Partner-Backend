package partner42.modulecommon.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomUtils {

    private static final Random random = new Random();

    public static String getRandomString(Integer range) {
        return Character.toString((random.nextInt() / range));
    }

    public static List<Integer> createRangeDistinctIntegerList(int s, int e) {
        if (s > e) {
            throw new IllegalArgumentException("start must be smaller than or be equals with end");
        }
        List<Integer> integers = new ArrayList<>(e - s + 1);
        for (int i = s; i < e; i++){
            integers.add(i);
        }
        Collections.shuffle(integers);
        return integers;
    }
}
