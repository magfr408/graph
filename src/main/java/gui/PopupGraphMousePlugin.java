package gui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import network.Link;
import network.Node;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;

/**
 * a GraphMousePlugin that offers popup menu support
 */
class PopupGraphMousePlugin extends AbstractPopupGraphMousePlugin implements
		MouseListener {

	public PopupGraphMousePlugin() {
		this(MouseEvent.BUTTON3_DOWN_MASK);
	}

	public PopupGraphMousePlugin(int modifiers) {
		super(modifiers);
	}

	/**
	 * If this event is over a station (vertex), pop up a menu to allow the user
	 * to perform a few actions; else, pop up a menu over the layout/canvas
	 *
	 * @param e
	 */
	protected void handlePopup(MouseEvent e) {
		final VisualizationViewer<Node, Link> vv 
			= (VisualizationViewer<Node, Link>) e.getSource();
		
		final Point2D p = e.getPoint();
		final Point2D ivp = p;

		GraphElementAccessor<Node, Link> pickSupport = vv.getPickSupport();
		if (pickSupport != null) {

			JPopupMenu popup = new JPopupMenu();

			final Node node = pickSupport.getVertex(vv.getGraphLayout(),
					ivp.getX(), ivp.getY());
			
			final Link link = pickSupport.getEdge(vv.getGraphLayout(),
					ivp.getX(), ivp.getY());
			
			if (node != null) {

				popup.add(new AbstractAction("<html><center>" + "example 1") {
					public void actionPerformed(ActionEvent e) {
						//Action goes here...
					}
				});

				popup.add(new AbstractAction("<html><center>" + "example 2") {
					public void actionPerformed(ActionEvent e) {
						//Action goes here...
					}
				});
				
				if (popup.getComponentCount() > 0) {
					popup.show(vv, e.getX(), e.getY());
				}
			} else if (link != null) {
				popup.add(new AbstractAction("<html><center>" + "example 1") {
					public void actionPerformed(ActionEvent e) {
						//Action goes here...
					}
				});

				popup.add(new AbstractAction("<html><center>" + "example 2") {
					public void actionPerformed(ActionEvent e) {
						//Action goes here...
					}
				});
				
				if (popup.getComponentCount() > 0) {
					popup.show(vv, e.getX(), e.getY());
				}				
			}
		}
		/**
		 * else { //to pop-up over the canvas/layout rather than over the
		 * station
		 * 
		 * popup.add(new AbstractAction("Create Unit") { public void
		 * actionPerformed(ActionEvent e) { //do something here } });
		 * 
		 * 
		 * if(popup.getComponentCount() > 0) { popup.show(vv, e.getX(),
		 * e.getY()); } }
		 */
	}
}