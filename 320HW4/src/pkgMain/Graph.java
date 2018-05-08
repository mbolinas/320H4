package pkgMain;

import java.util.LinkedList;

public class Graph {
	LinkedList<Node> nodes = new LinkedList<>();
	
	public Graph() {
		
	}
	
	public void add(Node n) {
		nodes.add(n);
	}
}
