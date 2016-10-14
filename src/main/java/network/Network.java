package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.GeometryFactory;

import core.DatabaseException;
import core.DatabaseReader;
import core.Monitor;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class Network {
	private final HashMap<Long, Link> links; //link id as key
	private final HashMap<Long, Node> nodes; //node id as key
	private final HashMap<Long, LinkGroup> linkGroups; //group id as key, previously known as MG
	private final GeometryFactory factory;
	private Long linkGroupId;
	private Graph<Node, LinkGroup> g;
	public DatabaseReader dbr;
	private boolean instantiated = false;

	public Network(ArrayList<Link> links, DatabaseReader dbr, GeometryFactory factory)
			throws NetworkInstantiationException {

		if (links == null || links.isEmpty()) {
			throw new NetworkInstantiationException(
					"Cannot make network without links.");
		}

		
		this.dbr = dbr;
		this.factory = factory;
		this.links = new HashMap<Long, Link>();
		this.nodes = new HashMap<Long, Node>();
		this.linkGroups = new HashMap<Long, LinkGroup>();
		// TODO: Should have a better way of doing IDs if they are stored in DB.
		this.linkGroupId = 0l;
		
		for (Link link: links) {
			if (!this.links.containsKey(link.id)) {
				this.links.put(link.id, link);
			}
		}
		
		//TODO: Informed choice of graph.
		this.g = new DirectedSparseGraph<Node, LinkGroup>();

		try {
			this.build();
		} catch (DatabaseException e) {
			Monitor.err(e.getMessage());
			throw new NetworkInstantiationException(
					"Something went wrong while reading network from DB.");
		}
	}

	/**
	 * Actually build the network, or rather, populate the arraylists of nodes
	 * and links.
	 * 
	 * @throws DatabaseException
	 */
	private void build() throws DatabaseException {
		if (!this.instantiated) {
			
			HashMap<Long, ArrayList<Link>> nodeLinks = new HashMap<Long, ArrayList<Link>>(); //map between nodes and connecting links
			HashMap<Long, Long> linkLinkGroups = new HashMap<Long, Long>(); //map from link to linkgroup
			
			/*
			 * Populate map with mapping between nodes and incoming/outgoing links.
			 * This is used to check the number of links connecting to a node.
			 */
			for (Link link : this.links.values()) {
				if (!nodeLinks.containsKey(link.startNode.id)) {
					ArrayList<Link> value = new ArrayList<Link>();
					value.add(link);
					nodeLinks.put(link.startNode.id, value);
				} else {
					nodeLinks.get(link.startNode.id).add(link);
				}
				
				if (!nodeLinks.containsKey(link.endNode.id)) {
					ArrayList<Link> value = new ArrayList<Link>();
					value.add(link);
					nodeLinks.put(link.endNode.id, value);
				} else {
					nodeLinks.get(link.endNode.id).add(link);
				}
			}
			
			/*
			 * For each pair in the mapping between nodes and links...
			 */
			for (Map.Entry<Long, ArrayList<Link>> entry : nodeLinks.entrySet()) {
				Node n;
				/*
				 * Check if links in this node could be added to the same link
				 * group.
				 */
				if ((entry.getValue().size() == 2)
						&& (entry.getValue().get(0).propertyEqualTo(entry.getValue().get(1)))) {

					if (entry.getKey() == entry.getValue().get(0).startNode.id) {
						n = entry.getValue().get(0).startNode;
					} else {
						n = entry.getValue().get(0).endNode;
					}
					
					/*
					 * If neither of the links are in a group then add them to a new group.
					 */
					if (!linkLinkGroups.containsKey(entry.getValue().get(0).id)
							&& !linkLinkGroups.containsKey(entry.getValue().get(1).id)) {

						LinkGroup group = LinkGroup.newLinkGroup(n, entry.getValue().get(0), entry.getValue().get(1), factory);

						if (group != null) {
							this.linkGroups.put(this.linkGroupId, group);
							linkLinkGroups.put(entry.getValue().get(0).id, this.linkGroupId);
							linkLinkGroups.put(entry.getValue().get(1).id, this.linkGroupId);
							this.linkGroupId++;
						} else {
							group = new LinkGroup();
							//Could not add to group, make two groups.
							group.addLink(entry.getValue().get(0), this.factory, entry.getValue().get(0).geom.getSRID(), true);
							this.linkGroups.put(this.linkGroupId, group);
							linkLinkGroups.put(entry.getValue().get(0).id, this.linkGroupId);
							this.linkGroupId++;
							
							group = new LinkGroup();
							group.addLink(entry.getValue().get(1), this.factory, entry.getValue().get(1).geom.getSRID(), true);
							linkLinkGroups.put(entry.getValue().get(1).id, this.linkGroupId);
							this.linkGroupId++;
						}
						
					/*
					 * The second link is already in a group, add the first link to that group.	
					 */
					} else if (!linkLinkGroups.containsKey(entry.getValue().get(0).id)) {
						
						Boolean b = LinkGroup.orderInLinkGroup(n, entry.getValue().get(0), entry.getValue().get(1));
						
						if (b != null) {
							this.linkGroups.get(linkLinkGroups.get(entry.getValue().get(1).id)).addLink(entry.getValue().get(0), this.factory, entry.getValue().get(0).geom.getSRID(), b);
							
							linkLinkGroups.put(entry.getValue().get(0).id, linkLinkGroups.get(entry.getValue().get(1).id));					
						} else {
							// On b == null, links couldn't be grouped, add first link to new group 
							LinkGroup group = new LinkGroup();
							//Could not add to group, make two groups.
							group.addLink(entry.getValue().get(0), this.factory, entry.getValue().get(0).geom.getSRID(), true);
							this.linkGroups.put(this.linkGroupId, group);
							linkLinkGroups.put(entry.getValue().get(0).id, this.linkGroupId);
							this.linkGroupId++;
						}
						
					/*
					 * The first link is already in a group, add the second link to that group.
					 */
					} else if (!linkLinkGroups.containsKey(entry.getValue()
							.get(1).id)) {
						
						Boolean b = LinkGroup.orderInLinkGroup(n, entry.getValue().get(1), entry.getValue().get(0));
						
						if (b != null) {

							this.linkGroups.get(linkLinkGroups.get(entry.getValue().get(0).id)).addLink(entry.getValue().get(1), this.factory, entry.getValue().get(1).geom.getSRID(), b);

							linkLinkGroups.put(entry.getValue().get(0).id, linkLinkGroups.get(entry.getValue().get(1).id));

						} else {
							// On b == null, links couldn't be grouped, add second link to new group 
							LinkGroup group = new LinkGroup();
							group.addLink(entry.getValue().get(1), this.factory, entry.getValue().get(1).geom.getSRID(), true);
							linkLinkGroups.put(entry.getValue().get(1).id, this.linkGroupId);
							this.linkGroupId++;
						}

					/*
					 * Both links are in separate groups, can the groups be merged?
					 */
					} else {
						LinkGroup G1 = this.linkGroups.get(linkLinkGroups.get(entry.getValue().get(0).id));
						LinkGroup G2 = this.linkGroups.get(linkLinkGroups.get(entry.getValue().get(1).id));
						
						Boolean b = G1.addGroup(G2, this.factory, G2.getLinkN(0).geom.getSRID());
						
						if (!b)
						// TODO: Merge the groups. Don't forget to update the mappings.
					}
				} else {
					// TODO: Should they be added to separated groups?
				}
			}
			
			this.instantiated = true;
		}
	}

	public void writeToDatabase() {
		if (this.instantiated) {
			// Save as network to DB, just as the MG.
		}
	}

	public void writeToFile() {
		if (this.instantiated) {
			// TODO: Stuff
		}
	}
}