package DS;

public class ComplaintBinaryTree {
    private TreeNode root;

    public class TreeNode {
        public int complaintId;
        public String description;
        public String status;
        public String priority;
        TreeNode left, right;

        public TreeNode(int id, String desc, String stat, String prio) {
            complaintId = id;
            description = desc;
            status = stat;
            priority = prio;
            left = right = null;
        }
    }

    public ComplaintBinaryTree() {
        root = null;
    }

    public void insert(int id, String desc, String status, String priority) {
        root = insertRec(root, id, desc, status, priority);
    }

    private TreeNode insertRec(TreeNode root, int id, String desc, String status, String priority) {
        if (root == null) {
            return new TreeNode(id, desc, status, priority);
        }

        if (id < root.complaintId) {
            root.left = insertRec(root.left, id, desc, status, priority);
        } else if (id > root.complaintId) {
            root.right = insertRec(root.right, id, desc, status, priority);
        }

        return root;
    }

    public TreeNode search(int id) {
        return searchRec(root, id);
    }

    private TreeNode searchRec(TreeNode root, int id) {
        if (root == null || root.complaintId == id) {
            return root;
        }

        if (id < root.complaintId) {
            return searchRec(root.left, id);
        }

        return searchRec(root.right, id);
    }

    public void inorderTraversal() {
        System.out.println("=== Complaints (Sorted by ID) ===");
        inorderRec(root);
    }

    private void inorderRec(TreeNode root) {
        if (root != null) {
            inorderRec(root.left);
            System.out.println("ID: " + root.complaintId +
                    " | Status: " + root.status +
                    " | Priority: " + root.priority);
            inorderRec(root.right);
        }
    }

    public void updateStatus(int id, String newStatus) {
        TreeNode node = search(id);
        if (node != null) {
            node.status = newStatus;
            System.out.println("Status updated in tree structure.");
        }
    }
}