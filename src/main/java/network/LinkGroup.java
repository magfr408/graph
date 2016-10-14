package network;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

public class LinkGroup {
	private ArrayList<Link> links; //ArrayLists maintains the order
	private MultiLineString geom;

	/**
	 * <b>Default constructor</b>
	 */
	public LinkGroup() {
		this.links = new ArrayList<Link>();
	}

	public Boolean addGroup(LinkGroup other, GeometryFactory factory, Integer SRID) {
		
		Boolean b = false;
		
		if (this.getEndNode().id == other.getStartNode().id) {
			b = LinkGroup.orderInLinkGroup(
					this.getEndNode(), other.getLinkN(0), this.getLinkN(this.getNumberOfLinks()-1));
			
			for (Link L1 : other.links) {
				this.addLink(L1, factory, SRID, b);
			}
		} else if (this.getStartNode().id == other.getEndNode().id) {
			b = LinkGroup.orderInLinkGroup(
					this.getStartNode(), this.getLinkN(0), other.getLinkN(this.getNumberOfLinks()-1));
			
			for (Link L1 : other.links) {
				this.addLink(L1, factory, SRID, b);
			}		
		} else if (this.getEndNode().id == other.getEndNode().id) {
			b = LinkGroup.orderInLinkGroup(
					this.getEndNode(), other.getLinkN(other.getNumberOfLinks()-1), this.getLinkN(this.getNumberOfLinks()-1));
			
			for (Link L1 : other.links) {
				this.addLink(L1, factory, SRID, b);
			}			
		} else if (this.getStartNode().id == other.getStartNode().id) {
			b = LinkGroup.orderInLinkGroup(
					this.getStartNode(), this.getLinkN(0), other.getLinkN(0));
			
			for (Link L1 : other.links) {
				this.addLink(L1, factory, SRID, b);
			}				
		}
		
		if (!b) {
			b = true;
		}
		
		return b;
	}
	
	/**
	 * 
	 * @param link
	 * @param factory
	 * @param SRID
	 * @param first true if this Link is first in the List.
	 * @throws IllegalArgumentException
	 */
	public void addLink(Link link, GeometryFactory factory, Integer SRID,
			boolean first) throws IllegalArgumentException {

		if (links.isEmpty()) {
			this.links.add(link);
			LineString[] lineStrings = { link.geom };

			this.geom = new MultiLineString(lineStrings, factory);

			if (SRID != null) {
				this.geom.setSRID(SRID);
			}
		} else {

			boolean add = true;

			for (Link other : links) {
				if (other.id == link.id) {
					add = false;
					break;
				}
			}

			if (add) {
				LineString[] lineStrings = new LineString[this.geom
						.getNumGeometries() + 1];

				if (this.links.isEmpty()) {
					this.links.add(0, link);

					lineStrings[0] = link.geom;

					for (int i = 1; i < this.geom.getNumGeometries() + 1; i++) {
						lineStrings[i] = (LineString) this.geom.getGeometryN(i);
					}

				} else {
					if (first) {
						this.links.add(0, link);
						
						for (int i = 0; i < this.links.size(); i++) {
							lineStrings[i] = (LineString) this.links.get(i).geom.getGeometryN(i);
						}					
					} else {
						this.links.add(link);
	
						for (int i = 0; i < this.geom.getNumGeometries(); i++) {
							lineStrings[i] = (LineString) this.geom.getGeometryN(i);
						}
	
						lineStrings[lineStrings.length - 1] = link.geom;
					}
				}

				// TODO: Losing SRID here.
				this.geom = new MultiLineString(lineStrings, factory);
			}
		}
	}
	
	// TODO: Is this correct?
	public Node getStartNode() {
		return this.links.get(0).startNode;
		/*
		if (this.links.get(0).drivingDirection == Link.Directions.FORWARD) {
			return this.links.get(0).startNode;
		} else {
			return this.links.get(this.links.size() - 1).endNode;
		}
		*/
	}

	// TODO: Is this correct?
	public Node getEndNode() {
		return this.links.get(this.links.size() - 1).endNode;
		/*
		if (this.links.get(this.links.size() - 1).drivingDirection == Link.Directions.FORWARD) {
			return this.links.get(this.links.size() - 1).endNode;
		} else {
			return this.links.get(0).startNode;
		}
		*/
	}
	
	public Link getLinkN(int n) {
		return this.links.get(n);
	}
	
	public int getNumberOfLinks() {
		return this.links.size();
	}
	
