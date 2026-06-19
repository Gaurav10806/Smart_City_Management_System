package DS;

public class WorkloadHashTable {
    private WorkloadEntry[] table;
    private int capacity;

    public class WorkloadEntry {
        String username;
        int activeComplaints;
        int resolvedComplaints;
        WorkloadEntry next;

        public WorkloadEntry(String username, int active, int resolved) {
            this.username = username;
            this.activeComplaints = active;
            this.resolvedComplaints = resolved;
            this.next = null;
        }
    }

    public WorkloadHashTable(int cap) {
        this.capacity = cap;
        table = new WorkloadEntry[capacity];
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    public void put(String username, int active, int resolved) {
        int index = hash(username);
        WorkloadEntry newEntry = new WorkloadEntry(username, active, resolved);

        if (table[index] == null) {
            table[index] = newEntry;
        } else {
            WorkloadEntry current = table[index];
            while (current != null) {
                if (current.username.equals(username)) {
                    current.activeComplaints = active;
                    current.resolvedComplaints = resolved;
                    return;
                }
                if (current.next == null) break;
                current = current.next;
            }
            current.next = newEntry;
        }
    }

    public WorkloadEntry get(String username) {
        int index = hash(username);
        WorkloadEntry current = table[index];

        while (current != null) {
            if (current.username.equals(username)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    public void displayWorkloadStats() {
        System.out.println("=== Workload Statistics ===");
        for (int i = 0; i < capacity; i++) {
            WorkloadEntry current = table[i];
            while (current != null) {
                System.out.println("Officer: " + current.username +
                                 ", Active: " + current.activeComplaints +
                                 ", Resolved: " + current.resolvedComplaints);
                current = current.next;
            }
        }
    }

    // Add displayStats method to match Officer.java calls
    public void displayStats() {
        displayWorkloadStats();
    }
}