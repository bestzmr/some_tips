package com.merlin.some_tips;

public class TestClass {
    public static void main(String[] args) {
        ClassInit init=ClassInit.newInstance();
 
        System.out.println(init.x);
        System.out.println(init.y);
    }
}
 
class ClassInit{
    private static ClassInit init=new ClassInit();
    public static int x;
    public static int y=0;
    static{
        x=10;
        y=10;
    }
    private ClassInit(){
        x++;
        y++;
    }
    public static ClassInit newInstance(){
        return init;
    }
}
