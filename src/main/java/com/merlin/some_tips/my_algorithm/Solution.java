package com.merlin.some_tips.my_algorithm;

import java.util.*;

/**
 * @author merlin
 * @date 2022/4/7 9:34 上午
 */
public class Solution {
    public boolean rotateString(String s, String goal) {
        if (s.equals(goal)) {
            return true;
        }
        int count = s.length();
        while (count != 0) {
            s = s.substring(1) + s.charAt(0);
            if (s.equals(goal)) {
                return true;
            }
            count--;
        }
        return false;
    }

    public List<List<Integer>> levelOrder(Node root) {
        List<List<Integer>> ans = new ArrayList<>();
        if (root == null) {
            return ans;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> nums = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Node node = queue.poll();
                nums.add(node.val);
                queue.addAll(node.children);
            }
            ans.add(nums);
        }
        return ans;
    }
    public int countNumbersWithUniqueDigits(int n) {
        if (n==0) {
            return 1;
        }
        if (n == 1) {
            return 10;
        }
        int res = 10, cur = 9;
        for (int i = 0; i < n - 1; i++) {
            cur = cur * (9 - i);
            res += cur;
        }
        return res;
    }


    public int[] numberOfLines(int[] widths, String s) {
        int count = 0;
        int level = 1;
        for (int i = 0; i < s.length(); i++) {
            count += widths[s.charAt(i) - 97];
            if (count > 100) {
                level++;
                count = 0;
                i--;
            }
        }
        return new int[]{level, count};
    }

    public int maximumWealth(int[][] accounts) {
        int max = 0;
        for (int i = 0; i < accounts.length; i++) {
            int clientMax = 0;
            for (int j = 0; j < accounts[i].length; j++) {
                clientMax += accounts[i][j];
            }
            if (clientMax > max) {
                max = clientMax;
            }
        }
        return max;
    }

    int index = 0;
    public NestedInteger deserialize(String s) {
        if (s.charAt(index) == '[') {
            NestedInteger ni = new NestedInteger();
            index++;
            while (s.charAt(index) != ']') {
                ni.add(deserialize(s));
                if (s.charAt(index) == ',') {
                    index++;
                }
            }
            index++;
            return ni;
        } else {
            boolean negative = false;
            if (s.charAt(index) == '-') {
                negative = true;
                index++;
            }
            int num = 0;
            while (s.length() > index && s.charAt(index) != ',' && s.charAt(index) != ']') {
                int t = (int) s.charAt(index) - 48;
                num = num * 10 + t;
                index++;
            }
            if (negative) {
                num = -1 * num;
            }
            return new NestedInteger(num);
        }

    }

    public List<Integer> lexicalOrder(int n) {
        
        return null;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
//        System.out.println(solution.countNumbersWithUniqueDigits(3));
        char c = '8';
        int t = (int) c-48;
        System.out.println(t);

    }
}
class Node {
    public int val;
    public List<Node> children;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
};
class RandomizedSet {
    List<Integer> list;
    Set<Integer> set;
    public RandomizedSet() {
        set = new HashSet<>();
        list = new ArrayList<>();
    }

    public boolean insert(int val) {
        if (set.contains(val)) {
            return false;
        }
        return set.add(val);
    }

    public boolean remove(int val) {
        return set.remove(val);
    }

    public int getRandom() {
        list.clear();
        list.addAll(set);
        return list.get(new Random().nextInt(list.size()));
    }
}

/**
 * // This is the interface that allows for creating nested lists.
 * // You should not implement it, or speculate about its implementation
 * public interface NestedInteger {
 * // Constructor initializes an empty nested list.
 * public NestedInteger();
 * <p>
 * // Constructor initializes a single integer.
 * public NestedInteger(int value);
 * <p>
 * // @return true if this NestedInteger holds a single integer, rather than a nested list.
 * public boolean isInteger();
 * <p>
 * // @return the single integer that this NestedInteger holds, if it holds a single integer
 * // Return null if this NestedInteger holds a nested list
 * public Integer getInteger();
 * <p>
 * // Set this NestedInteger to hold a single integer.
 * public void setInteger(int value);
 * <p>
 * // Set this NestedInteger to hold a nested list and adds a nested integer to it.
 * public void add(NestedInteger ni);
 * <p>
 * // @return the nested list that this NestedInteger holds, if it holds a nested list
 * // Return empty list if this NestedInteger holds a single integer
 * public List<NestedInteger> getList();
 * }
 */
class NestedInteger {
    Integer singleInteger;
    List<NestedInteger> nestedIntegers;
    public NestedInteger(){
        nestedIntegers = new ArrayList<>();
    }
    // Constructor initializes a single integer.
    public NestedInteger(int value){
        singleInteger = value;
    }

    // @return true if this NestedInteger holds a single integer, rather than a nested
    public boolean isInteger(){
        return singleInteger != null;
    }
    // @return the single integer that this NestedInteger holds, if it holds a single
// Return null if this NestedInteger holds a nested list
    public Integer getInteger(){
        return singleInteger;
    }
    // Set this NestedInteger to hold a single integer.
    public void setInteger(int value){
        singleInteger = value;
    }
    // Set this NestedInteger to hold a nested list and adds a nested integer to it.
    public void add(NestedInteger ni){
        nestedIntegers.add(ni);
    }
    // @return the nested list that this NestedInteger holds, if it holds a nested lis
// Return empty list if this NestedInteger holds a single integer
    public List<NestedInteger> getList(){
        return nestedIntegers;
    }
}
