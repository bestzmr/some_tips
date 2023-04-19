package com.merlin.some_tips.my_algorithm.tree;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.val;
import org.elasticsearch.search.aggregations.metrics.Max;

import javax.print.DocFlavor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author merlin
 * @date 2022/3/14 10:54 上午
 */
public class MyTree {
    //****************************************BFS****************************************
    public int minDepth(TreeNode root) {

        if (root == null) {
            return 0;
        }
        int depth = 1;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node.left == null && node.right == null) {
                    return depth;
                }
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }

            depth++;
        }
        return depth;
    }

    /**
     * 二叉树的层序遍历 II
     *
     * @param root
     * @return
     */
    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        LinkedList<List<Integer>> ans = new LinkedList<>();
        if (root == null) {
            return ans;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> layerNums = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode treeNode = queue.poll();
                layerNums.add(treeNode.val);
                if (treeNode.left != null) {
                    queue.add(treeNode.left);
                }
                if (treeNode.right != null) {
                    queue.add(treeNode.right);
                }
            }
            ans.addFirst(layerNums);
        }
        return ans;

    }

    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> ans = new ArrayList<>();
        if (root == null) {
            return ans;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        boolean flag = true; // 左->右
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> oneLevel = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode treeNode = queue.poll();
                oneLevel.add(treeNode.val);
                if (treeNode.left != null) {
                    queue.add(treeNode.left);
                }
                if (treeNode.right != null) {
                    queue.add(treeNode.right);
                }

            }
            if (!flag) {
                Collections.reverse(oneLevel);
            }
            flag = !flag;
            ans.add(oneLevel);
        }
        return ans;
    }

    public Node connect(Node root) {
        if (root == null) {
            return null;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            Node pre = queue.poll();
            pre.next = null;
            if (pre.left != null) {
                queue.add(pre.left);
            }
            if (pre.right != null) {
                queue.add(pre.right);
            }
            Node cur = null;
            for (int i = 0; i < size - 1; i++) {
                cur = queue.poll();
                cur.next = null;
                pre.next = cur;
                pre = cur;
                if (cur.left != null) {
                    queue.add(cur.left);
                }
                if (cur.right != null) {
                    queue.add(cur.right);
                }
            }
        }
        return root;
    }

