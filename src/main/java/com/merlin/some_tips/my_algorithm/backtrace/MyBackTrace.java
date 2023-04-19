package com.merlin.some_tips.my_algorithm.backtrace;


import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author merlin
 * @date 2022/3/11 1:57 下午
 */
public class MyBackTrace {
    /**
     * 全排列
     * @param nums
     * @return
     */
    List<List<Integer>> ans = new ArrayList<>();
    public List<List<Integer>> permute(int[] nums) {
        // 路径
        LinkedList<Integer> trace = new LinkedList<>();
        // 用来记录数字是否使用过
        boolean[] used = new boolean[nums.length];
        fullPermutation(nums, trace, used);
        return ans;
    }

    public void fullPermutation(int[] nums,LinkedList<Integer> trace, boolean[] used) {
        if (nums.length == trace.size()) {
            ans.add(new ArrayList<>(trace));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) {
                continue;
            }
            used[i] = true;
            trace.add(nums[i]);
            fullPermutation(nums, trace, used);
            used[i] = false;
            trace.removeLast();

        }
    }

    /**
     * N皇后
     *
     * @param n
     * @return
     */
    List<List<String>> result = new ArrayList<>();
    public List<List<String>> solveNQueens(int n) {
        String[][] board = new String[n][n];
        for (int i = 0; i < board.length; i++) {
            Arrays.fill(board[i], ".");
        }
        backtraceQueues(board,0);
        return result;
    }

    public void backtraceQueues(String[][]board,int row) {
        if (row==board.length) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < board.length; i++) {
                list.add(String.join("", board[i]));
            }
            result.add(list);
            return;
        }
        for (int j = 0; j < board[0].length; j++) {
            if (!isValid(board, row, j)) {
                continue;
            }
            board[row][j] = "Q";
            backtraceQueues(board, row + 1);
            board[row][j] = ".";
        }
    }

    public boolean isValid(String[][] board, int row, int col) {
        for (int i = row-1; i >=0; i--) {
            if ("Q".equals(board[i][col])) {
                return false;
            }
        }
        for (int i = row - 1, j = col + 1; i >= 0 && j < board.length; i--, j++) {
            if ("Q".equals(board[i][j])) {
                return false;
            }
        }
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if ("Q".equals(board[i][j])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 字母组合
     *
     * @param digits
     * @return
     */
    List<String> letters = new ArrayList<>();
    String[] letterMap = new String[]{
            "",
            "",
            "abc",
            "def",
            "ghi",
            "jkl",
            "mno",
            "pqrs",
            "tuv",
            "wxyz"
    };
    public List<String> letterCombinations(String digits) {
        if ("".equals(digits)) {
            return letters;
        }

        letterBackTrace(digits, 0, "");
        return letters;
    }

    public void letterBackTrace(String digits,int index,String s) {
        if (s.length() == digits.length()) {
            letters.add(s);
            return;
        }
        char c = digits.charAt(index);
        String let = letterMap[c - '0'];
        for (int i = 0; i < let.length(); i++) {
            letterBackTrace(digits, index + 1, s + let.charAt(i));
        }
        return;
    }

    /**
     * 最小索引和
     * @param list1
     * @param list2
     * @return
     */
    public String[] findRestaurant(String[] list1, String[] list2) {
        List<String> stringList = new ArrayList<>(Arrays.asList(list2));
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < list1.length; i++) {
            int index = stringList.indexOf(list1[i]);
            if (index == -1) {
                continue;
            }
            map.put(list1[i], i + index);
        }
        Integer value = map.entrySet().stream().min((x, y) -> x.getValue() - y.getValue()).get().getValue();
        return map.entrySet().stream().filter(item -> item.getValue().equals(value)).map(Map.Entry::getKey).toArray(String[]::new);
    }
    public static void main(String[] args) {
        MyBackTrace myBackTrace = new MyBackTrace();
//        myBackTrace.permute(new int[]{1, 2, 3});
        myBackTrace.solveNQueens(4);

        String[] strings = new String[3];
        strings[0] = "qwer";
        strings[1] = "asdf";
        strings[2] = "asdfa";
        String[] strings1 = new String[3];
        strings1[0] = "tyui";
        strings1[1] = "qwer";
        strings1[2] = "asdfa";
        System.out.println(Arrays.deepToString(myBackTrace.findRestaurant(strings, strings1)));

    }
}
