package gui;

import util.TinyTestGraph;
import network.Link;
import network.NetworkViz;
import network.Node;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;

import edu.uci.ics.jung.graph.Graph;

public class Master {
	public static void main(String[] args) {
		try {
			PrecisionModel pm = new PrecisionModel();
			GeometryFactory factory = new GeometryFactory(pm);

			NetworkViz net = new NetworkViz(factory,
					(Graph<Node, Link>) TinyTestGraph.getTinyTestGraph(factory));

			Viewer view = new Viewer(net);
			view.show();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}