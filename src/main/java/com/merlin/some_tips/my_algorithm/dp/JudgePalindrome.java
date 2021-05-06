package com.merlin.some_tips.my_algorithm.dp;

/**
 * @author zhaoqiang
 */
public class JudgePalindrome {
    public static String isPalindrome(String str) {
        int maxLen = 1;
        int beginIndex = 0;
        int len = str.length();
        boolean[][] dp = new boolean[len][len];
        for (int i = 0; i < len; i++) {
            dp[i][i] = true;
        }
        char[] charArray = str.toCharArray();
        for (int j = 1; j < len; j++) {
            for (int i = 0; i < j; i++) {
                if (charArray[i] != charArray[j]) {
                    dp[i][j] = false;
                } else {
                    // 字符串小于3，必定是回文
                    if (j - i < 3) {
                        dp[i][j] = true;
                    } else {
                        dp[i][j] = dp[i + 1][j - 1];

                    }
                }
                if (dp[i][j] && j - i + 1 > maxLen) {
                    maxLen = j - i + 1;
                    beginIndex = i;
                }
            }
        }

        return str.substring(beginIndex,beginIndex+maxLen);
    }

    public static void main(String[] args) {
        System.out.println(isPalindrome("abddcbad"));
    }
}

