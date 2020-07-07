import java.util.HashSet;
import java.util.Set;

public class ArticulationPoints {
//set to avoid duplicates
	private Set<Node> pts;

	public ArticulationPoints() {
		pts = new HashSet<Node>();
	}
    //this method takes in the start node
	//initially the count is 0
	//initially the subtrees are 0
	//the we go through each neighbor of the start node
	//if the neigbour's count is infinity that means the neighbor is already visited
	//then we call recursiveArticulation method passing in this neighbor, with count 1, parent node is the start node
	//in the recursiveArticulation: node's count is 1, and reach back is also assigned the count which is 1 now
	//we go through each neighbor of the node which was the neighbor of the start node
	//we put the neighbor in the neighbor field
	//then if that neighbor is not equal to the parent(which was our start node) and if the neighbor was not visited(if count is not infinity) then we assign reach back either the count or reach back whichever is minimum
	//else calculate alternative paths of the child, which can also be reached by itself
	
	public void findPts(Node s) {
		Node start = s;
		start.count = 0;
		int subtrees = 0;

		for (Neighbour ne : start.getNeighbours()) {
			Node neighbour = ne.n; 
			if (neighbour.count == Integer.MAX_VALUE) {
				recursiveArticulation(neighbour, 1, start);
				subtrees++;
			}
		}
		if (subtrees > 1) pts.add(start);//greater than one so its an articultaion point
	}

	private int recursiveArticulation(Node node, int count, Node parent) {
		node.count = count;
		int rb = count;

		for (Neighbour ne : node.getNeighbours()) {
			Node neighbour = ne.n;
			if (!neighbour.equals(parent)) {
				if (neighbour.count != Integer.MAX_VALUE) {
					rb = Math.min(neighbour.count, rb);
				}
				else {
					int cr = recursiveArticulation(neighbour, count+1, node);
					rb = Math.min(cr, rb);
					if (cr >= count) pts.add(node);
				}
			}
		}
		return rb;
	}
	
	public Set<Node> getArtPts() { return pts; }
}
