package com.merlin.some_tips.my_algorithm.linkedlist;

import java.util.List;

/**
 * @author merlin
 * @date 2022/3/3 8:28 下午
 */
public class MyLinkedListExample {
    public static void main(String[] args) {
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        Node node3 = new Node(3);
        Node node4 = new Node(4);
        Node node5 = new Node(5);
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        node5.next = node2;
        System.out.println(linkedListCircleEntryPoint(node1));

    }

    /**
     * 链表中环的入口点
     * @param head
     * @return
     */
    public static int linkedListCircleEntryPoint(Node head) {
        if (head == null || head.next == null) {
            return -1;
        }
        //找到重合位置
        Node fast = head;
        Node slow = head;
        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow) {
                break;
            }
        }
        // 求出起始点
        Node first = head;
        Node second = fast;
        while (first != second) {
            first = first.next;
            second = second.next;
        }
        return first.data;
    }

    /**
     * 链表逆序
     * @param head
     * @return
     */
    public ListNode linkedListReverse(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode tmp;
        ListNode pre = head;
        ListNode cur = head.next;
        pre.next = null;
        while (cur != null) {
            tmp = cur.next;
            cur.next = pre;
            pre = cur;
            cur = tmp;
        }
        return pre;
    }

    /**
     * 移除倒数第n个节点
     * @param head
     * @param n
     * @return
     */
    public ListNode removeLastSpecifyNode(ListNode head, int n) {
        if (head == null) {
            return head;
        }
        // 哨兵节点
        ListNode guard = new ListNode(-1);
        guard.next = head;
        ListNode fast = guard;
        int i = 0;
        while (i++ <n) {
            fast = fast.next;
        }
        ListNode slow = guard;
        while (fast.next != null) {
            slow = slow.next;
            fast = fast.next;
        }
        slow.next = slow.next.next;
        return guard.next;
    }

    /**
     * 检测节点是否有环
     * @param head
     * @return
     */
    public boolean checkLinkedListCircle(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        ListNode fast = head;
        ListNode slow = head;
        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (slow == fast) {
                return true;
            }
        }
        return false;
    }

    /**
     * 链表节点中间位置
     * @param head
     * @return
     */
    public ListNode linkedListMidNode(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode fast = head;
        ListNode slow = head;
        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }
        return slow;
    }

    /**
     * 合并两个有序链表使之变为一个有序链表
     * @param node1
     * @param node2
     * @return
     */
    public ListNode mergeOrderlyLinkedList(ListNode l1, ListNode l2) {
        ListNode guard = new ListNode(-1);
        ListNode cur = guard;
        ListNode p = l1;
        ListNode q = l2;
        while (p != null || q != null) {
            if (p == null) {
                cur.next = q;
                return guard.next;
            }
            if (q == null) {
                cur.next = p;
                return guard.next;
            }
            if (p.val > q.val) {
                cur.next = q;
                q = q.next;
                cur = cur.next;
            } else {
                cur.next = p;
                p = p.next;
                cur = cur.next;
            }
        }
        return guard.next;
    }
    // todo 时间超时
    public ListNode mergeOrderlyLinkedList(ListNode[] lists) {
        ListNode l1 = lists[0];
        int i = 1;
        ListNode l2 = lists[i];

        while (i < lists.length) {
            l1 = mergeOrderlyLinkedList(l1, l2);
            i++;
        }
        return l1;
    }
}

class Node{
    int data;
    Node next;

    public Node(int data) {
        this.data = data;
    }
}

class ListNode {
    int val;
    ListNode next;
    ListNode(int x) { val = x; }
}