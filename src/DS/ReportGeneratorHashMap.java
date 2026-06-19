package DS;

public class ReportGeneratorHashMap {
    private ReportEntry[] table;
    private int size;
    private int capacity;
    private final double LOAD_FACTOR = 0.75;

    public class ReportEntry {
        String reportKey;
        String reportData;
        long timestamp;
        int accessCount;
        ReportEntry next;

        public ReportEntry(String key, String data) {
            reportKey = key;
            reportData = data;
            timestamp = System.currentTimeMillis();
            accessCount = 1;
            next = null;
        }
    }

    public ReportGeneratorHashMap(int cap) {
        capacity = cap;
        table = new ReportEntry[capacity];
        size = 0;
    }

    private int hash(String key) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * 31 + key.charAt(i)) % capacity;
        }
        return Math.abs(hash);
    }

    public void putReport(String key, String data) {
        if (size >= capacity * LOAD_FACTOR) {
            resize();
        }

        int index = hash(key);
        ReportEntry newEntry = new ReportEntry(key, data);

        if (table[index] == null) {
            table[index] = newEntry;
            size++;
        } else {
            ReportEntry current = table[index];
            while (current != null) {
                if (current.reportKey.equals(key)) {
                    // Update existing entry
                    current.reportData = data;
                    current.timestamp = System.currentTimeMillis();
                    current.accessCount++;
                    return;
                }
                if (current.next == null) {
                    current.next = newEntry;
                    size++;
                    break;
                }
                current = current.next;
            }
        }
    }

    public String getReport(String key) {
        int index = hash(key);
        ReportEntry current = table[index];

        while (current != null) {
            if (current.reportKey.equals(key)) {
                current.accessCount++;
                return current.reportData;
            }
            current = current.next;
        }
        return null;
    }

    private void resize() {
        ReportEntry[] oldTable = table;
        int oldCapacity = capacity;
        capacity = capacity * 2;
        table = new ReportEntry[capacity];
        size = 0;

        for (int i = 0; i < oldCapacity; i++) {
            ReportEntry current = oldTable[i];
            while (current != null) {
                putReport(current.reportKey, current.reportData);
                current = current.next;
            }
        }
    }
    public void displayCacheStats() {
    System.out.println("=== Report Cache Statistics ===");
    System.out.println("Cache Size: " + size);
    System.out.println("Cache Capacity: " + capacity);
    System.out.printf("Load Factor: %.2f\n", (double)size/capacity);
    
    int totalAccess = 0;
    int chains = 0;
    int maxChainLength = 0;
    
    for (int i = 0; i < capacity; i++) {
        ReportEntry current = table[i];
        if (current != null) {
            chains++;
            int chainLength = 0;
            while (current != null) {
                totalAccess += current.accessCount;
                chainLength++;
                current = current.next;
            }
            maxChainLength = Math.max(maxChainLength, chainLength);
        }
    }
    
    System.out.println("Total Accesses: " + totalAccess);
    System.out.println("Number of Chains: " + chains);
    System.out.println("Max Chain Length: " + maxChainLength);
    System.out.println("Average Chain Length: " + (chains > 0 ? (double)size/chains : 0));
}

public void clearCache() {
    table = new ReportEntry[capacity];
    size = 0;
    System.out.println("Cache cleared successfully.");
}

}