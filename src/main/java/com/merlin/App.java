package com.merlin;

/**
 * Hello world!
 *
 * @author zhaoqiang
 */
public class App 
{
    public int nn = 1;

    public void printSuiBian() {
        System.out.println("hello world");
    }
    public static class InnerClass {
        public static int staticMem = 1;
        public int num = 2;

        public void printNum() {
            System.out.println(num);
            System.out.println(staticMem);
        }
    }

//    public class InnerClass2 {
//        public int num = 1;
//        public static int staticNum = 2;
//
//        public static void printNum() {
//
//        }

//    }
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        App.InnerClass innerClass = new InnerClass();
        innerClass.printNum();
    }
}