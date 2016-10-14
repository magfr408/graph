package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import network.Link;
import network.NetworkViz;
import network.Node;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

@SuppressWarnings("serial")
public class Viewer extends JApplet {

	private JFrame frame;
	private Container content;
	private final GraphZoomScrollPane pane;
	private VisualizationViewer<Node, Link> viz;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	// public Viewer(Graph<Node, Link> g, final VisualizationViewer<Node, Link>
	// viz) {
	public Viewer(final NetworkViz net) {

		this.viz = (VisualizationViewer<Node, Link>) net.present();

		viz.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
		viz.getRenderContext()
				.setVertexLabelTransformer(new ToStringLabeller());
		viz.setVertexToolTipTransformer(new ToStringLabeller());

		this.frame = new JFrame();
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.content = this.frame.getContentPane();

		this.pane = new GraphZoomScrollPane(viz);
		content.add(pane);

		final DefaultModalGraphMouse<Node, Link> graphMouse = new DefaultModalGraphMouse<Node, Link>();
		graphMouse.add(new PopupGraphMousePlugin());
		// final PluggableGraphMouse graphMouse = new PluggableGraphMouse();
		viz.setGraphMouse(graphMouse);
		
		JComboBox modeBox = graphMouse.getModeComboBox();
		//EditingModalGraphMouse<V, E>
		modeBox.addItemListener(graphMouse.getModeListener());
		graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

		
		final ScalingControl scaler = new CrossoverScalingControl();

		JButton zoomIn = new JButton("+");
		zoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(viz, 1.1f, viz.getCenter());
			}
		});

		JButton zoomOut = new JButton("-");
		zoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(viz, 1.0f / 1.1f, viz.getCenter());
			}
		});
		
		JButton updateNodeCoords = new JButton("update");
		updateNodeCoords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				net.updateNodeCoordinates(viz);
			}
		});

		JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
		// scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

		JPanel controls = new JPanel();
		scaleGrid.add(updateNodeCoords);
		scaleGrid.add(zoomIn);
		scaleGrid.add(zoomOut);
		controls.add(scaleGrid);
		controls.add(modeBox);

		this.content.add(controls, BorderLayout.SOUTH);
	}

	public void show() {
		frame.pack();
		frame.setVisible(true);
	}
}