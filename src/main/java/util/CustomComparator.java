package util;

import java.math.BigDecimal;
import java.util.Comparator;

import network.Node;

public class CustomComparator implements Comparator<Node> {

	public int compare(Node A, Node B) {

			BigDecimal a = BigDecimal.valueOf(A.id);
			BigDecimal b = BigDecimal.valueOf(B.id);

			return a.compareTo(b);
	}
}