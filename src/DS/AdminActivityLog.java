package DS;

public class AdminActivityLog {
    private ActivityNode head;
    private int size, maxSize;

    public class ActivityNode {
        String action;
        String description;
        String timestamp;
        ActivityNode next;

        public ActivityNode(String action, String desc, String time) {
            this.action = action;
            this.description = desc;
            this.timestamp = time;
            this.next = null;
        }
    }

    public AdminActivityLog(int max) {
        this.head = null;
        this.size = 0;
        this.maxSize = max;
    }

    public void logActivity(String action, String description) {
        ActivityNode newNode = new ActivityNode(action, description, new java.util.Date().toString());
        
        // Add to front of linked list
        newNode.next = head;
        head = newNode;
        
        // Maintain max size by removing oldest entries
        if (size >= maxSize) {
            // Find second to last node
            ActivityNode current = head;
            for (int i = 0; i < maxSize - 2 && current.next != null; i++) {
                current = current.next;
            }
            if (current.next != null) {
                current.next = null; // Remove last node
            }
        } else {
            size++;
        }
    }

    public void displayRecentActivity(int count) {
        System.out.println("=== Recent Admin Activity ===");
        if (head == null) {
            System.out.println("No activity recorded.");
            return;
        }
        
        ActivityNode current = head;
        int displayed = 0;
        
        while (current != null && displayed < count) {
            System.out.println(current.timestamp + " - " + current.action + ": " + current.description);
            current = current.next;
            displayed++;
        }
        
        if (displayed == 0) {
            System.out.println("No activities to display.");
        }
    }

    public void displayAllActivity() {
        System.out.println("=== All Admin Activity ===");
        if (head == null) {
            System.out.println("No activity recorded.");
            return;
        }
        
        ActivityNode current = head;
        int count = 1;
        
        while (current != null) {
            System.out.println(count + ". " + current.timestamp + " - " + current.action + ": " + current.description);
            current = current.next;
            count++;
        }
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void clearLog() {
        head = null;
        size = 0;
        System.out.println("Activity log cleared.");
    }
}
