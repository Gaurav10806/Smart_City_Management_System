package DS;

public class ComplaintQueue {
    private ComplaintNode[] queue;
    private int front, rear, size, capacity;

    public class ComplaintNode {
        public int complaintId;
        public String description;
        public String priority;
        String timestamp;

        public ComplaintNode(int id, String desc, String prio, String time) {
            complaintId = id;
            description = desc;
            priority = prio;
            timestamp = time;
        }
    }

    public ComplaintQueue(int cap) {
        capacity = cap;
        queue = new ComplaintNode[capacity];
        front = rear = size = 0;
    }

    public boolean enqueue(int id, String desc, String priority, String time) {
        if (size >= capacity) return false;
        queue[rear] = new ComplaintNode(id, desc, priority, time);
        rear = (rear + 1) % capacity;
        size++;
        return true;
    }

    public ComplaintNode dequeue() {
        if (size == 0) return null;
        ComplaintNode node = queue[front];
        queue[front] = null;
        front = (front + 1) % capacity;
        size--;
        return node;
    }

    public boolean isEmpty() { return size == 0; }
    public int getSize() { return size; }
}