package org.openrdf.repository.object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Sparql;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;

public class CollectionTest extends ObjectRepositoryTestCase {
	public static final String NS = "urn:test:";
	public static final String PREFIX = "PREFIX ex:<" + NS + ">\n";

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(CollectionTest.class);
	}

	@Iri(NS + "Node")
	public interface Node {
		@Iri(NS + "child")
		Set<Node> getChildren();

		@Iri(NS + "child")
		void setChildren(Set<Node> children);

		@Iri(NS + "position")
		Integer getPosition();

		@Iri(NS + "position")
		void setPosition(Integer position);

		@Sparql(PREFIX
				+ "SELECT ?child { $this ex:child ?child . ?child ex:position ?position }\n"
				+ "ORDER BY ?position")
		List<Node> getOrderedChildren();

		List<Node> getSortedChildren();
	}

	public static abstract class NodeSupport implements Node {
		public List<Node> getSortedChildren() {
			Set<Node> live = getChildren();
			List<Node> memory = new ArrayList<Node>(live);
			Collections.sort(memory, new Comparator<Node>() {
				public int compare(Node o1, Node o2) {
					Integer p1 = o1.getPosition();
					Integer p2 = o2.getPosition();
					if (p1 == p2)
						return 0;
					if (p1 == null)
						return -1;
					if (p2 == null)
						return 1;
					return p1.compareTo(p2);
				}
			});
			return memory;
		}
	}

	@Override
	public void setUp() throws Exception {
		config.addConcept(Node.class);
		config.addBehaviour(NodeSupport.class);
		super.setUp();
	}

	public void testUnorderedCollection() throws Exception {
		Node n0 = con.addDesignation(con.getObject("urn:test:n0"), Node.class);
		Node n1 = con.addDesignation(con.getObject("urn:test:n1"), Node.class);
		Node n2 = con.addDesignation(con.getObject("urn:test:n2"), Node.class);
		Node n3 = con.addDesignation(con.getObject("urn:test:n3"), Node.class);
		Node n4 = con.addDesignation(con.getObject("urn:test:n4"), Node.class);
		Node n5 = con.addDesignation(con.getObject("urn:test:n5"), Node.class);
		Node n6 = con.addDesignation(con.getObject("urn:test:n6"), Node.class);
		Node n7 = con.addDesignation(con.getObject("urn:test:n7"), Node.class);
		Node n8 = con.addDesignation(con.getObject("urn:test:n8"), Node.class);
		Node n9 = con.addDesignation(con.getObject("urn:test:n9"), Node.class);
		Node n10 = con
				.addDesignation(con.getObject("urn:test:n10"), Node.class);
		n1.setPosition(1);
		n2.setPosition(2);
		n3.setPosition(3);
		n4.setPosition(4);
		n5.setPosition(5);
		n6.setPosition(6);
		n7.setPosition(7);
		n8.setPosition(8);
		n9.setPosition(9);
		n10.setPosition(10);
		n0.getChildren().add(n10);
		n0.getChildren().add(n9);
		n0.getChildren().add(n8);
		n0.getChildren().add(n7);
		n0.getChildren().add(n6);
		n0.getChildren().add(n5);
		n0.getChildren().add(n4);
		n0.getChildren().add(n3);
		n0.getChildren().add(n2);
		n0.getChildren().add(n1);
		assertEquals(Arrays.asList(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10),
				n0.getSortedChildren());
		assertEquals(Arrays.asList(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10),
				n0.getOrderedChildren());
	}

}
