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
 * MockDataSet class
 * 
 * This object is used by the GraphUnitTest class to simulate a DataSet object
 * that will be the true 'client' of this Graph utility.  In the Graph utility,
 * a MockDataSet will be the Nodes of a Graph that are used to find the minimum
 * set of Paths needed to connect an input set of MockDataSet objects.
 * 
 * @author Steve Rosen
 */
public class MockDataSet {
    private String name;

    /**
     * MockDataSet
     * 
     * This constructor creates a MockDataSet object with a specified name
     * 
     * @param   name
     *          the name of this MockDataSet object
     */
    public MockDataSet(String name) {
        this.name = name;
    }

    /**
     * name
     * 
     * Gets the name of this MockDataSet object
     * 
     * @return  the name
     */
    public String name() {
        return name;
    }

    /**
     * toString
     * 
     * Used to print out the name of this MockDataSet for debugging purposes
     * 
     * @return  the String
     */
    public String toString() {
        return name;
    }

    /**
     * equals
     * 
     * Consider two MockDataSets equal if their names are equal
     * 
     * @return  true if the names are equal, false if the names are not equal
     */
    public boolean equals(Object o) {
        if (!(o instanceof MockDataSet)) {
            return false;
        }

        return name.equals(((MockDataSet) o).name());
    }
}
