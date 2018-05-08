package pkgMain;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Node {

	
	final int name;
	int distance = Integer.MAX_VALUE;
	boolean visited = false;
	Map<Node, Edge> adjacent_nodes = new HashMap<>();
	LinkedList<Node> shortest_path = new LinkedList<>();
	Node parent;
	int jumps = 0;
	
	
	public Node(int n) {
		name = n;
	}
	
	public void add_edge(Node destination, Edge e) {
		adjacent_nodes.put(destination, e);
	}
	
	public void add_edge_u(Node destination, Edge e) {
		adjacent_nodes.put(destination, e);
		destination.adjacent_nodes.put(this, e);
	}
	
}
