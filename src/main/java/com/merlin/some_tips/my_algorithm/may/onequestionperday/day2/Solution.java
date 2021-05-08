package com.merlin.some_tips.my_algorithm.may.onequestionperday.day2;

/**
 * @author zhaoqiang
 */
public class Solution {
    public  static int xorOperation(int n, int start) {
        int ans = start;
        for (int i = 1; i < n; i++) {
            ans = ans ^ (start + 2 * i);
        }
        return ans;
    }

    public static void main(String[] args) {
        System.out.println(xorOperation(4, 3));;
    }
}
