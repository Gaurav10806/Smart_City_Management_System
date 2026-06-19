package DS;

public class SystemMetricsGraph {
    private GraphNode[] vertices;
    private int[][] adjacencyMatrix;
    private int vertexCount;
    private int maxVertices;

    public class GraphNode {
        String area;
        String category;
        int complaintCount;
        double resolutionRate;

        public GraphNode(String a, String c, int count, double rate) {
            area = a;
            category = c;
            complaintCount = count;
            resolutionRate = rate;
        }
    }

    public SystemMetricsGraph(int max) {
        maxVertices = max;
        vertices = new GraphNode[maxVertices];
        adjacencyMatrix = new int[maxVertices][maxVertices];
        vertexCount = 0;
    }

    public boolean addVertex(String area, String category, int count, double rate) {
        if (vertexCount >= maxVertices) return false;
        vertices[vertexCount] = new GraphNode(area, category, count, rate);
        vertexCount++;
        return true;
    }

    public void addEdge(int from, int to, int weight) {
        if (from < vertexCount && to < vertexCount) {
            adjacencyMatrix[from][to] = weight;
        }
    }

    public void displaySystemOverview() {
        System.out.println("=== System Metrics Graph Overview ===");
        for (int i = 0; i < vertexCount; i++) {
            GraphNode node = vertices[i];
            System.out.printf("Node %d: %s-%s | Complaints: %d | Resolution: %.2f%%\n",
                    i, node.area, node.category, node.complaintCount, node.resolutionRate);
        }
    }

    public void findCriticalAreas() {
        System.out.println("=== Critical Areas Analysis ===");
        double avgResolution = 0;
        int totalComplaints = 0;

        // Calculate averages
        for (int i = 0; i < vertexCount; i++) {
            avgResolution += vertices[i].resolutionRate;
            totalComplaints += vertices[i].complaintCount;
        }
        if (vertexCount > 0) {
            avgResolution /= vertexCount;
        }

        System.out.printf("System Average Resolution Rate: %.2f%%\n", avgResolution);
        System.out.printf("Total System Complaints: %d\n", totalComplaints);


        for (int i = 0; i < vertexCount; i++) {
            GraphNode node = vertices[i];
            if (node.resolutionRate < avgResolution && node.complaintCount > 5) {
                System.out.printf("  %s-%s: %.2f%% resolution, %d complaints\n",
                        node.area, node.category, node.resolutionRate, node.complaintCount);
            }
        }
    }
}