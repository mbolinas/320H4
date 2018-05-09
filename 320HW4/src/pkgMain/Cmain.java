/*
 * @Marc Bolinas
 * 5/8/18
 * CISC320
 * HW4
 */


package pkgMain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;

public class Cmain {
	
	final static String input = "K:\\Downloads\\input.txt";
	final static String output = "K:\\Downloads\\output.txt";
	
	//These are global variables that hold the avg awkwardness
	//Ideally there wouldn't be global variables, but then I would have to update the return types of all my functions
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
			
			//Create graph
			Graph g = new Graph();
			for(int i = 1; i <= n; i++) {
				g.add(new Node(i));
			}
			
			//Populate graph with nodes
			while(f > 0) {
				line = reader.readLine();
				String[] pair = line.split(" ");
				int first = Integer.parseInt(pair[0]);
				int second = Integer.parseInt(pair[1]);
				g.nodes.get(first-1).add_edge_u(g.nodes.get(second-1), new Edge(1));
				
				f--;
			}
			
			//Call part1 and part2 only
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
			//Call part5 only
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
			//Call part2 only
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
			//Call part3, which calls the heuristics, and part4
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
		
		//Standard BFS
		Queue<Node> queue = new LinkedList<>();
		queue.add(host);
		
		while(queue.size() > 0) {
			count++;
			Node current_node = queue.poll();
			total_weight = total_weight + current_node.distance;
			for(Entry <Node, Edge> adjacency_pair : current_node.adjacent_nodes.entrySet()) {
				if(adjacency_pair.getKey().visited == false) {
					adjacency_pair.getKey().visited = true;
					adjacency_pair.getKey().distance = current_node.distance + 1;
					queue.add(adjacency_pair.getKey());
				}
			}
		}
		count--;	//don't count the host
		return total_weight / count;
	}
	
	public static double part2(LinkedList<Node> hosts, Graph g) {
		for(Node host : hosts) {
			reset(g);	//reset all "visited" to false, so that we can rerun our algorithm
			host.distance = 0;
			host.visited = true;
			
			//Perform BFS for each host, updating distance if 'adding' a new host causes the distance to decrease
			Queue<Node> queue = new LinkedList<>();
			queue.add(host);
			
			while(queue.size() > 0) {
				Node current_node = queue.poll();
				
				for(Entry <Node, Edge> adjacency_pair : current_node.adjacent_nodes.entrySet()) {
					if(adjacency_pair.getKey().visited == false) {
						adjacency_pair.getKey().visited = true;
						if(current_node.distance + 1 < adjacency_pair.getKey().distance) {
							adjacency_pair.getKey().distance = current_node.distance + 1;
						}
						queue.add(adjacency_pair.getKey());
					}
				}
			}
			
		}
		
		return calculate_avg(hosts.getFirst(), hosts.size());
	}
	//A helper function, general BFS based off of part1
	//part1 could not be simply called because of how 'count' was calculated (it would require passing in count as another variable)
	public static double calculate_avg(Node host, int size) {
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
			
			for(Entry <Node, Edge> adjacency_pair : current_node.adjacent_nodes.entrySet()) {
				if(adjacency_pair.getKey().visited == true) {
					adjacency_pair.getKey().visited = false;
					queue.add(adjacency_pair.getKey());
				}
			}
		}
		count--;
		count = count - size;
		return total_weight / count;
	}
	//calls both heuristic1 and heuristic2, because of it's nature of having to call and return values from 2 separate functions it's a bit of a mess
	public static String part3(int hosts, Graph g) {
		LinkedList<Node> lists = heuristic1(hosts, g);
		heur1 = part2(lists, g);
		String result_hosts = "";
		for(Node n : lists) {
			result_hosts = result_hosts + n.name + " ";
		}
		result_hosts = result_hosts + "x";
		
		full_reset(g);
		
		lists = heuristic2(hosts, g);
		heur2 = part2(lists, g);
		for(Node n : lists) {
			result_hosts = result_hosts + n.name + " ";
		}
		return result_hosts;
	}
	
	
	public static LinkedList<Node> heuristic1(int hosts, Graph g) {
		LinkedList<Node> list = new LinkedList<>();
		Node current_node = null;
		while(hosts > 0) {
			//for each node, find the biggest one, that's the one to add to the hosts
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
		//Start by picking the node with the most friends
		Node host = heuristic1(1, g).getFirst();
		hosts--;
		host.visited = false;
		
		LinkedList<Node> list = new LinkedList<>();
		list.add(host);
		
		//Then find the node that has the highest distance, add that to the list
		//Then recalculate distances again, repeat
		while(hosts > 0) {
			Node potential_host = host;
			host.distance = 0;
			host.visited = true;
			
			Queue<Node> queue = new LinkedList<>();
			queue.add(host);
			
			while(queue.size() > 0) {
				Node current_node = queue.poll();
				
				for(Entry <Node, Edge> adjacency_pair : current_node.adjacent_nodes.entrySet()) {
					if(adjacency_pair.getKey().visited == false) {
						adjacency_pair.getKey().visited = true;
						if(current_node.distance + 1 < adjacency_pair.getKey().distance) {
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
	
	//This heuristic is very similar to heuristic2, but instead of picking the node with the highest distance
	//it finds the path to the node with the highest distance, then picks the node halfway between that path
	public static LinkedList<Node> part4(int hosts, Graph g) {
		Node host = heuristic1(1, g).getFirst();
		hosts--;
		host.visited = false;
		
		LinkedList<Node> list = new LinkedList<>();
		list.add(host);
		
		
		while(hosts > 0) {
			Node potential_host = host;
			host.distance = 0;
			host.visited = true;
			host.jumps = 0;
			
			Queue<Node> queue = new LinkedList<>();
			queue.add(host);
			
			while(queue.size() > 0) {
				Node current_node = queue.poll();
				
				for(Entry <Node, Edge> adjacency_pair : current_node.adjacent_nodes.entrySet()) {
					if(adjacency_pair.getKey().visited == false) {
						adjacency_pair.getKey().visited = true;
						if(current_node.distance + 1 < adjacency_pair.getKey().distance) {
							//parent and jumps keeps track of the path of nodes to the host, as well as how far they are
							adjacency_pair.getKey().parent = current_node;
							adjacency_pair.getKey().jumps = current_node.jumps + 1;
							adjacency_pair.getKey().distance = current_node.distance + 1;
						}
						queue.add(adjacency_pair.getKey());
					}
				}
				if(current_node.distance > potential_host.distance) {
					potential_host = current_node;
				}
			}
			
			//make potential_host the node halfway between host and the original potential_host
			for(int i = potential_host.jumps / 2; i > 0; i--) {
				potential_host = potential_host.parent;
			}
			
			list.add(potential_host);
			hosts--;
			host = potential_host;
			reset(g);
		}
		full_reset(g);
		p4 = part2(list, g);
		return list;
	}
	//Very similar to part4 (as it was meant to be)
	//Main differences are the big main loop is changed to p5 > 1, and p5 is updated after every iteration
	public static LinkedList<Node> part5(int hosts, Graph g){
		Node host = heuristic1(1, g).getFirst();
		hosts--;
		host.visited = false;
		
		LinkedList<Node> list = new LinkedList<>();
		list.add(host);
		p5 = part2(list, g);
		reset(g);
		while(p5 > 1) {
			Node potential_host = host;
			host.distance = 0;
			host.visited = true;
			host.jumps = 0;
			
			Queue<Node> queue = new LinkedList<>();
			queue.add(host);
			
			while(queue.size() > 0) {
				Node current_node = queue.poll();
				
				for(Entry <Node, Edge> adjacency_pair : current_node.adjacent_nodes.entrySet()) {
					if(adjacency_pair.getKey().visited == false) {
						adjacency_pair.getKey().visited = true;
						if(current_node.distance + 1 < adjacency_pair.getKey().distance) {
							adjacency_pair.getKey().parent = current_node;
							adjacency_pair.getKey().jumps = current_node.jumps + 1;
							adjacency_pair.getKey().distance = current_node.distance + 1;
						}
						queue.add(adjacency_pair.getKey());
					}
				}
				if(current_node.distance > potential_host.distance) {
					potential_host = current_node;
				}
			}
			
			System.out.println(potential_host.name);
			for(int i = potential_host.jumps / 2; i > 0; i--) {
				System.out.println("ok");
				potential_host = potential_host.parent;
			}
			
			list.add(potential_host);
			hosts--;
			host = potential_host;
			reset(g);
			p5 = part2(list, g);
			reset(g);
		}
		full_reset(g);
		p5 = part2(list, g);
		return list;
	}
	
}
