package DS;

public class OfficerLinkedList {
    private OfficerNode head;
    private int size;

    public class OfficerNode {
    String username;
    String name;
    String email;
    String phone;
    String department;
    int workload; // Added workload field
    OfficerNode next;



    // Additional constructor with workload
    public OfficerNode(String username, String area, String category, int workload) {
        this.username = username;
        this.name = "N/A";
        this.email = "N/A";
        this.phone = "N/A";
        this.department = area + "-" + category;
        this.workload = workload;
        this.next = null;
    }
}

// Updated addOfficer method that matches the calls in your Java code
public void addOfficer(String username, String area, String category, int workload) {
    OfficerNode newNode = new OfficerNode(username, area, category, workload);
    newNode.next = head;
    head = newNode;
    size++;
}

    public OfficerLinkedList() {
        head = null;
        size = 0;
    }


    public OfficerNode findOfficer(String username) {
        OfficerNode current = head;
        while (current != null) {
            if (current.username.equals(username)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    public void displayOfficers() {
        System.out.println("=== Available Officers ===");
        OfficerNode current = head;
        while (current != null) {
            System.out.printf("%-15s %-20s %-25s %-15s %-15s\n",
                    current.username, current.name, current.email, current.phone, current.department);
            current = current.next;
        }
    }

    public int getSize() { return size; }
    public OfficerNode findOfficerWithLeastWorkload(String area, String category) {
    OfficerNode current = head;
    OfficerNode bestOfficer = null;
    int minWorkload = Integer.MAX_VALUE;

    while (current != null) {
        if (current.department.contains(area) && current.department.contains(category)) {
            if (current.workload < minWorkload) {
                minWorkload = current.workload;
                bestOfficer = current;
            }
        }
        current = current.next;
    }

    return bestOfficer;
}

public void updateOfficerWorkload(String username, int newWorkload) {
    OfficerNode current = head;
    while (current != null) {
        if (current.username.equals(username)) {
            current.workload = newWorkload;
            System.out.println("Updated workload for " + username + " to " + newWorkload);
            return;
        }
        current = current.next;
    }
    System.out.println("Officer " + username + " not found.");
}
}