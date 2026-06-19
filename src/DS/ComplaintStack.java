package DS;

public class ComplaintStack {
    private ComplaintHistoryNode top;
    private int size, maxSize;

    public class ComplaintHistoryNode {
        int complaintId;
        String status;
        String lastUpdated;
        ComplaintHistoryNode next;

        public ComplaintHistoryNode(int id, String stat, String time) {
            complaintId = id;
            status = stat;
            lastUpdated = time;
            next = null;
        }
    }

    public ComplaintStack(int max) {
        top = null;
        size = 0;
        maxSize = max;
    }

    public boolean push(int id, String status, String time) {
        if (size >= maxSize) return false;
        ComplaintHistoryNode newNode = new ComplaintHistoryNode(id, status, time);
        newNode.next = top;
        top = newNode;
        size++;
        return true;
    }

    public ComplaintHistoryNode pop() {
        if (top == null) return null;
        ComplaintHistoryNode popped = top;
        top = top.next;
        size--;
        return popped;
    }

    public void displayHistory() {
        ComplaintHistoryNode current = top;
        System.out.println("=== Recent Complaint History ===");
        while (current != null) {
            System.out.println("ID: " + current.complaintId +
                    " | Status: " + current.status +
                    " | Updated: " + current.lastUpdated);
            current = current.next;
        }
    }
}