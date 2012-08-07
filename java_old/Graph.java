/*
 * Copyright (C) 2005 - 2011 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package graphutil;

import java.util.Stack;
import java.util.Vector;
import java.util.Iterator;

/**
 * Graph class
 *
 * A Graph holds a vector of Nodes, and each Node holds its Edges. When a Node
 * is added to this Graph, the Node will be assigned an ordinal value, to be
 * used as a primary key for sorting Nodes, as well as indexing.
 *
 * It is assumed that this Graph will hold a set of connected Nodes (i.e., this
 * is a connected Graph) - if the Nodes in this Graph are not connected, the
 * methods of the GraphOperations abstract class will just return either empty
 * or null results (which can be useful for detecting unconnected sub-Graphs
 * within a main Graph object, for example).
 *
 * @author Steve Rosen
 */
public class Graph {
    private Vector<Node> nodes;

    /**
     * Graph
     *
     * This constructor creates an initial empty vector of Nodes for the Graph
     */
    public Graph() {
        nodes = new Vector<Node>();
    }

    /**
     * Graph
     *
     * This constructor takes an input vector of Path objects (where a Path is
     * simply a vector of Nodes with an associated total weight) - all the Nodes
     * in the vector of Node paths will be added to this Graph - and all the
     * Edges that connect each Node in each Node path will also be added
     * to this Graph.
     *
     * This particular constructor is used by the minPaths method of the
     * GraphOperations abstract class so it can be determined if a set of
     * Paths represent a connected graph.
     *
     * @param   paths
     *          the vector of Path objects to use for constructing this Graph
     */
    public Graph(Vector<Path> paths) {
        nodes = new Vector<Node>();
        Node n1, n2;

        for (Path path : paths) {
            // Attempt to add the first node of the Path to this Graph.
            // Successful add or not, we don't care -- just point n1
            // at the Node that we either just added or simply found
            // in our Graph after an unsuccessful add
            n1 = new Node(path.get(0).object());
            if (!addNode(n1)) {
                n1 = nodes.get(nodes.indexOf(n1));
            }

            // process all the pairs of Nodes in this Path. Implicit in the Path
            // is the fact that there exists an Edge between each two "adjacent"
            // Nodes (adjacent in the vector, anyway). Thus, we must advance two
            // pointers (n1, the previous Node, and n2, the current Node) and
            // construct Edges as we go.
            for (int i = 1; i < path.size(); i++) {
                // Attempt to add current node to this Graph, point n2 at the
                // current Node
                n2 = new Node(path.get(i).object());
                if (!addNode(n2)) {
                    n2 = nodes.get(nodes.indexOf(n2));
                }

                // Because a Path only has associated with it a vector of Nodes
                // and a total weight, we must extract the individual Edge weights
                // from the original Graph
                addEdge(n1, n2, path.get(i).edgeTo(path.get(i - 1)).weight());

                // advance previous Node pointer to current Node pointer
                n1 = n2;
            }
        }
    }

    /**
     * nodes
     *
     * Gets the Vector of Nodes in the Graph
     *
     * @return the Vector of Nodes
     */
    public Vector<Node> nodes() {
        return nodes;
    }

    /**
     * getNode
     *
     * Gets a node from the Graph, specified by the Node's inner Object
     *
     * @param 	o
     *          the Object to search for inside each Node
     *
     * @return  the Node, if the Object exists inside a Node in this Graph null,
     *          if the Object does not exist inside a Node in this Graph
     */
    public Node getNode(Object o) {
        Iterator<Node> iter = nodes.iterator();
        while (iter.hasNext()) {
            Node other = (Node) iter.next();
            if (other.object() == o) {
                return other;
            }
        }
        return null;
    }

    /**
     * getNodeByName
     *
     * Gets a node from the Graph using the node name, specified by the Node's inner Object
     *
     * @param   name
     *          the node name to search for inside each Node
     *
     * @return  the Node, if the node name exists inside a Node in this Graph null,
     *          if the node name does not exist inside a Node in this Graph
     */
    public Node getNodeByName(String name) {
        Iterator<Node> iter = nodes.iterator();
        while (iter.hasNext()) {
            Node other = (Node) iter.next();
            if (other.toString().equals(name)) {
                return other;
            }
        }
        return null;
    }

    /**
     * addNode
     *
     * Adds a Node to this Graph and sets its ordinal value for indexing
     * purposes
     *
     * @param 	n
     *          the Node to add
     *
     * @return	true if the Node was successfully added to the Graph false if the
     *        	Node already exists in the Graph
     */
    public boolean addNode(Node n) {
        if (!nodes.contains(n)) {
            n.setOrdinal(nodes.size());
            nodes.add(n);

            return true;
        }
        return false;
    }

    /**
     * addEdge
     *
     * Adds an Edge to this Graph between Node n1 and Node n2 with a specified
     * weight.
     *
     * @param 	n1
     *        	Node one
     *
     * @param 	n2
     *         	Node two
     *
     * @param 	weight
     *        	The weight of the Edge between Node n1 and Node n2
     */
    public void addEdge(Node n1, Node n2, int weight) {
        // As a fail-safe, if for whatever reason we're trying to add an Edge
        // between two Nodes and one or both of the Nodes aren't yet associated
        // with this Graph, add them to this Graph
        addNode(n1);
        addNode(n2);

        // Because an Edge represents a directed edge, fake like we're an
        // undirected Graph by adding an Edge from n1 to n2 AND from n2 to n1
        n1.addEdge(n2, weight);
        n2.addEdge(n1, weight);
    }

