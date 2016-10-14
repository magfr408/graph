package network;

import com.vividsolutions.jts.geom.Coordinate;

@SuppressWarnings("serial")
public class Node extends Coordinate {

	public final String oid;
	public final long id;

	public Node(String oid, long id, double x, double y) {
		super(x, y);
		this.oid = oid;
		this.id = id;
	}

	public String toString() {
		return "[" + this.id + ";(" + Math.round(this.x) + ", "
				+ Math.round(this.y) + ")]";
	}
}