	/**
	 * This method returns a link group with the links in a sensible order, or
	 * null if the links shouldn't be in the same group. Note: Method doesn't
	 * make any check about sensibility except for the driving direction of both
	 * links. This means that it does not check if L1 and L2 are property equal
	 * to each other or if n only has two connecting links.
	 * 
	 * @param n
	 *            the node that connects only two links, L1 and L2.
	 * @param L1
	 *            the first link connecting to n
	 * @param L2
	 *            the second link connecting to n
	 * @param factory
	 * @return a link group with L1 and L2 in a sensible order, or null if L1
	 *         and L2 shouldn't be in the same group.
	 */
	public static LinkGroup newLinkGroup(Node n, Link L1, Link L2, GeometryFactory factory) {
		Boolean b = LinkGroup.orderInLinkGroup(n, L1, L2);
		
		LinkGroup group = new LinkGroup();
		
		if (b == null) {
			return null;
		} else if (b) {
			group.addLink(L1, factory, L1.geom.getSRID(), true);
			group.addLink(L2, factory, L2.geom.getSRID(), false);
		} else {
			group.addLink(L2, factory, L2.geom.getSRID(), true);
			group.addLink(L1, factory, L1.geom.getSRID(), false);
		}
		
		return group;
	}
	
	/**
	 * Checks the order in which the Links L1 and L2 should be added to a link
	 * group.
	 * 
	 * @param n
	 * @param L1
	 * @param L2
	 * @return <b><i>true</i></b> if L1 should be first, <b><i>false</i></b> if L2 should be
	 *         first, <b><i>null</i></b> if L1 and L2 shouldn't be in the same
	 *         LinkGroup.
	 */
	public static Boolean orderInLinkGroup(Node n, Link L1, Link L2) {
		Boolean b = null;
		if (n.id == L1.startNode.id) {
			if (L1.drivingDirection == Link.Directions.FORWARD) {
				if ((n.id == L2.endNode.id) && (L2.drivingDirection == Link.Directions.FORWARD)) {
					//s->e s->e, L2 is first in return array
					b = false;
				} else if ((n.id == L2.endNode.id) && (L2.drivingDirection == Link.Directions.BACKWARD)) {
					//this doesn't make any sense: s<-e s->e, return null
				} else if ((n.id == L2.startNode.id) && (L2.drivingDirection == Link.Directions.FORWARD)) {
					//this doesn't make any sense: e<-s s->e, return null
				} else if ((n.id == L2.startNode.id) && (L2.drivingDirection == Link.Directions.BACKWARD)) {
					//e->s s->e, L2 is first in return array
					b = false;
				}
			} else if (L1.drivingDirection == Link.Directions.BACKWARD) {
				if ((n.id == L2.endNode.id) && (L2.drivingDirection == Link.Directions.FORWARD)) {
					//this doesn't make any sense: s->e s<-e, return null
				} else if ((n.id == L2.endNode.id) && (L2.drivingDirection == Link.Directions.BACKWARD)) {
					//s<-e s<-e, L1 is first in return array
					b = true;
				} else if ((n.id == L2.startNode.id) && (L2.drivingDirection == Link.Directions.FORWARD)) {
					//e<-s s<-e, L1 is first in return array
					b = true;
				} else if ((n.id == L2.startNode.id) && (L2.drivingDirection == Link.Directions.BACKWARD)) {
					//this doesn't make any sense: e->s s<-e, return null
				}				
			} else {
				if (L2.drivingDirection == Link.Directions.BOTH) {
					//Order doesn't matter, but L2 first makes more sense
					b = false;
				}
			}
		} else {
			if (L1.drivingDirection == Link.Directions.FORWARD) {
				if ((n.id == L2.endNode.id) && (L2.drivingDirection == Link.Directions.FORWARD)) {
					//this doesn't make any sense: s->e e<-s, return null
				} else if ((n.id == L2.endNode.id) && (L2.drivingDirection == Link.Directions.BACKWARD)) {
					//s->e e->s, L1 is first in return array
					b = true;
				} else if ((n.id == L2.startNode.id) && (L2.drivingDirection == Link.Directions.FORWARD)) {
					//s->e s->e, L1 is first in return array
					b = true;
				} else if ((n.id == L2.startNode.id) && (L2.drivingDirection == Link.Directions.BACKWARD)) {
					//this doesn't make any sense: s->e s<-e, return null
				}
			} else if (L1.drivingDirection == Link.Directions.BACKWARD) {
				if ((n.id == L2.endNode.id) && (L2.drivingDirection == Link.Directions.FORWARD)) {
					//s<-e e<-s, L2 is first in return array
					b = false;
				} else if ((n.id == L2.endNode.id) && (L2.drivingDirection == Link.Directions.BACKWARD)) {
					//this doesn't make any sense: s<-e e->s, return null
				} else if ((n.id == L2.startNode.id) && (L2.drivingDirection == Link.Directions.FORWARD)) {
					//this doesn't make any sense: s<-e s->e, return null
				} else if ((n.id == L2.startNode.id) && (L2.drivingDirection == Link.Directions.BACKWARD)) {
					//s<-e s<-e: L2 is first in return array
					b = false;
				}				
			} else {
				if (L2.drivingDirection == Link.Directions.BOTH) {
					//Order doesn't matter, but L1 first makes more sense
					b = true;
				}
			}			
		}

		return b;
	}
}