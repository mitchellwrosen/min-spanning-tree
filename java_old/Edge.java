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

/**
 * Edge class
 * 
 * An Edge represents directed edge connecting Node "from" to Node "to" by some
 * weight "weight".
 * 
 * @author Steve Rosen
 */
public class Edge implements Comparable<Edge> {
    private Node from;
    private Node to;
    private int weight;

    /**
     * Edge
     * 
     * This constructor initializes an Edge from Node "from" to Node "to"
     * with weight "weight"
     * 
     * @param   from
     *          the "from" Node for this Edge object
     * 
     * @param   to
     *          the "to" Node for this Edge object
     * 
     * @param   weight
     *          the weight of this Edge object
     */
    public Edge(Node from, Node to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    /**
     * from
     * 
     * Gets the Node from which this Edge points
     * 
     * @return  the Node
     */
    public Node from() {
        return from;
    }

    /**
     * to
     * 
     * Gets the Node to which this Edge points
     * 
     * @return  the Node
     */
    public Node to() {
        return to;
    }

    /**
     * weight
     * 
     * Gets the weight associated with this Edge
     * 
     * @return  the weight
     */
    public int weight() {
        return weight;
    }

    /**
     * equals
     * 
     * Consider two Edges equal if their either "from" Nodes, "to" Nodes, and
     * weights are all equal
     * 
     * @param   o
     *          the Object (Edge) to compare this Edge to
     * 
     * @return  true if the Edges are equal, false if the Edges are not equal
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Edge)) {
            return false;
        }

        Edge e = (Edge) o;
        return weight == e.weight() && from.equals(e.from())
                && to.equals(e.to());

    }

    /**
     * toString
     * 
     * Used to print out the "from" Node, "to" Node, and weight associated with
     * this Edge for debugging purposes
     * 
     * @return  the String
     */
    public String toString() {
        return "Edge from " + from.toString() + " to " + to.toString() + " ("
                + weight + ")";
    }

    /**
     * compareTo
     * 
     * Compare Edges by using... Primary key: "from" Node ordinal value
     * Secondary key: "to" Node ordinal value
     * 
     * @param   e
     *          the Edge that will be compared to this Edge
     * 
     * @return  -1 if this Edge is less than Edge e, 0 if this Edge is equivalent
     *          to Edge e, 1 if this edge is more than Edge e
     */
    public int compareTo(Edge e) {
        if (from.ordinal() == e.from().ordinal()) {
            return to.ordinal() - e.to().ordinal();
        }

        return from.ordinal() - e.from().ordinal();
    }
}
