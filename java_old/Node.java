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

import java.util.ArrayList;
import java.util.Collections;

/**
 * Node class
 * 
 * This object is used by the main Graph object - a Node object is associated
 * to some other Node via an Edge object. It is possible for a Node to have
 * more than one Edge if that Node is connected to more than one other Node -
 * so a Node will hold a list of Edges.
 * 
 * A Node object is identified by its name and can be assigned an ordinal value
 * for indexing purposes (indexing into a matrix of path costs between Nodes,
 * for example).
 * 
 * @author Steve Rosen
 */
public class Node implements Comparable<Node> {
    private Object object;
    private ArrayList<Edge> edges;
    private int ordinal;

    /**
     * Node
     * 
     * This constructor associates an object with this Node and
     * creates an empty list of Edges for this Node
     * 
     * @param   object
     *          the Object associated with this Node
     */
    public Node(Object object) {
        this.object = object;
        edges = new ArrayList<Edge>();
        ordinal = -1;
    }

    /**
     * object
     * 
     * Gets the object associated with this Node
     * 
     * @return  the object
     */
    public Object object() {
        return object;
    }

    /**
     * edges
     * 
     * Gets the list of Edges attached to this Node
     * 
     * @return  the list of Edges
     */
    public ArrayList<Edge> edges() {
        return edges;
    }

    /**
     * ordinal
     * 
     * Gets the ordinal associated with this Node (for indexing purposes)
     * 
     * @return  the ordinal
     */
    public int ordinal() {
        return ordinal;
    }

    /**
     * setOrdinal
     * 
     * Sets the ordinal associated with this Node (for indexing purposes)
     * 
     * @param   num
     *          the ordinal value to set
     */
    public void setOrdinal(int num) {
        ordinal = num;
    }

    /**
     * addEdge
     * 
     * Adds an Edge to this Node with a specified weight
     * 
     * @param   toNode
     *          the Node this Edge is attached to
     * 
     * @param   weight
     *          the weight of this new Edge
     *          
     * @return  true if the Node is in this Path; otherwise false
     */
    public boolean addEdge(Node toNode, int weight) {
        Edge toAdd = new Edge(this, toNode, weight);

        // we can only add a new Edge to this Node if we don't
        // already have an Edge with the specified Node
        if (!edges.contains(toAdd)) {
            edges.add(toAdd);
            Collections.sort(edges);
            return true;
        }

        // we did not add this Edge because we already have
        // an Edge with the specified Node
        return false;
    }

    /**
     * edgeTo
     * 
     * Returns the Edge to a specified Node, if one exists
     * 
     * @param   n
     *          the Node to search for
     *          
     * @return  the Edge, if the specified Node can be found as the 'to' Node
     *          for any of the Edges; null, if the specified Node cannot be found
     */
    public Edge edgeTo(Node n) {
        // search through all the Edges for this Node, looking
        // for a match (where a match is based on the names of
        // the Nodes matching)
        for (Edge edge : edges) {
            if (edge.to().equals(n)) {
                // we found a match, so return this Edge
                return edge;
            }
        }

        // no matching Edge was found for the specified Edge,
        // so we just return null
        return null;
    }

    /**
     * equals
     * 
     * Consider two Nodes equal if their objects are equal
     * 
     * @param   o
     *          the Object (Node) to compare this Node to
     * 
     * @return  true if the objects are equal, false if the objects are not equal
     */
    public boolean equals(Object o) {
        if (!(o instanceof Node)) {
            return false;
        }

        return object == ((Node) o).object();
    }

    /**
     * toString
     * 
     * Used to print out the Node for debugging purposes
     * 
     * @return  the String
     */
    public String toString() {
        return object.toString();
    }

    /**
     * compareTo
     * 
     * Compare Nodes by using... their respective ordinal values
     * 
     * @param   n
     *          the Node that will be compared to this Node
     * 
     * @return  this.ordinal() - n.ordinal()
     */
    public int compareTo(Node n) {
        return this.ordinal() - n.ordinal();
    }
}
