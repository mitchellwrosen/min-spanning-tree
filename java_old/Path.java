/*
 * Copyright (C) 2005 - 2011 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package graphutil;

import java.util.Vector;

/**
 * Path class
 * 
 * This object is used by the main GraphOperations utility - a Path object
 * is simply a vector of Node objects that contain all the Nodes between
 * a source Node (the first Node in the vector) and a destination Node (the
 * last Node in the vector).
 * 
 * A Path object can be constructed with an initial starting Node OR constructed
 * with another Path object.
 * 
 * @author Steve Rosen
 */
public class Path implements Comparable<Path> {
    private Vector<Node> nodes;
    private int weight;
    private int ordinal;

    /**
     * Path
     * 
     * This constructor creates an initial Path object with a
     * starting Node and established an initial weight for this
     * Path as 0 (since it only has the one Node)
     * 
     * @param   node
     *          the starting Node for this Path
     */
    public Path(Node node) {
        nodes = new Vector<Node>();
        nodes.add(node);
        weight = 0;
        ordinal = -1;
    }

    /**
     * Path
     * 
     * This constructor creates a Path object from another Path object,
     * which is useful when iteratively building up a new Path from
     * some source Node to some final destination Node
     * 
     * @param   path
     *          the Path to use for initializing this Path
     */
    public Path(Path path) {
        nodes = new Vector<Node>();
        for (Node node : path.nodes()) {
            nodes.add(node);
        }
        weight = path.weight();
    }

    /**
     * nodes
     * 
     * Gets the vector of Nodes associated with this Path
     * 
     * @return  the vector of Nodes
     */
    public Vector<Node> nodes() {
        return nodes;
    }

    /**
     * weight
     * 
     * Gets the weight associated with this Path
     * 
     * @return  the weight
     */
    public int weight() {
        return weight;
    }

    /**
     * ordinal
     * 
     * Gets the ordinal associated with this Path (for indexing purposes)
     * 
     * @return  the ordinal
     */
    public int ordinal() {
        return ordinal;
    }

    /**
     * setOrdinal
     * 
     * Sets the ordinal associated with this Path (for indexing purposes)
     * 
     * @param   ordinal
     *          the ordinal value to set
     */
    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    /**
     * addNode
     * 
     * Adds a new Node with a specified weight to this Path
     * 
     * @param   node
     *          the Node to add
     * 
     * @param   weight
     *          the weight of this new Node
     */
    public void addNode(Node node, int weight) {
        nodes.add(node);
        this.weight += weight;
    }

    /**
     * lastNode
     * 
     * Gets the last Node in this Path
     * 
     * @return  the last Node
     */
    public Node lastNode() {
        return nodes.lastElement();
    }

    /**
     * getNode
     * 
     * Gets a Node of a specified index in this Path
     * 
     * @param   index
     *          the Node index to get in this Path
     * 
     * @return  the Node
     */
    public Node get(int index) {
        return nodes.get(index);
    }

    /**
     * size
     * 
     * Gets the size of all the Nodes in this Path
     * 
     * @return  the size
     */
    public int size() {
        return nodes.size();
    }

    /**
     * contains
     * 
     * Whether or not a specified Node is in this Path
     * 
     * @param   node
     *          the Node to search for in this Path
     * 
     * @return  true if the Node is in this Path; otherwise false
     */
    public boolean contains(Node node) {
        return nodes.contains(node);
    }

    /**
     * equals
     * 
     * Consider two Paths equal if their respective sizes
     * are equal and their respective Nodes are equal
     * 
     * @param   o
     *          the Object (Path) to compare this Path to
     * 
     * @return  true if the objects are equal, false if the objects are not equal
     */
    public boolean equals(Object o) {
        if (!(o instanceof Path)) {
            return false;
        }
        
        Path p = (Path) o;
        
        if (nodes.size() != p.nodes().size()) {
            return false;
        }
        
        if (weight != p.weight()) {
            return false;
        }
        
        for (int i = 0; i < nodes.size(); i++) {
            if (!nodes.get(i).equals(p.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * toString
     * 
     * Used to print out the Nodes in this Path for debugging purposes
     * 
     * @return  the String
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Path: ");
        for (Node node : nodes) {
            sb.append(node.toString() + " ");
        }
        sb.append("(" + weight + ")");

        return sb.toString();
    }

    /**
     * toStringWithoutWeight
     * 
     * Used to print out the Nodes in this Path for verification purposes
     * 
     * @return  the String
     */
    public String toStringWithoutWeight() {
        StringBuilder sb = new StringBuilder();
        sb.append("Path:");
        for (Node node : nodes) {
            sb.append(" " + node.toString());
        }

        return sb.toString();
    }

    /**
     * compareTo
     * 
     * Compare Paths by using... the weight of each Path
     * 
     * @param   path
     *          the Path that will be compared to this Path
     * 
     * @return  the difference between the weight of this Path and 
     *          the weight of Path p
     */
    public int compareTo(Path path) {
        return weight - path.weight();
    }
}