//****************************************BFS****************************************

    public void qusort(int[] array, int low, int high) {
        if (low >= high) {
            return;
        }
        int partition = partition(array, low, high);
        qusort(array, low, partition - 1);
        qusort(array, partition + 1, high);
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

    public void mergeSort(int[] array, int low, int high) {
        if (low >= high) {
            return;
        }
        int mid = low + (high - low) >> 2;
        mergeSort(array, low, mid);
        mergeSort(array, mid + 1, high);
        merge(array, low, mid, high);
    }

    void merge(int[] array, int low, int mid, int high) {

        int[] temp = new int[high - low + 1];
        int i = low;
        int j = mid + 1;
        int k = 0;
        while (i <= mid && j <= high) {
            if (array[i] < array[j]) {
                temp[k++] = array[i++];
            } else {
                temp[k++] = array[j++];
            }
        }
        int start = i, end = mid;
        if (start > mid) {
            start = j;
            end = high;
        }
        while (start <= end) {
            temp[k++] = array[start++];
        }
        for (int l = 0; l < temp.length; l++) {
            array[l + low] = temp[l];
        }

    }

    /**
     * 被围绕的区域
     *
     * @param board
     */
    public void solve(char[][] board) {

    }


    public void recoverTree(TreeNode root) {
        TreeNode node1 = null, node2 = null, pred = null;
        Deque<TreeNode> deque = new ArrayDeque<>();
        while (!deque.isEmpty() || root != null) {
            while (root != null) {
                deque.push(root);
                root = root.left;
            }
            root = deque.pop();
            if (pred != null && root.val < pred.val) {
                node2 = root;
                if (node1 == null) {
                    node1 = pred;
                } else {
                    break;
                }
            }
            pred = root;
            root = root.right;
        }
        int t = node1.val;
        node1.val = node2.val;
        node2.val = t;
    }

    TreeNode node1 = null, node2 = null, pred = null;

    public void recoverTree2(TreeNode root) {
        inorder(root);
        int t = node1.val;
        node1.val = node2.val;
        node2.val = t;

    }

    public void inorder(TreeNode root) {
        if (root == null) {
            return;
        }
        inorder(root.left);
        if (pred != null && root.val < pred.val) {
            node2 = root;
            if (node1 == null) {
                node1 = pred;
            }
        }
        pred = root;
        inorder(root.right);

    }

    public void printTree(TreeNode root) {
        if (root == null) {
            return;
        }
        System.out.print(root.val + " ");
        printTree(root.left);
        printTree(root.right);
    }

    public void morrisInOrder(TreeNode root) {
        TreeNode predecessor = null;
        TreeNode pred = null;
        List<Integer> nums = new ArrayList<>();
        if (root == null) {
            return;
        }
        while (root != null) {
            if (root.left == null) {
                nums.add(root.val);
                root = root.right;
            } else {
                // 拿到左子树中的最大节点
                predecessor = root.left;
                while (predecessor.right != null && predecessor.right != root) {
                    predecessor = predecessor.right;
                }
                // 最大节点的右节点为空
                if (predecessor.right == null) {
                    predecessor.right = root;
                    root = root.left;
                } else {
                    // 最大节点的右节点不为空，则断开连接
                    predecessor.right = null;
                    nums.add(root.val);
                    root = root.right;
                }

            }
        }
        System.out.println(nums);

    }


    public void morris(TreeNode root) {
        TreeNode predecessor = null;
        if (root == null) {
            return;
        }
        while (root != null) {
            if (root.left == null) {
                root = root.right;
            } else {
                predecessor = root.left;
                while (predecessor.right != null && predecessor.right != root) {
                    predecessor = predecessor.right;
                }
                if (predecessor.right == null) {
                    predecessor.right = root;
                    root = root.left;
                } else {
                    predecessor.right = null;
                    root = root.right;
                }
            }
        }
    }


    //preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
//输出: [3,9,20,null,null,15,7]
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            map.put(inorder[i], i);
        }
        return generateSubTree(preorder, 0, preorder.length, inorder, 0, inorder.length, map);

    }

    public TreeNode generateSubTree(int[] preorder, int p_start, int p_end, int[] inorder, int i_start, int i_end, Map<Integer, Integer> map) {
        if (p_start == p_end) {
            return null;
        }
        TreeNode root = new TreeNode(preorder[p_start]);
        int root_pos = map.get(root.val);
        root.left = generateSubTree(preorder, p_start + 1, p_start + root_pos - i_start + 1, inorder, i_start, root_pos, map);
        root.right = generateSubTree(preorder, p_start + root_pos - i_start + 1, p_end, inorder, root_pos + 1, i_end, map);
        return root;
    }

    //输入：inorder = [9,3,15,20,7], postorder = [9,15,7,20,3]
