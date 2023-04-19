package com.merlin.some_tips.my_algorithm.slide_window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author merlin
 * @date 2022/3/16 11:14 上午
 */
public class MySlideWindow {
    /**
     * 最小覆盖字串
     * @param s
     * @param t
     * @return
     */
    public String slideWindow(String s, String t) {
        char[] chars = s.toCharArray();
        char[] target = t.toCharArray();
        Map<Character, Integer> needs = new HashMap<>();
        for (int i = 0; i < target.length; i++) {
            needs.put(target[i], needs.getOrDefault(target[i], 0) + 1);
        }
        Map<Character, Integer> window = new HashMap<>();
        int left = 0, right = 0;
        int valid = 0;// 以满足条件的个数
        int start = 0;
        int len = Integer.MAX_VALUE;
        while (right < s.length()) {
            char c = chars[right];
            right++;
            if (needs.containsKey(c)) {
                window.put(c, window.getOrDefault(c, 0) + 1);
                if (window.getOrDefault(c, 0).equals(needs.get(c))) {
                    valid++;
                }
            }
            while (valid == needs.size()) {
                if (right - left < len) {
                    start = left;
                    len = right - left;
                }
                char c2 = chars[left];
                left++;
                if (needs.containsKey(c2)) {
                    if (window.getOrDefault(c2, 0).equals(needs.get(c2))) {
                        valid--;
                    }
                    window.put(c2, window.get(c2) - 1);
                }
            }

        }
        return len == Integer.MAX_VALUE ? "" : s.substring(start, start + len);
    }

    /**
     * 判断s2是否包含s1
     * @param s1
     * @param s2
     * @return
     */
    public boolean checkInclusion(String s1, String s2) {
        if (s1.length() > s2.length()) {
            return false;
        }
        char[] needChars = s1.toCharArray();
        Map<Character, Integer> needs = new HashMap<>();
        Map<Character, Integer> window = new HashMap<>();
        for (int i = 0; i < needChars.length; i++) {
            needs.put(needChars[i], needs.getOrDefault(needChars[i], 0) + 1);
        }
        int left = 0, right = 0;
        int valid = 0;
        int start = 0;
        int len = Integer.MAX_VALUE;
        while (right < s2.length()) {
            char c = s2.charAt(right);
            right++;
            window.put(c, window.getOrDefault(c, 0) + 1);
            if (needs.containsKey(c)) {
                if (window.get(c).equals(needs.get(c))) {
                    valid++;
                }
            }
            while (valid == needs.size()) {
                if (right - left < len) {
                    start = left;
                    len = right - left;
                }
                char c2 = s2.charAt(left);
                left++;
                if (needs.containsKey(c2)) {
                    if (window.get(c2).equals(needs.get(c2))) {
                        valid--;
                    }
                    window.put(c2, window.get(c2) - 1);
                }
            }
        }
        if (len == Integer.MAX_VALUE) {
            return false;
        }
        return s2.substring(start, start + len).length() == s1.length();
    }

    /**
     * 找到字符串中所有字母异位词
     * @param s
     * @param p
     * @return
     */
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> ans = new ArrayList<>();
        char[] chars = p.toCharArray();
        Map<Character, Integer> needs = new HashMap<>();
        Map<Character, Integer> window = new HashMap<>();
        for (int i = 0; i < chars.length; i++) {
            needs.put(chars[i], needs.getOrDefault(chars[i], 0) + 1);
        }
        int left = 0, right = 0;
        int valid = 0;
        while (right < s.length()) {
            char c = s.charAt(right);
            right++;
            if (needs.containsKey(c)) {
                window.put(c, window.getOrDefault(c, 0) + 1);
                if (window.get(c).equals(needs.get(c))) {
                    valid++;
                }
            }
            while (valid == needs.size()) {
                if (right - left == p.length()) {
                    ans.add(left);
                }
                char c1 = s.charAt(left);
                left++;
                if (needs.containsKey(c1)) {
                    if (window.get(c1).equals(needs.get(c1))) {
                        valid--;
                    }
                    window.put(c1, window.get(c1) - 1);
                }

            }
        }
        return ans;

    }

    /**
     * 无重复字符的最长字串
     * @param s
     * @return
     */
    public int lengthOfLongestSubstring(String s) {
        int left = 0,right = 0;
        Map<Character, Integer> window = new HashMap<>();
        int max = 0;
        while (right < s.length()) {
            char c = s.charAt(right++);
            window.put(c, window.getOrDefault(c, 0) + 1);
            while (window.get(c) > 1) {
                char c1 = s.charAt(left++);
                window.put(c1, window.get(c1) - 1);
            }
            max = Math.max(max, right - left);
        }
        return max;
    }
    public static void main(String[] args) {
        MySlideWindow mySlideWindow = new MySlideWindow();
        //"cbaebabacd"
        //"abc"
        mySlideWindow.findAnagrams("cbaebabacd", "abc");
    }
}
