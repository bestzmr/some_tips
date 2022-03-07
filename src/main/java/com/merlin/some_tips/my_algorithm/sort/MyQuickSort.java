package com.merlin.some_tips.my_algorithm.sort;

/**
 * @author merlin
 * @date 2022/3/7 5:59 下午
 */
public class MyQuickSort {
    public int theKthLargestElement(int[] array,int low,int high,int K) {
        if (array.length < K) {
            return -1;
        }
        int partition = partition(array, low, high);
        if (partition >= K) {
            // 说明第K大元素在[0，partition）之中
            return theKthLargestElement(array, low, partition - 1,K);
        } else if (partition + 1 == K) {
            // partition就为最大元素
            return array[partition];
        } else {
            // 说明第K大元素在(partition，high]之中
            return theKthLargestElement(array, partition+1, high, K);
        }

    }

    public int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = low;
        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                int t = array[j];
                array[j] = array[i];
                array[i] = t;
                i++;

            }
        }
        array[high] = array[i];
        array[i] = pivot;
        return i;
    }
    public static void main(String[] args) {
        int[] array = new int[]{3, 1, 7, 5, 6, 4, 2, 0, 1};
        MyQuickSort myQuickSort = new MyQuickSort();
        System.out.println(myQuickSort.theKthLargestElement(array, 0, array.length - 1, 5));
    }

}
