package com.pygman.ngback;

import java.util.Arrays;

public class TestTest {

    public static void main(String[] args) {
        Arrays.sort(new int[1000]);
    }

    private static void shellSort(int[] nums) {
        //TODO
        for (int gap = nums.length / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < nums.length; i++) {
                for (int j = i - gap; j >= 0; j -= gap) {
                    if (nums[j] > nums[j + gap]) {
                        int temp = nums[j];
                        nums[j] = nums[j + gap];
                        nums[j + gap] = temp;
                    }
                }
            }
        }
    }
}
