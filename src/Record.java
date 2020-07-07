import java.util.List;
//
//
//
//we should have two nodes one should represent current node and the other should represent previous
public class Record {
	public Node current;
	public Node prev;
	public double distance;
	public double heuristic;

	public Record(Node current, Node prev, double distance, double heuristic) {
		this.current = current;
		this.prev = prev;
		this.distance = distance;
		this.heuristic = heuristic;
	}
}