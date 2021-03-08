package com.merlin.some_tips;

public class InnerClassDemo {
	public void print() {

	}
	public static int ll = 1;
	public int ll2 = 2;
	class InnerClass{
		//1、这样是可以的，这样编译器会在编译的时候就将这种语句变成常量值（也就是说可以定义所有的static final + 基本数据类型）
		static final int i = 50;

		//2、这样不可以，虽然也为static final 但是说到底还是个变量对象，所以不可以这样
//		static final String str = new String("");
		public InnerClass() {
			System.out.println("init");
		}
		//3、与上相同
		InnerClass innerClass = new InnerClass();
	}

	static class StaticInnerClass {
		public void println() {
			System.out.println(ll);
//			System.out.println();
		}

	}

	public static void main(String[] args) {
		InnerClassDemo innerClassDemo = new InnerClassDemo();
	}
}

