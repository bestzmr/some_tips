package com.merlin.some_tips.my_algorithm.string;

/**
 * @author merlin
 * @date 2022/3/15 10:12 上午
 */
public class MyString {
    /**
     * 最长回文字串
     * 左右指针
     * @param s
     * @return
     */
    public String longestPalindrome(String s) {
        String ans = "";
        for (int i = 0; i < s.length(); i++) {
            String res1 = palindrome(s, i, i);
            String res2 = palindrome(s, i, i + 1);
            ans = res1.length() > res2.length() ? (res1.length() > ans.length() ? res1 : ans) : (res2.length() > ans.length() ? res2 : ans);
        }
        return ans;
    }

    public String palindrome(String s, int left, int right) {

        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return s.substring(left + 1, right);
    }
}