//输出：[3,9,20,null,null,15,7]
    public TreeNode buildTree2(int[] inorder, int[] postorder) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            map.put(inorder[i], i);
        }
        return generateSubTreeByPost(postorder, 0, postorder.length - 1, inorder, 0, inorder.length - 1, map);
    }

    public TreeNode generateSubTreeByPost(int[] postorder, int p_start, int p_end, int[] inorder, int i_start, int i_end, Map<Integer, Integer> map) {
        if (p_start > p_end) {
            return null;
        }
        TreeNode root = new TreeNode(postorder[p_end]);
        int root_pos = map.get(root.val);
        root.left = generateSubTreeByPost(postorder, p_start, p_start + root_pos - i_start - 1, inorder, i_start, root_pos - 1, map);
        root.right = generateSubTreeByPost(postorder, p_start + root_pos - i_start, p_end - 1, inorder, root_pos + 1, i_end, map);
        return root;
    }

    public List<List<Integer>> levelOrderBottom2(TreeNode root) {
        List<List<Integer>> ans = new ArrayList<>();
        if (root == null) {
            return ans;
        }
        Deque<List<Integer>> deque = new LinkedList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            List<TreeNode> list = new ArrayList<>();
            while (!queue.isEmpty()) {
                list.add(queue.poll());
            }
            List<Integer> nums = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).left != null) {
                    queue.add(list.get(i).left);
                }
                if (list.get(i).right != null) {
                    queue.add(list.get(i).right);
                }
                nums.add(list.get(i).val);
            }
            deque.push(nums);
        }
        while (!deque.isEmpty()) {
            ans.add(deque.pop());
        }
        return ans;
    }

    public TreeNode sortedArrayToBST(int[] nums) {
        if (nums == null || nums.length == 0) {
            return null;
        }
        return generateBST(nums, 0, nums.length - 1);
    }

    public TreeNode generateBST(int[] nums, int start, int end) {
        if (start > end) {
            return null;
        }
        int mid = start + ((end - start) >> 1);
        TreeNode root = new TreeNode(nums[mid]);
        root.left = generateBST(nums, start, mid - 1);
        root.right = generateBST(nums, mid + 1, end);
        return root;
    }

    //[-10, -3, 0, 5, 9]
    public TreeNode sortedListToBST(ListNode head) {
        if (head == null) {
            return null;
        }
        ListNode p = head;
        List<ListNode> list = new ArrayList<>();
        while (p != null) {
            list.add(p);
            p = p.next;
        }
        return generateBST(list, 0, list.size() - 1);
    }

    public TreeNode generateBST(List<ListNode> list, int start, int end) {
        if (start > end) {
            return null;
        }
        int mid = start + ((end - start) >> 1);
        TreeNode root = new TreeNode(list.get(mid).val);
        root.left = generateBST(list, start, mid - 1);
        root.right = generateBST(list, mid + 1, end);
        return root;
    }

    public boolean isBalanced(TreeNode root) {
        if (root == null) {
            return true;
        }
        return getDepth(root) != -1;
    }

    int getDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = getDepth(root.left);
        if (left == -1) {
            return left;
        }
        int right = getDepth(root.right);
        if (right == -1) {
            return right;
        }
        return Math.abs(left - right) < 2 ? Math.max(left, right) + 1 : -1;
    }


    public int minDepth2(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return getMinDepth(root);
    }

    int getMinDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = getMinDepth(root.left);
        int right = getMinDepth(root.right);
        if (left == 0) {
            return right + 1;
        }
        if (right == 0) {
            return left + 1;
        }
        return left > right ? right + 1 : left + 1;
    }

    //[5,4,8,11,null,13,4,7,2,null,null,5,1]
    //22
    //[[5,4,11,2],[5,8,4,5]]
    List<List<Integer>> ans = new ArrayList<>();
    LinkedList<Integer> track = new LinkedList<>();

    public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
        if (root == null) {
            return ans;
        }
        track.add(root.val);
        dfs(root, targetSum - root.val);
        return ans;
    }

    public void dfs(TreeNode root, int targetSum) {
        if (targetSum == 0 && root.left == null && root.right == null) {
            ans.add(new ArrayList<>(track));
            return;
        }
        if (root.left != null) {
            track.add(root.left.val);
            dfs(root.left, targetSum - root.left.val);
            track.removeLast();
        }
        if (root.right != null) {
            track.add(root.right.val);
            dfs(root.right, targetSum - root.right.val);
            track.removeLast();
        }
    }

    public void flatten(TreeNode root) {
        if (root == null) {
            return;
        }
        TreeNode predecessor = null;
        while (root != null) {
            if (root.left == null) {
                root = root.right;
            } else {
                predecessor = root.left;
                while (predecessor.right != null) {
                    predecessor = predecessor.right;
                }
                predecessor.right = root.right;
                root.right = root.left;
                root.left = null;
                root = root.right;
            }
        }


    }

    List<Integer> numbers = new ArrayList<>();

    public int sumNumbers(TreeNode root) {
        return dfsNumber(root, 0);
    }

    public int sumNumbers2(TreeNode root) {
        int sum = 0;
        Queue<TreeNode> treeNodes = new LinkedList<>();
        Queue<Integer> queue = new LinkedList<>();
        treeNodes.add(root);
        queue.add(root.val);
        while (!treeNodes.isEmpty()) {
            TreeNode node = treeNodes.poll();
            Integer num = queue.poll();
            if (node.left == null && node.right == null) {
                sum += num;
            } else {

                if (node.left != null) {
                    treeNodes.add(node.left);
                    queue.add(num * 10 + node.left.val);
                }
                if (node.right != null) {
                    treeNodes.add(node.right);
                    queue.add(num * 10 + node.right.val);
                }
            }
        }
        return sum;
    }

    int dfsNumber(TreeNode root, int pre) {
        if (root == null) {
            return 0;
        }
        pre = pre * 10 + root.val;
        if (root.left == null && root.right == null) {
            return pre;
        }

        return dfsNumber(root.left, pre) + dfsNumber(root.right, pre);
    }

    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        preOrder(root, result);
        return result;
    }

    public void preOrder(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }
        result.add(root.val);
        preOrder(root.left, result);
        preOrder(root.right, result);
    }

    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        postOrder(root, result);
        return result;
    }

    public void postOrder(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }
        postOrder(root.left, result);
        postOrder(root.right, result);
        result.add(root.val);
    }


    public int trailingZeroes(int n) {
        int sum = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = i; j % 5 == 0; j /= 5) {
                sum++;
            }
        }
        return sum;
    }

    public int countNodes(TreeNode root) {
        int count = 0;
        if (root == null) {
            return 0;
        }
        TreeNode predecessor = null;
        while (root != null) {
            if (root.left == null) {
                count++;
                root = root.right;
            } else {
                predecessor = root.left;
                while (predecessor.right != null && predecessor.right != root) {
                    predecessor = predecessor.right;
                }
                if (predecessor.right == null) {
                    predecessor.right = root;
                    root = root.left;
                } else {
                    predecessor.right = null;
                    count++;
                    root = root.right;
                }
            }
        }
        return count;
    }

    public boolean hasAlternatingBits(int n) {
        int t;
        t = n % 2;
        n /= 2;
        int pre = t;
        while (n != 0) {
            t = n % 2;
            n /= 2;

            if ((pre ^ t) == 0) {
                return false;
            }
            pre = t;
        }
        return true;

    }

    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node != null) {
                TreeNode temp = null;
                temp = node.left;
                node.left = node.right;
                node.right = temp;
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }
        }
        return root;
    }

    public int kthSmallest(TreeNode root, int k) {
        List<Integer> list = new ArrayList<>();
        dfs(root, list);
        return list.get(k - 1);
    }

    void dfs(TreeNode root, List<Integer> list) {
        if (root == null) {
            return;
        }
        dfs(root.left, list);
        list.add(root.val);
        dfs(root.right, list);
    }

