package com.merlin.utils;

/**
 * @author zhaoqiang
 */
public class NumberFillZero {
    /**
     * @param number
     * @return
     * @description 2->02   11->11
     */
    public static String numberFillZero(String number) {
        if (number.length() < 2) {
            return "0" + number;
        }
        return number;
    }

    public static void main(String[] args) {
        System.out.println(numberFillZero("9"));
    }
}
