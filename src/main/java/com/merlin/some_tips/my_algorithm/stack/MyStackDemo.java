package com.merlin.some_tips.my_algorithm.stack;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author merlin
 * @date 2022/3/4 2:19 下午
 */
public class MyStackDemo {
    // tokens = ["2","1","+","3","*"]
    public static int computeExpression(String[] tokens) {
        MyStack numStack = new MyStack(100);
        MyStack strStack = new MyStack(100);

        for (int i = 0; i < tokens.length; i++) {
            if (StringUtils.isNumeric(tokens[i])) {
                // 数字
                numStack.put(tokens);
            } else {
                // 运算符
                // 运算符栈为空
                if (strStack.count == 0) {
                    strStack.put(tokens[i]);
                }
                // 运算符栈不为空，将它取出与当前运算符比较
                String ch = (String) strStack.get();
                if (("+".equals(ch) || "-".equals(ch)) && ("*".equals(tokens[i]) || "/".equals(tokens[i]))) {
                    strStack.put(tokens[i]);
                }else{
                    int result;
                    int x = (int) numStack.getAndRemove();
                    int y = (int) numStack.getAndRemove();
                    switch (tokens[i]) {
                        case "*":
                            result = y * x;
                            break;
                        case "/":
                            result = y / x;
                            break;
                        case "+":
                            result = y + x;
                            break;
                        case "-":
                            result = y - x;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + tokens[i]);
                    }
                    numStack.put(result);
                    i--;
                }
            }
        }
        if (strStack.count != 0) {
            int x = (int) numStack.getAndRemove();
            int y = (int) numStack.getAndRemove();
            String operator = (String) strStack.getAndRemove();
            switch (operator) {
                case "*":
                    return y * x;
                case "/":
                    return y / x;
                case "+":
                    return y + x;
                case "-":
                    return y - x;
                default:
                    throw new IllegalStateException("Unexpected value: " + operator);
            }
        }

        return (int) numStack.get();
    }

    public static void main(String[] args) {
        System.out.println(StringUtils.isNumeric("234"));
    }
}
class MyStack{
    Object[] data;
    int size;
    int count;

    public MyStack(int size) {
        this.data = new Object[size];
        this.size = size;
        this.count = 0;
    }
    public boolean put(Object val) {
        if (count == size) {
            return false;
        }
        data[count++] = val;
        return true;
    }

    public Object getAndRemove() {
        if (count == 0) {
            return null;
        }
        return data[--count];
    }
    public Object get() {
        if (count == 0) {
            return null;
        }
        return data[count-1];
    }

}
