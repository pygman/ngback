package com.pygman.ngback.util;

import com.pygman.ngback.struct.Pair;

import java.util.Random;

/**
 * 时间id 工具类
 * Created by pygman on 12/2/2016.
 */
public class TheTime {

    private static Random random = new Random();

    public static long newID() {
        long l = System.currentTimeMillis();
        return l << 16 | random.nextInt(1 << 16);
    }

    public static long lastIntegralPoint() {
        long now = System.currentTimeMillis();
        return (now / 60 / 1000 * 60 * 1000) << 16;
    }

    public static long lastIntegralPoint(int mi) {
        long now = System.currentTimeMillis();
        return ((now / 60 / 1000 - mi) * 60 * 1000) << 16;
    }

    public static Pair<Long, Long> lastIntegralPointBetween(int low, int high) {
        if (low > high) {
            int temp = high;
            high = low;
            low = temp;
        }
        long now = System.currentTimeMillis();
        long nowMin = now / 60 / 1000;
        return new Pair<>(((nowMin - high) * 60 * 1000) << 16, ((nowMin - low) * 60 * 1000) << 16);
    }

}
