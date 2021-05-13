package com.merlin.some_tips.my_algorithm.may.onequestionperday.day3;

/**
 * @author zhaoqiang
 * 每日亿题
 * 1269 停在原地的方案数
 */
public class Solution {
    public static int numWays(int steps, int arrLen) {
        int maxColumn = Math.min(arrLen - 1, steps);
        int[][] dp = new int[steps + 1][maxColumn + 1];
        dp[0][0] = 1;
        for (int i = 1; i <= steps; i++) {
            for (int j = 0; j <= maxColumn; j++) {
                dp[i][j] = dp[i - 1][j];
                if (j - 1 >= 0) {
                    dp[i][j] = (dp[i][j] + dp[i - 1][j - 1]) ;
                }
                if (j + 1 <= maxColumn) {
                    dp[i][j] = (dp[i][j] + dp[i - 1][j + 1]);
                }
            }
        }

        System.out.println(dp[steps][0]);
        return dp[steps][0];
    }

    public static void main(String[] args) {
        numWays(3, 2);
    }
}
