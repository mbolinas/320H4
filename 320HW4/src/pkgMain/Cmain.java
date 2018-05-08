package pkgMain;

import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;

public class Cmain {
	public static void main(String[] args) {

		Edge e = new Edge(1);
		Node n1 = new Node(1);
		Node n2 = new Node(2);
		Node n3 = new Node(3);
		Node n4 = new Node(4);
		Node n5 = new Node(5);
		Node n6 = new Node(6);
		Node n7 = new Node(7);
		Node n8 = new Node(8);
		Node n9 = new Node(9);
		Node n10 = new Node(10);
		Node n11 = new Node(11);
		
		n1.add_edge_u(n4, e);
		n1.add_edge_u(n6, e);
		n2.add_edge_u(n5, e);
		n6.add_edge_u(n2, e);
		n3.add_edge_u(n7, e);
		n6.add_edge_u(n7, e);
		n3.add_edge_u(n1, e);
		n7.add_edge_u(n8, e);
		n7.add_edge_u(n9, e);
		n10.add_edge_u(n3, e);
		n11.add_edge_u(n6, e);
		
		Graph g = new Graph();
		g.add(n1);
		g.add(n2);
		g.add(n3);
		g.add(n4);
		g.add(n5);
		g.add(n6);
		g.add(n7);
		g.add(n8);
		g.add(n9);
		g.add(n10);
		g.add(n11);
		
		LinkedList<Node> nodes = part4(5, g);
		
		for(Node n : nodes) {
			System.out.println(n.name);
		}
	}
	
	
	
	public static void reset(Node n) {
		Queue<Node> queue = new LinkedList<>();
		queue.add(n);
		
		while(queue.size() > 0) {
			Node current_node = queue.poll();
			
			for(Entry <Node, Edge> adjacency_pair : current_node.adjacent_nodes.entrySet()) {
				if(adjacency_pair.getKey().visited == true) {
					adjacency_pair.getKey().visited = false;
					queue.add(adjacency_pair.getKey());
				}
			}
		}
	}
	
	public static void part1(Node host) {
		int count = 0;
		double total_weight = 0;
		host.distance = 0;
		host.visited = true;
		
		Queue<Node> queue = new LinkedList<>();
		queue.add(host);
		
		while(queue.size() > 0) {
			count++;
			Node current_node = queue.poll();
			total_weight = total_weight + current_node.distance;
			//System.out.println(current_node.name);
			
			for(Entry <Node, Edge> adjacency_pair : current_node.adjacent_nodes.entrySet()) {
				if(adjacency_pair.getKey().visited == false) {
					adjacency_pair.getKey().visited = true;
					adjacency_pair.getKey().distance = current_node.distance + 1;
					queue.add(adjacency_pair.getKey());
				}
			}
		}
		System.out.println(total_weight);
		System.out.println(count);
		count--;
		System.out.println("Part 1: average awkwardness from host '" + host.name + "' = " + total_weight/count);
	}
	
	public static void part2(Node[] hosts) {
		int count = 0;
		int total_weight = 0;
		for(Node host : hosts) {
			reset(host);
			
			if(host.distance < Integer.MAX_VALUE) {
				//total_weight = total_weight - host.distance;
			}
			host.distance = 0;
			host.visited = true;
			
			Queue<Node> queue = new LinkedList<>();
			queue.add(host);
			
			while(queue.size() > 0) {
				count++;
				Node current_node = queue.poll();
				//total_weight = total_weight + current_node.distance;
				//System.out.println("current node " + current_node.name + " node weight " + current_node.distance);
				//System.out.println("total weight " + total_weight);
				
				for(Entry <Node, Edge> adjacency_pair : current_node.adjacent_nodes.entrySet()) {
					if(adjacency_pair.getKey().visited == false) {
						adjacency_pair.getKey().visited = true;
						if(current_node.distance + 1 < adjacency_pair.getKey().distance) {
							//System.out.println("changing weight from " + adjacency_pair.getKey().distance + " to " + (current_node.distance + 1));
							if(adjacency_pair.getKey().distance < Integer.MAX_VALUE) {
								//total_weight = total_weight - (adjacency_pair.getKey().distance - (current_node.distance + 1));
							}
							adjacency_pair.getKey().distance = current_node.distance + 1;
						}
						adjacency_pair.getKey().distance = Math.min(current_node.distance + 1, adjacency_pair.getKey().distance);
						queue.add(adjacency_pair.getKey());
					}
				}
			}
			
		}
		awkwardness(hosts[0], hosts.length);
		
		//System.out.println("count = " + count);
		//System.out.println("total weight = " + total_weight);
	}
	
	public static void awkwardness(Node host, int size) {
		int count = 0;
		double total_weight = 0;
		host.distance = 0;
		host.visited = true;
		
		Queue<Node> queue = new LinkedList<>();
		queue.add(host);
		
		while(queue.size() > 0) {
			count++;
			Node current_node = queue.poll();
			total_weight = total_weight + current_node.distance;
			//System.out.println(current_node.name);
			
			for(Entry <Node, Edge> adjacency_pair : current_node.adjacent_nodes.entrySet()) {
				if(adjacency_pair.getKey().visited == true) {
					adjacency_pair.getKey().visited = false;
					//adjacency_pair.getKey().distance = current_node.distance + 1;
					queue.add(adjacency_pair.getKey());
				}
			}
		}
		count--;
		count = count - size;
		System.out.println("total weight = " + total_weight);
		System.out.println("answer = " + total_weight / count);
	}

