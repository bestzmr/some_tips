package com.merlin.some_tips.my_algorithm.lru;

/**
 * @author merlin
 * @date 2022/3/3 4:38 下午
 */

/**
 * lru缓存淘汰算法
 * 维护一个有序单链表，越靠近链表尾部的结点是越早之前访问的。当有一个新的数据被访问时，我们从链表头开始顺序遍历链表。
 * 1. 如果此数据之前已经被缓存在链表中了，我们遍历得到这个数据对应的结点，并将其从原来的位置删除，然后再插入到链表的头部。
 * 2. 如果此数据没有在缓存链表中，又可以分为两种情况：
 *         如果此时缓存未满，则将此结点直接插入到链表的头部；
 *         如果此时缓存已满，则链表尾结点删除，将新的数据结点插入链表的头部。
 * 这样我们就用链表实现了一个 LRU 缓存
 */
public class MyLRU {
    public static void print(MyLinkedList list) {

        if (list == null || list.node == null) {
            return;
        }
        Node node = list.node;
        while (node.next != null) {
            node = node.next;
            System.out.println(node.data);
        }
    }
    public static String getData(MyLinkedList list,String data) {
        Node head = list.node;
        if (head == null) {
            return null;
        }
        Node pre = head;
        while (pre.next != null) {

            Node cur = pre.next;
            if (cur.data.equals(data)) {
                pre.next = cur.next;
                cur.next = head.next;
                head.next = cur;
                return cur.data;
            }
            pre = cur;
        }
        return null;
    }

    public static void addData(MyLinkedList list,String data) {
        if (list.node==null) {
            Node head = new Node(null);
            Node node = new Node(data);
            node.next = null;
            head.next = node;
            list.node = head;
            return;
        }
        if (list.size < list.capacity) {
            //链表没满
            Node head = list.node;
            Node node = new Node(data);
            node.next = head.next;
            head.next = node;
            list.size++;
        } else {
            //链表满了
            Node node = list.node;
            while ( node.next.next != null) {
                node = node.next;
            }
            node.next = null;
            list.size--;
        }
    }

    public static void main(String[] args) {
        MyLinkedList list = new MyLinkedList();
        addData(list, "hello");
        addData(list, "world");
        String hello = getData(list, "hello");
        System.out.println(hello);
        print(list);
    }
}

class Node{
    String data;
    Node next;

    public Node(String data) {
        this.data = data;
    }
}
class MyLinkedList{
    Node node;
    int size;
    int capacity=10;
}