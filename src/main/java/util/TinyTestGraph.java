package util;

import network.Link;
import network.Node;
import network.Link.Directions;
import network.Link.LinkSource;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class TinyTestGraph {
	public static Graph<Node, Link> getTinyTestGraph(GeometryFactory factory) throws ParseException {
		WKTReader reader = new WKTReader(factory);

		LineString geom1 = (LineString) reader
				.read("LINESTRING (0.0 0.0, 10 10, 30.0 30.0)");
		LineString geom2 = (LineString) reader
				.read("LINESTRING (30.0 30.0, 40 30, 50.0 30.0)");
		LineString geom3 = (LineString) reader
				.read("LINESTRING (-100.0 -50.0, 0.0 0.0)");

		Node n1 = new Node("1", 1, 0.0d, 0.0d);
		Node n2 = new Node("2", 2, 30.0d, 30.0d);
		Node n3 = new Node("3", 3, 50.0d, 30.0d);
		Node n4 = new Node("4", 4, -100.0d, -50.0d);

		Link l1 = new Link("1", (long) 1, Link.LinkSource.UNKNOWN, n1, n2, Directions.FORWARD, 70.0d, (short) 1, false,
				geom1, factory);
		Link l2 = new Link("2", (long) 2, Link.LinkSource.UNKNOWN, n2, n3, Directions.FORWARD, 70.0d, (short) 1, false,
				geom2, factory);
		Link l3 = new Link("3", (long) 3, Link.LinkSource.UNKNOWN, n4, n1, Directions.FORWARD, 70.0d, (short) 1, false,
				geom3, factory);

		Graph<Node, Link> g = new SparseMultigraph<Node, Link>();
		// Add some vertices.
		g.addVertex(n1);
		g.addVertex(n2);
		g.addVertex(n3);
		g.addVertex(n4);

		// Add some edges.
		g.addEdge(l1, n1, n2, EdgeType.DIRECTED);
		g.addEdge(l2, n2, n3, EdgeType.DIRECTED);
		g.addEdge(l3, n4, n1, EdgeType.DIRECTED);

		return g;
	}
}