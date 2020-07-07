import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * 
 * @author tony
 */
public class AuckMap extends GUI {

	private final Random random = new Random();

	private double scale;
	private Location origin;
	
	private String currentRoad = "";
	
	private Node startNode;
	private List<Segment> path = new ArrayList<>();
	
	private Set<Node> artPoints = new HashSet<>();

	public AuckMap() {
	}

	@Override
	protected void redraw(Graphics g) {

		g.setColor(Color.BLUE);

		for (Node n : RoadCollection.nodes.values()) {
			if (n.highlighted) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.BLUE);
			}
			n.draw(g, origin, scale);
		}

		for (Segment segment : RoadCollection.segments) {
			if (RoadCollection.roads.get(segment.getRoadid()).getLabel().equals(currentRoad)) {
				g.setColor(Color.red);
			}
			else {
				g.setColor(Color.BLACK);
			}
			
			if (path.contains(segment)) {
				g.setColor(Color.red);
			}
			segment.draw(g, origin, scale);
		}
		
		if (artPoints != null) {
			g.setColor(Color.red);
			for (Node artNode : artPoints) {
				
				artNode.draw(g, origin, scale);
			}
		}
		
		System.out.println(artPoints.size());
		
	}
	
	public void artPoints() {
		ArticulationPoints art = new ArticulationPoints();
		// This loops finds the articulation points given every node.
		for (Node node : RoadCollection.nodes.values()) {
			art.findPts(node);
		}

		artPoints = art.getArtPts();
	}

	@Override
	protected void onClick(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		Location loc = Location.newFromPoint(new Point(x, y), origin, scale);

		clearHighlighting();

		for (Node n : RoadCollection.nodes.values()) {
			if (loc.isClose(n.location, 2)) {
				n.highlighted = true;
				RoadCollection.nodes.put(n.getNodeid(), n);//putting highlighted to update collection
				if (startNode == null) {
					startNode = n;
					for (Node nd : RoadCollection.nodes.values()) {
						nd.visited = false;
						nd.previous = null;
					}
					path.clear();
				}
				else {
					path = ASearch.search(startNode, n);
					startNode = null; //set start node back to null
					
				}
				
				List<String> roads = new ArrayList<>();
				for (Segment s : n.segments) {
					int roadid = s.getRoadid();
					roads.add(RoadCollection.roads.get(roadid).getLabel());//getting each segment in nodes and storing its road id
				}
				getTextOutputArea().setText("");
				getTextOutputArea().append("Intersection ID: " + n.getNodeid() + "\n");
				getTextOutputArea().append("Road Names:\n");
				Set<String> listWithoutDuplicates = new LinkedHashSet<String>(roads); //passing in road names as strings
				for (String r : listWithoutDuplicates) {
					getTextOutputArea().append(r + "\n");
				}

				break;
			}
		}

		redraw();
	}

	public void clearHighlighting() {
		for (Node n : RoadCollection.nodes.values()) {
			if (n.highlighted) {
				n.highlighted = false;
				RoadCollection.nodes.put(n.getNodeid(), n);//putting nodes back after clearing the highlight
			}

		}
	}

	@Override
	protected void onSearch() {
		String name = getSearchBox().getText();
		for (Road r : RoadCollection.roads.values()) {
			if (name.equals(r.getLabel())) {
				getTextOutputArea().append("Found road!\n");
				currentRoad = name;
				break;
			}
			
		}
		
		redraw();
	}

	@Override
	protected void onMove(Move m) {       //to zoom, zoom out to move left right up down
		double zoom = 1.20;
		if (m == Move.ZOOM_IN) {
			scale = scale * zoom;

			this.redraw();
		} else if (m == Move.ZOOM_OUT) {
			scale = scale / zoom;
			this.redraw();
		} else if (m == Move.EAST) {
			origin = origin.moveBy(10 / scale, 0);
		} else if (m == Move.NORTH) {
			origin = origin.moveBy(0, 10 / scale);
		} else if (m == Move.SOUTH) {
			origin = origin.moveBy(0, -10 / scale);
		} else if (m == Move.WEST) {
			origin = origin.moveBy(-10 / scale, 0);
		}
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {  
		RoadCollection.readNodes(nodes);
		RoadCollection.readRoads(roads);
		RoadCollection.readSegments(segments);
		getTextOutputArea().setText("Loading done.");

		double left = Double.POSITIVE_INFINITY;
		double right = Double.NEGATIVE_INFINITY;
		double top = Double.NEGATIVE_INFINITY;
		double bot = Double.POSITIVE_INFINITY;

		for (Node n : RoadCollection.nodes.values()) {
			if (n.location.x < left) {
				left = n.location.x;
			}
			if (n.location.x > right) {
				right = n.location.x;
			}
			if (n.location.y > top) {
				top = n.location.y;
			}
			if (n.location.y < bot) {
				bot = n.location.y;
			}
		}

		origin = new Location(left, top);

		scale = Math.min(400 / (right - left), 400 / (top - bot));//to fit in the pane
	}

	public static void main(String[] args) {
		new AuckMap();
	}
}

// code for COMP261 assignments