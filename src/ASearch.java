import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ASearch {
	
	
	public static PriorityQueue<Record> fringe = new PriorityQueue<>(new Comparator<Record>() {


		@Override
		public int compare(Record o1, Record o2) {
			return (int) (o1.heuristic - o2.heuristic);
		}
		
	});
	//takes in start node and the goal node as parameter
	//path that will contain the list of all segments from start to goal node, hence the path
	//start Record type holds the starting node as first parameter, null as previous node as there are no previous nodes
	//first location is 0, heuristic is the location from the start to the goal node(using location from location class, straightline dist)
	//adds the start to the fringe
	//so while fringe is not empty in this case it takes out the start and put it in variable r and remove it from the fringe
	//if this node is not visited yet, then set visited to true now
	//set current node's previous node field to r's previous which in first case is null
	//if record r's current node's id equals the goal node's id then we break from while
	
	//create a list of neighbors then put current node's neighbors in the list of neighbors
	//now for each neighbor in neighbors we create a neigh node object that holds neighbor node 
	//if the neigh is not visited we add to the distance variable = the record's distance(which is 0 at the start)+this node's segment's length
	//and for heuristic we put the distance + distance between neighbour's location and the goals's location
	//then we add new Record to the fringe with neigh as the current node, the current node as previous, updated distance as dist and updated heuristic
	//the while loop goes again and we go through all the nodes reaching the goal
	public static List<Segment> search(Node s, Node g) {
		List<Segment> path = new ArrayList<>();
		
		Record start = new Record(s, null, 0, s.location.distance(g.location));
		fringe.offer(start);
		
		while (!fringe.isEmpty()) {
			Record r = fringe.poll();
			if (!r.current.visited) {
				r.current.visited = true;
				r.current.previous = r.prev;
				
				if (r.current.getNodeid() == g.getNodeid()) {
					break;
				}
				
				List<Neighbour> neighbours = r.current.getNeighbours();
				
				for (Neighbour noden : neighbours) {
					Node neigh = noden.n;
					if (!neigh.visited) {
						double dist = r.distance + noden.segment.getLength();
						double heuristic = dist + neigh.location.distance(g.location);
						fringe.offer(new Record(neigh, r.current, dist, heuristic));
					}
				}
			}
		}
		
		Node n1 = g; //We assign the goal node to n1 Node
		Node n2 = g.previous; //We assign the one before the goal node to n2
		//while checks if we reach the the start node, means while the previous is not null since the start node's previous node was null
		while (n2 != null) {
			for (Segment seg : n2.segments) { //for each segment in the previous node
				if (seg.getNodeid1() == n1.getNodeid() || seg.getNodeid2() == n1.getNodeid()) { //if segment's node id 1 or 2 equals goal node's id, we add seg to path and break out of for loop
					path.add(seg);
					break;
				}
			}
			n1 = n1.previous; // then n1 which started from the goal is assigned the second last node and n2 is assigned third last
			n2 = n2.previous;
		}
		
		return path; //this will return the path from goal to the start in the end
	}

}
