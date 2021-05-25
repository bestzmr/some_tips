package com.merlin.some_tips.my_javaobjectmemorylayout;

import org.openjdk.jol.info.ClassLayout;

/**
 * jol-core 常用的三个方法
 *
 * ClassLayout.parseInstance(object).toPrintable()：查看对象内部信息.
 * GraphLayout.parseInstance(object).toPrintable()：查看对象外部信息，包括引用的对象.
 * GraphLayout.parseInstance(object).totalSize()：查看对象总大小.
 */
public class JavaObjectMemoryLayout {
    public static void main(String[] args) {
        D d = new D();
        System.out.println(ClassLayout.parseInstance(d).toPrintable());
        /**
         *
         * 可以看到有 OFFSET、SIZE、TYPE DESCRIPTION、VALUE 这几个名词头，它们的含义分别是
         *
         * OFFSET：偏移地址，单位字节；
         * SIZE：占用的内存大小，单位为字节；
         * TYPE DESCRIPTION：类型描述，其中object header为对象头；
         * VALUE：对应内存中当前存储的值，二进制32位；
         *
         *
         * Mark Word在32位JVM中的长度是32bit，在64位JVM中长度是64bit。
         * com.merlin.some_tips.my_javaobjectmemorylayout.D object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1) Mark word
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0) Mark word
         *       8     4        (object header)                           43 c1 00 f8 (01000011 11000001 00000000 11111000) (-134168253)  klass pointer
         *      12     4        (loss due to the next object alignment)  padding 对齐填充
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         */



        int[] a = {1,2,3};
        System.out.println(ClassLayout.parseInstance(a).toPrintable());
        /**
         * [I object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1) Mark word
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0) Mark word
         *       8     4        (object header)                           6d 01 00 f8 (01101101 00000001 00000000 11111000) (-134217363) klass pointer
         *      12     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1) 数组长度
         *      16     4    int [I.<elements>                             N/A   实例数据
         *      20     4        (loss due to the next object alignment)         对齐填充
         * Instance size: 24 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         */
    }
}
