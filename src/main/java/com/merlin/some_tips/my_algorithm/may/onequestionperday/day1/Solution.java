package com.merlin.some_tips.my_algorithm.may.onequestionperday.day1;

import java.util.Arrays;

/**
 * @author zhaoqiang
 */
public class Solution {
    public static int[] decode(int[] encoded, int first) {
        int len = encoded.length;
        int[] ans = new int[len + 1];
        ans[0] = first;
        int tmp = first;
        for (int i = 0; i <len; i++) {
            tmp = encoded[i] ^ tmp;
            ans[i + 1] = tmp;

        }
        return ans;
    }
    public static void main(String[] args) {
        int[] encoded = {6, 2, 7, 3};
        System.out.println(Arrays.toString(decode(encoded, 4)));;
    }
}
