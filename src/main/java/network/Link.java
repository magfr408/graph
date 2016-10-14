package network;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

/**
 * 
 * @author Magnus Fransson
 * @version 0.1
 * @since Java 1.7
 */
public class Link {

	public final String oid;
	public final long id;
	public final Link.LinkSource source;
	public Directions drivingDirection;
	public Double speed;
	public Double shockwaveSpeed;
	public Double maxDens;
	public Short lanes;
	public Boolean internal;
	public LineString geom;
	public Node startNode;
	public Node endNode;
	
	/**
	 * <b>Default constructor</b>. Creates a link with given attributes and creates a new LineString from geom through factory.
	 * @param oid				<i>optional</i> String identifier.
	 * @param id				primary identifier of this.
	 * @param source			origin of this.
	 * @param start				start node of this.
	 * @param end				end node if this.
	 * @param direction			direction in which vehicles can traverse this Link (from or to start node).
	 * @param speed				<i>optional</i> the free flow speed of this in <b>m/s</b>.
	 * @param shockwaveSpeed	<i>optional</i> the backward speed (positive value) of this in <b>m/s</b>.
	 * @param maxDens			<i>optional</i> the maximum density of this in veh/m/lane.
	 * @param lanes				<i>optional</i> the number of lanes on this.
	 * @param internal			<i>optional</i> true of this link is marked as 'internal intersection'
	 * @param geom				the geometry of this, cannot be null, closed or a ring, must have at least two points,
	 * @param factory			used to create a new geometry from geom.
	 * @throws IllegalArgumentException if conditions above are violated.
	 * @throws NullPointerException if any node or geom is null.
	 */
	public Link(String oid, long id, Link.LinkSource source, Node start, Node end,
			Link.Directions direction, Double speed, Double shockwaveSpeed, Double maxDens, Short lanes,
			Boolean internal, LineString geom, GeometryFactory factory) throws IllegalArgumentException, NullPointerException {
		
		if (start == null) {
			throw new NullPointerException("Start node cannot be null.");
		} else if (end == null) {
			throw new NullPointerException("End node cannot be null.");
		} else if (start.id == end.id) {
			throw new IllegalArgumentException("Start and end node cannot be equal.");
		} else if (geom == null) {
			throw new NullPointerException("geom cannot be null.");
		} else if (geom.isClosed() || geom.isRing() || geom.isEmpty() || geom.getNumPoints() < 2) {
			throw new IllegalArgumentException("Invalid geometry.");
		}
		
		this.oid = oid;
		this.id = id;
		this.source = source;
		this.startNode = start;
		this.endNode = end;
		this.drivingDirection = direction;
		this.speed = new Double(speed);
		this.shockwaveSpeed = shockwaveSpeed == null ? null : new Double (shockwaveSpeed);
		this.maxDens = maxDens  == null ? null : new Double(maxDens);
		this.lanes =  lanes == null ? null : new Short(lanes);
		this.internal = internal  == null ? null : new Boolean(internal);
		try {
			this.geom = new LineString(geom.getCoordinateSequence(), factory);
	
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Coulde not create geom from geom: " + ex.getMessage());
		}
	}
	
	/**
	 * <b>Calls</b> default constructor. Creates a link with given attributes and creates a new LineString from geom through factory.
	 * @param oid				<i>optional</i> String identifier.
	 * @param id				primary identifier of this.
	 * @param source			the source of this.
	 * @param start				start node of this.
	 * @param end				end node if this.
	 * @param direction			direction in which vehicles can traverse this Link (from or to start node).
	 * @param speed				<i>optional</i> the free flow speed of this in <b>m/s</b>.
	 * @param lanes				<i>optional</i> the number of lanes on this.
	 * @param internal			<i>optional</i> true of this link is marked as 'internal intersection'
	 * @param geom				the geometry of this, cannot be null, closed or a ring, must have at least two points,
	 * @param factory			used to create a new geometry from geom.
	 * @throws IllegalArgumentException if conditions above are violated.
	 * @throws NullPointerException if any node or geom is null.
	 */
	public Link(String oid, long id, Link.LinkSource source, Node start, Node end,
			Link.Directions direction, Double speed, Short lanes,
			Boolean internal, LineString geom, GeometryFactory factory)
			throws IllegalArgumentException, NullPointerException {

		this(oid, id, source, start, end, direction, speed, null, null, lanes,
				internal, geom, factory);
	}

