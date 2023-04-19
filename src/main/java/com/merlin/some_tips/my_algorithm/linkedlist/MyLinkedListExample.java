package com.merlin.some_tips.my_algorithm.linkedlist;

import java.util.List;
import java.util.PriorityQueue;

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
     * @param l1
     * @param l2
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

    /**
     * 合并k个有序链表(使用优先级队列--最小堆)
     * @param lists
     * @return
     */
    public ListNode mergeOrderlyLinkedList(ListNode[] lists) {
        ListNode dummy = new ListNode(-1);
        ListNode cur = dummy;
        PriorityQueue<ListNode> pq = new PriorityQueue<>(lists.length, (a, b) -> (a.val - b.val));
        for (ListNode node : lists) {
            if (node != null) {
                pq.add(node);
            }
        }
        while (!pq.isEmpty()) {
            ListNode node = pq.poll();
            cur.next = node;
            if (node.next != null) {
                pq.add(node.next);
            }
            cur = cur.next;
        }
        return dummy.next;

    }

    /**
     * 相交链表 第一种解法
     * headA: A->B->C->D
     * headB: E->F->G->C->D
     *
     * A->B->C->D->E->F->G->C->D
     * E->F->G->C->D->A->B->C->D
     * @param headA
     * @param headB
     * @return
     */
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode p = headA;
        ListNode q = headB;
        while (p != null || q != null) {
            if (p == q) {
                return p;
            }
            if (p == null) {
                p = headB;
                continue;
            }
            if (q == null) {
                q = headA;
                continue;
            }
            p = p.next;
            q = q.next;
        }
        return null;
    }
    /**
     * 相交链表 第二种解法,将链表1的尾节点的指针指向链表2的头节点，
     * 构造成一个环，通过求环的入口，从而求出相交节点
     * headA: A->B->C->D
     * headB: E->F->G->C->D
     *
     * A->B->C->D->E->F->G->C->D
     * E->F->G->C->D->A->B->C->D
     * @param headA
     * @param headB
     * @return
     */
    public ListNode getIntersectionNodeByCycle(ListNode headA, ListNode headB) {
        if (headA == headB) {
            return headA;
        }
        ListNode p = headA;// p为尾节点
        while (p.next != null) {
            p = p.next;
        }
        p.next = headB;
        ListNode fast = headA;
        ListNode slow = headA;
        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow) {
                break;
            }
        }
        if (fast.next == null || fast.next.next == null) {
            p.next = null;
            return null;
        }
        slow = headA;
        while (fast != null) {
            if (fast == slow) {
                p.next = null;
                return slow;
            }
            fast = fast.next;
            slow = slow.next;

        }
        p.next = null;
        return null;


    }

    /**
     * 删除有序链表中重复元素
     * @param head
     * @return
     */
    public ListNode deleteDuplicates(ListNode head) {
        if (head == null) {
            return null;
        }
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null) {
            if (fast.val != slow.val) {
                slow.next = fast;
                slow = fast;
            }
            fast = fast.next;
        }
        slow.next = null;
        return head;
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