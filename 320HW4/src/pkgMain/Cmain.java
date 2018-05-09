package pkgMain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;

public class Cmain {
	
	final static String input = "K:\\Downloads\\input.txt";
	final static String output = "K:\\Downloads\\output.txt";
	static double heur1;
	static double heur2;
	static double p4;
	static double p5;
	
	public static void main(String[] args) throws IOException{

		BufferedReader reader = new BufferedReader(new FileReader(input));
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		
		String line = reader.readLine();
		
		int cases = Integer.parseInt(line);
		int casenum = 1;
		
		while(cases > 0) {
			writer.write("Test case " + casenum);
			writer.newLine();
			line = reader.readLine();
			String[] info = line.split(" ");
			int n = Integer.parseInt(info[0]);
			int f = Integer.parseInt(info[1]);
			int m = Integer.parseInt(info[2]);
			int a = Integer.parseInt(info[3]);
			Graph g = new Graph();
			
			for(int i = 1; i <= n; i++) {
				g.add(new Node(i));
			}
			
			while(f > 0) {
				line = reader.readLine();
				String[] pair = line.split(" ");
				int first = Integer.parseInt(pair[0]);
				int second = Integer.parseInt(pair[1]);
				g.nodes.get(first-1).add_edge_u(g.nodes.get(second-1), new Edge(1));
				
				f--;
			}
			
			if(a == 1) {
				line = reader.readLine();
				
				writer.write("Part 1 answer: " + part1(g.nodes.get(Integer.parseInt(line) - 1)));
				writer.newLine();
				full_reset(g);
				LinkedList<Node> l = new LinkedList<>();
				l.add(g.nodes.get(Integer.parseInt(line)));
				writer.write("Part 2 answer: " + part2(l, g));
				writer.newLine();
			}
			else if(m == 0) {
				LinkedList<Node> l = part5(0, g);
				writer.write("Part 5 answer: " + p5);
				writer.newLine();
				String s = "Part 5 hosts: ";
				for(Node x : l) {
					s = s + x.name + " ";
				}
				writer.write(s);
				writer.newLine();
			}
			else if (a == m) {
				LinkedList<Node> l = new LinkedList<>();
				while(a > 0) {
					line = reader.readLine();
					l.add(g.nodes.get(Integer.parseInt(line) - 1));
					a--;
				}
				writer.write("Part 2 answer: " + part2(l, g));
				writer.newLine();
			}
			else if(a == 0 && m != 0) {
				String ans = part3(m, g);
				writer.write("Heuristic 1 answer: " + heur1);
				writer.newLine();
				writer.write("Heuristic 1 hosts: " + ans.split("x")[0]);
				writer.newLine();
				writer.write("Heuristic 2 answer: " + heur2);
				writer.newLine();
				writer.write("Heuristic 2 hosts: " + ans.split("x")[1]);
				writer.newLine();
				full_reset(g);
				LinkedList<Node> l = part4(m, g);
				writer.write("Part 4 answer: " + p4);
				writer.newLine();
				String s = "Part 4 hosts: ";
				for(Node x : l) {
					s = s + x.name + " ";
				}
				writer.write(s);
				writer.newLine();
			}
			else {
				System.out.println("oops");
			}
			casenum++;
			cases--;
				
		}
		
		
		reader.close();
		writer.close();
	}
	
	
	
	public static void reset(Graph g) {
		for(Node x : g.nodes) {
			x.visited = false;
			
		}
	}
	
	public static void full_reset(Graph g) {
		for(Node x : g.nodes) {
			x.visited = false;
			x.distance = Integer.MAX_VALUE;
			x.jumps = 0;
			x.parent = null;
		}
	}
	
	public static double part1(Node host) {
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
		//System.out.println(total_weight);
		//System.out.println(count);
		count--;
		return total_weight / count;
		//System.out.println("Part 1: average awkwardness from host '" + host.name + "' = " + total_weight/count);
	}
	
	public static double part2(LinkedList<Node> hosts, Graph g) {
		int count = 0;
		int total_weight = 0;
		for(Node host : hosts) {
			reset(g);
			
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
						//adjacency_pair.getKey().distance = Math.min(current_node.distance + 1, adjacency_pair.getKey().distance);
						queue.add(adjacency_pair.getKey());
					}
				}
			}
			
		}
		return awkwardness(hosts.getFirst(), hosts.size());
		
		//System.out.println("count = " + count);
		//System.out.println("total weight = " + total_weight);
	}
	
	public static double awkwardness(Node host, int size) {
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
		//System.out.println("total weight = " + total_weight);
		return total_weight / count;
		//System.out.println("Part 2: average awkwardness with multiple hosts = " + total_weight / count);
	}

	public static String part3(int hosts, Graph g) {
		LinkedList<Node> lists = heuristic1(hosts, g);
		heur1 = part2(lists, g);
		String h = "";
		for(Node n : lists) {
			h = h + n.name + " ";
		}
		h = h + "x";
		full_reset(g);
		
		lists = heuristic2(hosts, g);
		heur2 = part2(lists, g);
		for(Node n : lists) {
			h = h + n.name + " ";
		}
		return h;
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
			reset(g);
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
			reset(g);
		}
		full_reset(g);
		p4 = part2(list, g);
		//System.out.println("Part 4 : " + part2(list, g));
		return list;
	}
	
	public static LinkedList<Node> part5(int hosts, Graph g){
		Node host = heuristic1(1, g).getFirst();
		hosts--;
		host.visited = false;
		//System.out.println(host.name);
		
		LinkedList<Node> list = new LinkedList<>();
		list.add(host);
		p5 = part2(list, g);
		reset(g);
		while(p5 > 1) {
			//System.out.println(list.size());
			//System.out.println(p5);
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
			
			System.out.println(potential_host.name);
			for(int i = potential_host.jumps / 2; i > 0; i--) {
				System.out.println("ok");
				potential_host = potential_host.parent;
			}
			
			//potential_host = potential_host.shortest_path.get(potential_host.shortest_path.size() / 2);
			list.add(potential_host);
			hosts--;
			host = potential_host;
			reset(g);
			p5 = part2(list, g);
			reset(g);
		}
		full_reset(g);
		p5 = part2(list, g);
		//System.out.println("Part 4 : " + part2(list, g));
		return list;
	}
	
}
