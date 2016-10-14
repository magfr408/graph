package network;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import util.CustomComparator;

import com.vividsolutions.jts.geom.GeometryFactory;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class NetworkViz {
	private static final double precision = 0.01;
	public Graph<Node, Link> g;
	private GeometryFactory factory;

	public NetworkViz(GeometryFactory factory, Graph<Node, Link> g) {
		this.factory = factory;
		this.g = g;
	}

	public void simplify() {
		ArrayList<Node> nodes = new ArrayList<Node>(g.getVertices());
		Collections.sort(nodes, new CustomComparator());
		ArrayList<Link> links = new ArrayList<Link>(g.getEdges());

		Iterator<Node> iterator = nodes.iterator();

		while (iterator.hasNext()) {
			Node node = iterator.next();

			if (g.degree(node) == 2) {
				links = new ArrayList<Link>(g.getIncidentEdges(node));

				Link L0 = null;
				Link L1 = null;

				/**
				 * Ensures stability.
				 */
				if ((links.get(0).endNode.id == node.id)
						&& ((links.get(1).startNode.id == node.id))) {
					L0 = links.get(1);
					L1 = links.get(0);
				} else if ((links.get(0).endNode.id == node.id)
						&& ((links.get(1).endNode.id == node.id))) {
					L0 = links.get(1);
					L1 = links.get(0);
				} else if ((links.get(0).startNode.id == node.id)
						&& ((links.get(1).endNode.id == node.id))) {
					L0 = links.get(0);
					L1 = links.get(1);
				} else if ((links.get(0).startNode.id == node.id)
						&& ((links.get(1).startNode.id == node.id))) {
					L0 = links.get(0);
					L1 = links.get(1);
				}

				/**
				 * Check if graph can be simplified.
				 */
				if (L0.propertyEqualTo(L1)) {
					Link L2 = L0.mergeWith(L1, node, this.factory,
							NetworkViz.precision);

					// Simplify graph.
					if (L2 != null) {
						g.removeVertex(node);
						iterator.remove();
						g.addEdge(L2, L2.startNode, L2.endNode,
								EdgeType.DIRECTED);
					}
				}
			}
		}
	}

	public void updateNodeCoordinates(VisualizationViewer<Node, Link> viz) {
		Point2D p;
		for (Node node : this.g.getVertices()) {
			p = viz.getGraphLayout().transform((Node) node);
			node.x = p.getX();
			node.y = p.getY();
			viz.getGraphLayout().setLocation(node, p);
		}
	}

	public VisualizationServer<Node, Link> present() {
		StaticLayout<Node, Link> layout = new StaticLayout<Node, Link>(this.g);
		ArrayList<Node> nodes = new ArrayList<Node>(this.g.getVertices());

		for (Node node : nodes) {
			layout.setLocation(node, new Point2D.Double(node.x, node.y));
			layout.lock(node, true);
		}

		VisualizationServer<Node, Link> viz = new VisualizationViewer<Node, Link>(
				layout);

		return viz;
	}
}