//    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
//        while (root != null) {
//            if (p.val > root.val && q.val > root.val) {
//                root = root.right;
//            } else if (p.val < root.val && q.val < root.val) {
//                root = root.left;
//            } else {
//                return root;
//            }
//        }
//        return null;
//    }

    public List<String> binaryTreePaths(TreeNode root) {
        List<String> paths = new ArrayList<>();
        if (root == null) {
            return paths;
        }
        backtrack(root, paths, "");
        return paths;
    }

    void backtrack(TreeNode root, List<String> paths, String s) {

        StringBuilder sb = new StringBuilder(s);
        if (sb.length() == 0) {
            sb.append(root.val);
        } else {
            sb.append("->").append(root.val);
        }
        s = sb.toString();
        if (root.left == null && root.right == null) {
            paths.add(s);
            return;
        }
        if (root.left != null) {
            backtrack(root.left, paths, s);
        }
        if (root.right != null) {
            backtrack(root.right, paths, s);
        }
    }

    int sumOfLeaves = 0;

    public int sumOfLeftLeaves(TreeNode root) {
        List<TreeNode> nodes = new ArrayList<>();
        inorderLeaves(root);
        return sumOfLeaves;
    }

    void inorderLeaves(TreeNode root) {
        if (root == null) {
            return;
        }
        if (root.left != null && root.left.left == null && root.left.right == null) {
            sumOfLeaves += root.left.val;
        }
        inorderLeaves(root.left);
        inorderLeaves(root.right);
    }

    public boolean isValidSerialization(String preorder) {
        Deque<Integer> deque = new LinkedList<>();
        deque.push(-1);
        String[] strings = preorder.split(",");
        for (int i = 0; i < strings.length; i++) {
            if (deque.size() == 0) {
                return false;
            }
            if (Character.isDigit(strings[i].charAt(0))) {
                deque.pop();
                deque.push(-1);
                deque.push(-1);
            } else {
                deque.pop();
            }
        }
        if (deque.size() != 0) {
            return false;
        }
        return true;
    }

    public boolean isValidSerialization2(String preorder) {
        int k = 1;
        String[] strings = preorder.split(",");
        for (int i = 0; i < strings.length; i++) {
            if (k == 0) {
                return false;
            }
            if (Character.isDigit(strings[i].charAt(0))) {
                k++;
            } else {
                k--;
            }
        }
        if (k != 0) {
            return false;
        }
        return true;
    }

    int maxLength = 0;

    public int diameterOfBinaryTree(TreeNode root) {
        if (root == null) {
            return 0;
        }
        getMaxLength(root);
        return maxLength;
    }

    public int getMaxLength(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int leftLength = getMaxLength(root.left);
        int rightLength = getMaxLength(root.right);
        int diameter = leftLength + rightLength;
        maxLength = Math.max(maxLength, diameter);
        return 1 + Math.max(leftLength, rightLength);
    }

    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null) {
            return null;
        }
        if (root.val > key) {
            root.left = deleteNode(root.left, key);
        } else if (root.val < key) {
            root.right = deleteNode(root.right, key);
        } else {
            if (root.left == null) {
                return root.right;
            }
            if (root.right == null) {
                return root.left;
            }
            TreeNode temp = root.right;
            while (temp.left != null) {
                temp = temp.left;
            }
            temp.left = root.left;
            root = root.right;
        }
        return root;
    }

    List<Integer> answer = new ArrayList<>();
    int base = Integer.MIN_VALUE, maxCount = 0, count = 0;

    public int[] findMode(TreeNode root) {
        findModeInOrder(root);
        int[] ans = new int[answer.size()];
        for (int i = 0; i < answer.size(); i++) {
            ans[i] = answer.get(i);
        }
        return ans;
    }

    void findModeInOrder(TreeNode root) {
        if (root == null) {
            return;
        }
        findModeInOrder(root.left);
        if (base != root.val) {
            base = root.val;
            count = 1;
        } else {
            count++;
        }
        if (count > maxCount) {
            maxCount = count;
            answer.clear();
            answer.add(base);
        } else if (count == maxCount) {
            answer.add(base);
        }
        findModeInOrder(root.right);
    }


    public int findBottomLeftValue(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        TreeNode leftNode = null;
        while (!queue.isEmpty()) {
            int size = queue.size();
            leftNode = queue.element();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }
        }
        return leftNode.val;
    }

    public List<Integer> largestValues(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        if (root == null) {
            return ans;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            int maxValue = Integer.MIN_VALUE;
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node.val > maxValue) {
                    maxValue = node.val;
                }
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }
            ans.add(maxValue);
        }
        return ans;
    }

    public int getMinimumDifference(TreeNode root) {
        int min = Integer.MAX_VALUE;
        int pre = -1;
        TreeNode predecessor = null;
        while (root != null) {
            if (root.left == null) {
                if (pre != -1) {
                    int value = Math.abs(root.val - pre);
                    if (value < min) {
                        min = value;
                    }
                }
                pre = root.val;
                root = root.right;
            } else {
                predecessor = root.left;
                while (predecessor.right != null && predecessor.right != root) {
                    predecessor = predecessor.right;
                }
                if (predecessor.right == null) {
                    predecessor.right = root;
                    root = root.left;
                } else {
                    predecessor.right = null;
                    if (pre != -1) {
                        int value = Math.abs(root.val - pre);
                        if (value < min) {
                            min = value;
                        }
                    }
                    pre = root.val;
                    root = root.right;
                }
            }
        }
        return min;
    }

    int tiltAns = 0;

    public int findTilt(TreeNode root) {
        tilt(root);
        return tiltAns;
    }

    public int tilt(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = tilt(root.left);
        int right = tilt(root.right);
        int tiltValue = Math.abs(left - right);
        tiltAns += tiltValue;
        return root.val + left + right;

    }

    int nodeValue = 0;

    public TreeNode convertBST(TreeNode root) {
        if (root == null) {
            return null;
        }
        convertBSTInorder(root);
        return root;
    }

    public void convertBSTInorder(TreeNode root) {
        if (root == null) {
            return;
        }
        convertBSTInorder(root.right);
        nodeValue += root.val;
        root.val = nodeValue;
        convertBSTInorder(root.left);

    }

    public TreeNode convertBSTByMorris(TreeNode root) {
        if (root == null) {
            return null;
        }
        TreeNode rootTemp = root;
        int sum = 0;
        TreeNode predecessor = null;
        while (root != null) {
            if (root.right == null) {
                sum += root.val;
                root.val = sum;
                root = root.left;
            } else {
                predecessor = root.right;
                while (predecessor.left != null && predecessor.left != root) {
                    predecessor = predecessor.left;
                }
                if (predecessor.left == null) {
                    predecessor.left = root;
                    root = root.right;
                } else {
                    sum += root.val;
                    root.val = sum;
                    predecessor.left = null;
                    root = root.left;
                }
            }
        }
        return rootTemp;
    }

    String s = "";

    public String tree2str(TreeNode root) {
        if (root == null) {
            s += "()";
            return s;
        }
        s += "(" + root.val;
        if (root.right != null) {
            tree2str(root.left);
            tree2str(root.right);
        } else if (root.left != null) {
            tree2str(root.left);
        }
        s += ")";
        return s.substring(1, s.length() - 1);
    }

    public TreeNode mergeTrees(TreeNode root1, TreeNode root2) {
        if (root1 == null) {
            return root2;
        }
        if (root2 == null) {
            return root1;
        }
        Queue<TreeNode> queue1 = new LinkedList<>();
        Queue<TreeNode> queue2 = new LinkedList<>();
        queue1.add(root1);
        queue2.add(root2);
        while (!queue1.isEmpty() || !queue2.isEmpty()) {
            int size = queue1.size();
            for (int i = 0; i < size; i++) {
                TreeNode node1 = queue1.poll();
                TreeNode node2 = queue2.poll();
                node1.val = node1.val + node2.val;
                if (node1.left != null && node2.left != null) {
                    queue1.add(node1.left);
                    queue2.add(node2.left);
                }
                if (node1.right != null && node2.right != null) {
                    queue1.add(node1.right);
                    queue2.add(node2.right);
                }
                if (node1.left == null) {
                    node1.left = node2.left;
                }
                if (node1.right == null) {
                    node1.right = node2.right;
                }
            }
        }
        return root1;

    }

    public TreeNode addOneRow(TreeNode root, int val, int depth) {
        if (depth == 1) {
            TreeNode node = new TreeNode(val);
            node.left = root;
            return node;
        }
        addOneRowDFS(root, val, 1, depth - 1);
        return root;
    }

    public TreeNode addOneRowDFS(TreeNode root, int val, int depth, int targetDepth) {
        if (root == null) {
            return null;
        }
        TreeNode left = addOneRowDFS(root.left, val, depth + 1, targetDepth);
        TreeNode right = addOneRowDFS(root.right, val, depth + 1, targetDepth);
        if (depth == targetDepth) {
            TreeNode node = new TreeNode(val);
            TreeNode node2 = new TreeNode(val);
            node.left = left;
            node2.right = right;
            root.left = node;
            root.right = node2;
        }
        return root;
    }

    public List<Double> averageOfLevels(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        List<Double> ans = new ArrayList<>();
        while (!queue.isEmpty()) {
            int size = queue.size();
            double sum = 0;
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                sum += node.val;
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }
            String doubleString = String.format("%.5f", sum / (size * 1.0));
            ans.add(Double.parseDouble(doubleString));
        }
        return ans;

    }

    Map<String, Integer> map = new HashMap<>();
    List<TreeNode> duplicateSubtrees = new ArrayList<>();

    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        dfs(root);
        return duplicateSubtrees;
    }

    String dfs(TreeNode root) {
        if (root == null) {
            return "#";
        }
        String left = dfs(root.left);
        String right = dfs(root.right);
        String cur = root.val + "," + left + "," + right;
        Integer count = map.getOrDefault(cur, 0);
        if (count == 1) {
            duplicateSubtrees.add(root);
        }
        map.put(cur, count + 1);
        return cur;
    }

    Set<Integer> set = new HashSet<>();

    public boolean findTarget(TreeNode root, int k) {
        return findTargetDFS(root, k);

    }

    boolean findTargetDFS(TreeNode root, int k) {
        if (root == null) {
            return false;
        }
        if (set.contains(k - root.val)) {
            return true;
        }
        set.add(root.val);
        boolean left = findTargetDFS(root.left, k);
        boolean right = findTargetDFS(root.right, k);

        return left || right;
    }

    public TreeNode constructMaximumBinaryTree(int[] nums) {
        return constructMaximumBinaryTreeByDFS(nums, 0, nums.length - 1);
    }

    public TreeNode constructMaximumBinaryTreeByDFS(int[] nums, int start, int end) {
        if (start > end) {
            return null;
        }
        int max = start;
        for (int i = start; i <= end; i++) {
            if (nums[i] > nums[max]) {
                max = i;
            }
        }
        TreeNode root = new TreeNode(nums[max]);
        TreeNode left = constructMaximumBinaryTreeByDFS(nums, start, max - 1);
        TreeNode right = constructMaximumBinaryTreeByDFS(nums, max + 1, end);
        root.left = left;
        root.right = right;
        return root;

    }

    public List<List<String>> printTree2(TreeNode root) {
        int maxDepth = getTreeDepthDFS(root);
        int col = (1 << maxDepth) - 1;
        int mid = col >> 1;
        String[][] ans = new String[maxDepth][col];
        for (int i = 0; i < ans.length; i++) {
            Arrays.fill(ans[i], "");
        }
        printTree(root, ans, 0, 0, col);
        List<List<String>> list = new ArrayList<>();
        for (int i = 0; i < ans.length; i++) {
            list.add(Arrays.asList(ans[i]));
        }
        return list;
    }

    void printTree(TreeNode root, String[][] ans, int depth, int start, int end) {
        if (root == null) {
            return;
        }
        int mid = start + ((end - start) >> 1);
        ans[depth][mid] = String.valueOf(root.val);
        printTree(root.left, ans, depth + 1, start, mid - 1);
        printTree(root.right, ans, depth + 1, mid + 1, end);

    }

    int getTreeDepthDFS(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = getTreeDepthDFS(root.left);
        int right = getTreeDepthDFS(root.right);
        int maxDepth = Math.max(left, right);
        return maxDepth + 1;
    }

    public int widthOfBinaryTree(TreeNode root) {
        if (root == null) {
            return 0;
        }
        LinkedList<TreeNode> queue = new LinkedList<>();
        int max = 0;
        root.val = 1;
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            max = Math.max(max, queue.getLast().val - queue.getFirst().val);
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) {
                    node.left.val = node.val * 2;
                    queue.add(node.left);
                }
                if (node.right != null) {
                    node.right.val = node.val * 2 + 1;
                    queue.add(node.right);
                }
            }

        }
        return max + 1;
    }

    class Item {
        int key;
        int value;

        Item(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    public boolean canReorderDoubled(int[] arr) {
        int count = arr.length / 2;
        PriorityQueue<Item> priorityQueue = new PriorityQueue<>((x, y) -> Math.abs(x.value) - Math.abs(y.value));
        for (int i = 0; i < arr.length; i++) {
            priorityQueue.add(new Item(i, arr[i]));
        }
        boolean[] visited = new boolean[arr.length];
        Arrays.fill(visited, false);
        while (!priorityQueue.isEmpty()) {
            Item item = priorityQueue.poll();
            if (!visited[item.key]) {
                int index = arrIndexOf(arr, item.value * 2, visited, item.key);
                if (index == -1) {
                    continue;
                }
                visited[index] = true;
                count--;
                if (count == 0) {
                    return true;
                }
            }
        }
        return count == 0;
    }

    public int arrIndexOf(int[] arr, int num, boolean[] visited, int oldIndex) {
        for (int i = 0; i < arr.length; i++) {
            if (!visited[i] && arr[i] == num && oldIndex != i) {
                return i;
            }
        }
        return -1;
    }

    public TreeNode trimBST(TreeNode root, int low, int high) {
        if (root == null) {
            return null;
        }
        if (root.val < low) {
            return trimBST(root.right, low, high);
        }
        if (root.val > high) {
            return trimBST(root.left, low, high);
        }
        root.left = trimBST(root.left, low, high);
        root.right = trimBST(root.right, low, high);
        return root;
    }

    int secondMinValue = -1;
    int rootValue;
    public int findSecondMinimumValue(TreeNode root) {
        rootValue = root.val;
        findSecondMinValue(root);
        return secondMinValue;
    }

    void findSecondMinValue(TreeNode root) {
        if (root == null) {
            return;
        }
        if (secondMinValue != -1 && root.val >= secondMinValue) {
            return;
        }
        if (root.val > rootValue) {
            secondMinValue = root.val;
        }
        findSecondMinValue(root.left);
        findSecondMinValue(root.right);
    }
//    public int longestUnivaluePath(TreeNode root) {
//        if (root == null) {
//            return 0;
//        }
//        Queue<TreeNode> queue = new LinkedList<>();
//        queue.add(root);
//        while (!queue.isEmpty()) {
//            TreeNode node = queue.poll();
//            if (node.left != null) {
//                queue.add(node.left);
//            }
//            if (node.right != null) {
//                queue.add(node.right);
//            }
//        }
//        return 0;
//    }
    public TreeNode searchBST(TreeNode root, int val) {
        if (root == null) {
            return null;
        }
        if (root.val == val) {
            return root;
        } else if (root.val > val) {
            TreeNode node = searchBST(root.left, val);
            return node;
        } else {
            TreeNode node = searchBST(root.right, val);
            return node;
        }
    }
    public TreeNode insertIntoBST(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }
        insertIntoBSTDFS(root, val);
        return root;
    }

    public TreeNode insertIntoBST2(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }
        TreeNode predecessor = null;
        TreeNode p = null;
        while (root != null) {
            if (root.left == null) {
                if (p != null && root.val > val && p.val < val) {

                }
                p = root;
                root = root.right;
            } else {
                predecessor = root.left;
                while (predecessor.right != null && predecessor.right != root) {
                    predecessor = predecessor.right;
                }
                if (predecessor.right == null) {
                    predecessor.right = root;
                    root = root.left;
                } else {
                    predecessor.right = null;
                    root = root.right;
                }
            }
        }
        return root;
    }
    void insertIntoBSTDFS(TreeNode root, int secondMinValue) {
        if (root == null) {
            return;
        }
        if (secondMinValue > root.val) {
            if (root.right == null) {
                root.right = new TreeNode(secondMinValue);
                return;
            } else {
                insertIntoBSTDFS(root.right, secondMinValue);
            }
        }
        if (secondMinValue < root.val) {
            if (root.left == null) {
                root.left = new TreeNode(secondMinValue);
            } else {
                insertIntoBSTDFS(root.left, secondMinValue);
            }
        }
    }

    public List<Integer> findMinHeightTrees(int n, int[][] edges) {
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < edges.length; i++) {
            List<Integer> arr = map.getOrDefault(edges[i][0], new ArrayList<>());
            arr.add(edges[i][1]);
            List<Integer> arr2 = map.getOrDefault(edges[i][1], new ArrayList<>());
            arr2.add(edges[i][0]);
        }
        return null;
    }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        return lowestCommonAncestorDFS(root, p, q);
    }

    public TreeNode lowestCommonAncestorDFS(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root.val == p.val || root.val == q.val) {
            return root;
        }
        TreeNode left = lowestCommonAncestorDFS(root.left, p, q);
        TreeNode right = lowestCommonAncestorDFS(root.right, p, q);
        if (left != null && right != null) {
            return root;
        }
        if (left == null && right == null) {
            return null;
        }
        return left == null ? right : left;
    }
    Map<Integer, Integer> treeSumMap = new HashMap<>();
    public int[] findFrequentTreeSum(TreeNode root) {
        if (root == null) {
            return new int[0];
        }
        findFrequentTreeSumDFS(root);
        List<Map.Entry<Integer, Integer>> list = treeSumMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        Collections.reverse(list);
        Integer value = list.get(0).getValue();
        List<Integer> collect = list.stream().filter(item -> item.getValue().equals(value)).map(Map.Entry::getKey).collect(Collectors.toList());
        int[] ans = new int[collect.size()];
        for (int i = 0; i < collect.size(); i++) {
            ans[i] = collect.get(i);
        }
        return ans;
    }

    public int findFrequentTreeSumDFS(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = findFrequentTreeSumDFS(root.left);
        int right = findFrequentTreeSumDFS(root.right);
        int sum = left + right + root.val;
        treeSumMap.put(sum, treeSumMap.getOrDefault(sum, 0) + 1);
        return sum;
    }

    int longestUnivaluePath = 0;
    public int longestUnivaluePath(TreeNode root) {
        longestUnivaluePathDFS(root);
        return longestUnivaluePath;
    }

    public int longestUnivaluePathDFS(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = longestUnivaluePathDFS(root.left);
        int right = longestUnivaluePathDFS(root.right);
        if (root.left != null && root.left.val == root.val && root.right != null && root.right.val == root.val) {
            longestUnivaluePath = Math.max(longestUnivaluePath, left + right + 2);
        }
        int max = 0;
        if (root.left != null && root.left.val == root.val) {
            max = left + 1;
        }

        if (root.right != null && root.right.val == root.val) {
            max = Math.max(max, right + 1);
            longestUnivaluePath = Math.max(longestUnivaluePath, max);
        }
        return max;

    }
    public static void main(String[] args) {
        MyTree myTree = new MyTree();
        TreeNode node1 = new TreeNode(1);
        TreeNode node2 = new TreeNode(2);
        TreeNode node3 = new TreeNode(3);
        TreeNode node4 = new TreeNode(4);
        node1.left = node2;
        node1.right = node3;
        node3.right = node4;
//        myTree.printTree2(node1);
        System.out.println(myTree.canReorderDoubled(new int[]{2, 4, 0, 0, 8, 1}));
        System.out.println(myTree.canReorderDoubled(new int[]{4, -2, 2, -4}));
        System.out.println(myTree.canReorderDoubled(new int[]{3, 1, 3, 6}));
        System.out.println(myTree.canReorderDoubled(new int[]{0, 0, 0, 0, 0, 0}));
    }


}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Node {
    public int val;
    public Node left;
    public Node right;
    public Node next;

    public Node() {
    }

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, Node _left, Node _right, Node _next) {
        val = _val;
        left = _left;
        right = _right;
        next = _next;
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}

class BSTIterator {

    Queue<TreeNode> queue = new LinkedList<>();

    public BSTIterator(TreeNode root) {
        TreeNode p = root;
        TreeNode predecessor = null;
        while (p != null) {
            if (p.left == null) {
                queue.add(p);
                p = p.right;
            } else {
                predecessor = p.left;
                while (predecessor.right != null && predecessor.right != p) {
                    predecessor = predecessor.right;
                }
                if (predecessor.right == null) {
                    predecessor.right = p;
                    p = p.left;
                } else {
                    predecessor.right = null;
                    queue.add(p);
                    p = p.right;
                }
            }

        }
    }

    public int next() {
        TreeNode node = queue.poll();
        if (node == null) {
            return -1;
        } else {
            return node.val;
        }
    }

    public boolean hasNext() {
        return queue.size() > 0;
    }
}