	public static void part3(int hosts, Graph g) {
//		LinkedList<Node> lists = heuristic1(hosts, g);
//		for(Node n : lists) {
//			System.out.println(n.name);
//		}
		LinkedList<Node> l = heuristic2(hosts, g);
		for(Node n : l) {
			System.out.println(n.name);
		}
	}
	
	
	public static LinkedList<Node> heuristic1(int hosts, Graph g) {
		LinkedList<Node> list = new LinkedList<>();
		Node current_node = null;
		while(hosts > 0) {
			for(Node n : g.nodes) {
				if(current_node == null || n.adjacent_nodes.size() >= current_node.adjacent_nodes.size()) {
					if(current_node == null && n.visited == false) {
						current_node = n;
					}
					else if(n.visited == false) {
						if(n.adjacent_nodes.size() == current_node.adjacent_nodes.size()) {
							if(n.name < current_node.name) {
								current_node = n;
							}
						}
						else {
							current_node = n;
						}
					}
				}
			}
			hosts--;
			list.add(current_node);
			current_node.visited = true;
			current_node = null;
		}
		return list;
	}
	
	public static LinkedList<Node> heuristic2(int hosts, Graph g) {
		Node host = heuristic1(1, g).getFirst();
		hosts--;
		host.visited = false;
		
		LinkedList<Node> list = new LinkedList<>();
		list.add(host);
		
		
		while(hosts > 0) {
			Node potential_host = host;
			host.distance = 0;
			host.visited = true;
			
			Queue<Node> queue = new LinkedList<>();
			queue.add(host);
			
			while(queue.size() > 0) {
				Node current_node = queue.poll();
				//System.out.println(current_node.name);
				
				for(Entry <Node, Edge> adjacency_pair : current_node.adjacent_nodes.entrySet()) {
					if(adjacency_pair.getKey().visited == false) {
						adjacency_pair.getKey().visited = true;
						if(current_node.distance + 1 < adjacency_pair.getKey().distance) {
							//System.out.println("changing weight from " + adjacency_pair.getKey().distance + " to " + (current_node.distance + 1));
							if(adjacency_pair.getKey().distance < Integer.MAX_VALUE) {
								//total_weight = total_weight - (adjacency_pair.getKey().distance - (current_node.distance + 1));
							}
							adjacency_pair.getKey().distance = current_node.distance + 1;
						}
						queue.add(adjacency_pair.getKey());
					}
				}
				if(current_node.distance > potential_host.distance) {
					potential_host = current_node;
				}
				if(current_node.distance == potential_host.distance) {
					if(potential_host.name > current_node.name) {
						potential_host = current_node;
					}
				}
			}
			
			list.add(potential_host);
			hosts--;
			host = potential_host;
			reset(host);
		}
		
		return list;
	}
	
	
	public static LinkedList<Node> part4(int hosts, Graph g) {
		Node host = heuristic1(1, g).getFirst();
		hosts--;
		host.visited = false;
		
		LinkedList<Node> list = new LinkedList<>();
		list.add(host);
		
		
		while(hosts > 0) {
			Node potential_host = host;
			//Stack<Node> stack = new Stack<>();
			//stack.push(potential_host);
			host.distance = 0;
			host.visited = true;
			host.jumps = 0;
			
			Queue<Node> queue = new LinkedList<>();
			queue.add(host);
			
			while(queue.size() > 0) {
				Node current_node = queue.poll();
				//System.out.println(current_node.name);
				
				for(Entry <Node, Edge> adjacency_pair : current_node.adjacent_nodes.entrySet()) {
					if(adjacency_pair.getKey().visited == false) {
						adjacency_pair.getKey().visited = true;
						if(current_node.distance + 1 < adjacency_pair.getKey().distance) {
							adjacency_pair.getKey().shortest_path = current_node.shortest_path;
							adjacency_pair.getKey().shortest_path.add(current_node);
							adjacency_pair.getKey().parent = current_node;
							adjacency_pair.getKey().jumps = current_node.jumps + 1;
							//System.out.println(adjacency_pair.getKey().name + " size: " + adjacency_pair.getKey().shortest_path.size());
							adjacency_pair.getKey().distance = current_node.distance + 1;
						}
						queue.add(adjacency_pair.getKey());
					}
				}
				if(current_node.distance > potential_host.distance) {
					//stack.push(potential_host);
					potential_host = current_node;
				}
			}
			
//			for(Node n : potential_host.shortest_path) {
//				System.out.println("+" + n.name);
//			}
			
			
			for(int i = potential_host.jumps / 2; i > 0; i--) {
				potential_host = potential_host.parent;
			}
			
			//potential_host = potential_host.shortest_path.get(potential_host.shortest_path.size() / 2);
			list.add(potential_host);
			hosts--;
			host = potential_host;
			reset(host);
		}
		
		return list;
	}
	
	
}