	/**
	 * Merges this Link with <b>other</b>, updating the start or the end node
	 * depending on how the Links are connected. The geometry of other is added
	 * to the geometry of this. 
	 * 
	 * The tolerance is used to make sure that the geometries are 'connected'.
	 * 
	 * @param other
	 * @param factory
	 * @param tolerance
	 * @return
	 */
	public Link mergeWith(Link other, Node atNode, GeometryFactory factory,
			double tolerance) {
		Coordinate[] c1 = this.geom.getCoordinates();
		Coordinate[] c2 = other.geom.getCoordinates();
		Coordinate[] c3 = new Coordinate[c1.length + c2.length - 1];

		int m = 0;
		int n = 0;
		if (c1[0].distance(c2[0]) <= tolerance) {
			while (n < c2.length) {
				c3[m] = new Coordinate(c2[m].x, c2[m].y);
				n++;
				m++;
			}

			n = c1.length - 1;

			while (n > 0) {
				c3[m] = new Coordinate(c1[n].x, c1[n].y);
				n--;
				m++;
			}
		} else if (c1[0].distance(c2[c2.length - 1]) <= tolerance) {
			while (n < c2.length) {
				c3[m] = new Coordinate(c2[n].x, c2[n].y);
				n++;
				m++;
			}

			n = 1;

			while (n < c1.length) {
				c3[m] = new Coordinate(c1[n].x, c1[n].y);
				n++;
				m++;
			}
		} else if (c1[c1.length - 1].distance(c2[0]) <= tolerance) {
			while (n < c1.length) {
				c3[m] = new Coordinate(c1[n].x, c1[n].y);
				n++;
				m++;
			}

			n = 1;

			while (n < c2.length) {
				c3[m] = new Coordinate(c2[n].x, c2[n].y);
				n++;
				m++;
			}
		} else if ((c1[c1.length - 1].distance(c2[c2.length - 1]) <= tolerance)) {
			while (n < c1.length) {
				c3[m] = new Coordinate(c1[n].x, c1[n].y);
				n++;
				m++;
			}

			n = c2.length - 1;

			while (n > 0) {
				c3[m] = new Coordinate(c2[n].x, c2[n].y);
				n--;
				m++;
			}
		} else {
			return null;
		}

		if (this.endNode.id == atNode.id && this.startNode.id == atNode.id) {
			return new Link(this.oid, this.id, this.source, this.startNode, other.endNode,
					this.drivingDirection, this.speed, this.shockwaveSpeed,
					this.maxDens, this.lanes, this.internal,
					(LineString) factory.createLineString(c3), factory);
		} else {
			return new Link(this.oid, this.id, this.source, other.startNode, this.endNode,
					this.drivingDirection, this.speed, this.shockwaveSpeed,
					this.maxDens, this.lanes, this.internal,
					(LineString) factory.createLineString(c3), factory);
		}
	}

	/**
	 * Checks if the attributes of this link equals those of other. Method
	 * disregards nodes, geometries, driving direction and id/oid.
	 * 
	 * @param other
	 *            the Link to compare with.
	 * @return true if both Links have the same attributes.
	 */
	public boolean propertyEqualTo(Link other) {
		if (this.internal && !other.internal) {
			return false;
		} else if (!this.lanes.equals(other.lanes)) {
			return false;
		} else if (!this.speed.equals(other.speed)) {
			return false;
		} else if (!this.shockwaveSpeed.equals(other.shockwaveSpeed)) {
			return false;
		} else if (!this.maxDens.equals(other.maxDens)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Definition of which possible driving directions a Link can have in regard
	 * to its startNode and endNode.
	 * 
	 * @author Magnus Fransson
	 * @version 0.1
	 */
	public enum Directions {
		FORWARD, BACKWARD, BOTH;
	}

	
	/**
	 * List of supported sources for Links.
	 * 
	 * @author Magnus Fransson
	 * @version 0.1
	 */
	public enum LinkSource {
		NVDB, NAVTEQ, NETWORK, UNKNOWN;
	}
	
	public String toString() {
		String str = "[";

		try {
			str = str + this.id + ";" + this.geom.toText();
		} catch (NullPointerException npe) {
			str = str + this.id + ";" + this.startNode.id + ";"
					+ this.endNode.id + ";" + "null";
		}

		return str + "]";
	}
}