    /**
     * isConnected
     *
     * Determines the connectivity of the Graph. A Graph is considered connected
     * if from EVERY Node there exists some path to every other Node, i.e.,
     * there exist no remote "islands" to which not all Nodes can travel. This
     * can be determined very simply by running a depth-first search starting at
     * ANY Node, and touching Nodes as we explore them. If after only one DFS
     * every Node has been touched, the Graph is connected. Otherwise, it's not.
     *
     * @return  true if this Graph is connected, false if not connected
     */
    public boolean isConnected() {
        boolean touched[] = new boolean[nodes().size()];

        // To do the DFS of our Graph, we'll use a stack - first we'll seed the
        // stack with the first Node in our vector of Nodes (any Node will do,
        // really) - then we'll pop a Node off the stack, touch it, and push all
        // of its unvisited "children" onto the stack, and repeat until the
        // stack
        // is empty
        Stack<Node> nodesToVisit = new Stack<Node>();
        nodesToVisit.add(nodes().get(0));

        while (!nodesToVisit.isEmpty()) {
            Node n = nodesToVisit.pop();
            touched[n.ordinal()] = true;

            // push all unvisited "children" onto the stack
            for (Edge edge : n.edges()) {
                Node child = edge.to();
                if (!touched[child.ordinal()]) {
                    nodesToVisit.push(child);
                }
            }
        }

        // if any Node was left untouched, this Graph is unconnected
        for (boolean b : touched) {
            if (!b) {
                return false;
            }
        }

        // no Node was left untouched - this Graph is connected
        return true;
    }

    /**
     * hasCycle
     *
     * Determines if this Graph has a cycle or not. Traditionally, only a
     * directed Graph can be considered cyclic or acyclic - one could argue that
     * every undirected Graph is cyclic, because it's possible to hop back and
     * forth between any two nodes. This method runs a depth-first search
     * starting at any Node, touching Nodes along the way - if ever we come
     * across a Node that's already been touched, this Graph has a cycle. We
     * don't want to look "backwards" up the tree, however -- that is to say, if
     * Node "A" touches its child, Node "B", we know Node "B" WILL have an Edge
     * pointing back to Node "A", and when Node "B" touches all of ITS children,
     * it's going to find that Node "A" has already been touched and falsely
     * declare this Graph to be cyclic. For that reason, we must keep track of
     * each Node's "parent", and each Node, when touching each of its
     * "children", will skip over its parent.
     *
     * @return  true if this Graph has a cycle false if this Graph does not have
     *          a cycle
     */
    public boolean hasCycle() {
        Node parent[] = new Node[nodes.size()];
        boolean touched[] = new boolean[nodes.size()];

        // To do the DFS of our Graph, we'll use a stack - first we'll seed the
        // stack with the first Node in our vector of Nodes (any Node will do,
        // really) - then we'll pop a Node off the stack, then push all of its
        // children onto the stack (after touching them and setting their
        // parents
        // to the current Node), not counting the current Node's parent, of
        // course. Repeat until the stack is empty, and if ever we find a child
        // that's already been touched, the Graph has a cycle.
        Stack<Node> nodesToVisit = new Stack<Node>();
        nodesToVisit.add(nodes().get(0));

        while (!nodesToVisit.isEmpty()) {
            Node n = nodesToVisit.pop();

            for (Edge edge : n.edges()) {
                Node other = edge.to();

                // take care not to consider this Node's parent one of its
                // children
                if (!(other == (parent[n.ordinal()]))) {
                    // this child's already been touched, so this Graph has a
                    // cycle!
                    if (touched[other.ordinal()]) {
                        return true;
                    }

                    // touch the child, set its parent to this Node, and push it
                    touched[other.ordinal()] = true;
                    parent[other.ordinal()] = n;
                    nodesToVisit.push(other);
                }
            }
        }

        // we never found any already-touched children; this Graph has no cycle
        return false;
    }

    /**
     * findTopNode
     *
     * Used to find the top Node of a Graph, where the top Node
     * is defined to be a Node that just has the fewest Edges and whose
     * ordinal value (from an original Graph) is smaller than
     * any other Node that also has the fewest Edges.
     *
     * @param	oGraph
     *  		The original Graph associated with the Nodes
     *        	in this Graph
     *
     * @return  the top Node
     */
    public Node findTopNode(Graph oGraph) {
        Node topNode = null;
        int leastEdge = Integer.MAX_VALUE;
        for (Node node : nodes()) {
            if (node.edges().size() < leastEdge) {
                topNode = node;
                leastEdge = node.edges().size();
            } else if (node.edges().size() == leastEdge){
                Node gNode = oGraph.getNode(node.object());
                Node gTopNode = oGraph.getNode(topNode.object());
                if (gNode.ordinal() < gTopNode.ordinal()) {
                    topNode = node;
                }
            }
        }

        return topNode;
    }

    /**
     * toString
     *
     * Used to print out the Nodes and Edges in this Graph for debugging purposes
     *
     * @return  the String
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph:\n");
        sb.append("   Nodes:\n");
        for (Node node : nodes) {
            sb.append("      " + node.toString() + "\n");
            sb.append("         Edges:\n");
            for (Edge edge : node.edges()) {
                sb.append("            " + edge.toString() + "\n");
            }
        }

        return sb.toString();
    }
}
