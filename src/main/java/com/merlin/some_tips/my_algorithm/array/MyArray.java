package com.merlin.some_tips.my_algorithm.array;

/**
 * @author merlin
 * @date 2022/3/15 9:32 上午
 */
public class MyArray {
    //****************************************快慢指针******************************************

    /**
     * 移除有序数组的重复元素
     * @param nums
     * @return
     */
    public int removeDuplicates(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        int slow = 0;
        int fast = 0;
        while (fast < nums.length) {
            if (nums[fast] != nums[slow]) {
                slow++;
                nums[slow] = nums[fast];
            }
            fast++;
        }
        return slow + 1;
    }

    /**
     * 移除指定元素
     * @param nums
     * @param val
     * @return
     */
    public int removeElement(int[] nums, int val) {
        if (nums.length == 0) {
            return 0;
        }
        int slow = 0;
        int fast = 0;
        while (fast < nums.length) {
            if (nums[fast] != val) {
                nums[slow++] = nums[fast];
            }
            fast++;
        }
        return slow;
    }

    /**
     * 移动0
     * @param nums
     */
    public void moveZeroes(int[] nums) {
        int element = removeElement(nums, 0);
        for (int i = element; i < nums.length; i++) {
            nums[i] = 0;
        }
    }
    //****************************************快慢指针******************************************

    //****************************************左右指针******************************************

    /**
     * 两数之和
     * @param numbers
     * @param target
     * @return
     */
    public int[] twoSum(int[] numbers, int target) {
        int left = 0;
        int right = numbers.length - 1;
        while (left < right) {
            if (numbers[left] + numbers[right] == target) {
                return new int[]{left + 1, right + 1};
            } else if (numbers[left] + numbers[right] < target) {
                left++;
            } else {
                right--;
            }
        }
        return new int[]{left + 1, right + 1};
    }

    /**
     * 反转字符串
     * @param s
     */
    public void reverseString(char[] s) {
        int left = 0;
        int right = s.length - 1;
        while (left < right) {
            char ch = s[right];
            s[right--] = s[left];
            s[left++] = ch;
        }
    }


}
