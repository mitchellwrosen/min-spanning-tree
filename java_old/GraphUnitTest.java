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

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Vector;

/**
 * GraphUnitTest class
 * 
 * This is the main unit test driver for the Graph and GraphOperations
 * classes, which also exercises the associated Node, Edge, and Path classes.
 * 
 * This unit test driver is a regular console Java application, taking its
 * input from a text file that follows a simple format.
 * 
 * Note that in the text input file, blank lines are ignored and comments
 * can be added by simply starting the line with a double-slash ("//").
 *
 * First, the input file indicates a NEW unit test is being defined by simply
 * specifying a special input string, "begin" - an example follows:
 * 
 * 		begin
 *		//==================================================
 *		//	unit test 1
 * 		//==================================================
 * 
 * Second, the input file provides a list of Nodes (by name) - each input name
 * will create a new Node object with that name. The list of input Nodes is
 * then terminated with a special input string, "done" - an example follows:
 *
 * 		// full set of nodes
 *      a
 *      b
 *      c
 *      d
 *      e
 *      done
 * 
 * Third, the input file provides a list of Edges for various pairs of the Nodes
 * that were already created - and each input Edge is given a specified weight
 * (integer values). The list of input Edges is terminated with a special input
 * string, "done" - an example follows:
 *
 * 		// edges for the full set of nodes
 *      a b 1
 *      b c 1
 *      a d 1
 *      d e 1
 *      e c 1
 *      done
 * 
 * It is assumed that the above set of defined Edges will define a fully
 * connected Graph - i.e., it must be possible to get from ANY Node in the Graph
 * to any OTHER Node through SOME path of Nodes.
 * 
 * Fourth, the input file provides a list of input Nodes that need to be
 * connected together with the minimum set of Nodes from the original list of
 * Nodes. The list of input Nodes is terminated with a special input string,
 * "done" - an example follows:
 *
 * 		// list of input nodes
 *      a
 *      c
 *      d
 *      done
 *      
 * Fifth, and last, the input file provides a list of EXPECTED minimum Paths
 * that will be needed to successfully connect all the input Nodes together.
 * The list of EXPECTED minimum Paths is terminated with a special input
 * string, "done" - an example follows:
 *
 * 		// list of expected minPaths
 * 		c b a
 * 		a d
 * 		done	
 * 
 * This unit test driver will then use the provided list of Nodes and list of
 * Edges to construct a new Graph object.
 * 
 * The newly created Graph object will be used with the list of input Nodes to
 * find the minimum set of Node paths needed to connect all the input Nodes
 * together by calling the "minPaths" method of the GraphOperations abstract
 * class.
 * 
 * If for some reason, the defined set of Edges for the Graph do NOT create a
 * connected Graph, then the "minPaths" method of the GraphOperations abstract
 * class will output an error message to let the user know they have not defined
 * a fully connected Graph object.
 * 
 * As a final verification step, this unit test driver will then use the provided
 * list of expected minimum paths to make sure the derived list of minimum paths
 * is correct.
 * 
 * The text input file can have as many unit tests as the user desires, as long
 * as each "unit test section" starts with the special line "begin" and is 
 * followed by the 4 parts of the unit test (full list of nodes, edges between
 * the nodes, the list of input nodes, and the list of expected minimum paths),
 * each part terminated by the special line "done".
 *
 * @author Steve Rosen
 */

public class GraphUnitTest {

    /**
     * main
     * 
     * The main unit test driver for the graph utility
     * 
     * @param   args
     *          the command line arguments to this unit test driver
     *          
     * @return  nothing
     */
    public static void main(String[] args) {
        // create a BufferedReader to read in our graph test input file
        try {
        	String inputFile = "graphtest.txt";
        	if (args.length > 0) {
        		inputFile = args[0];
        	}
            Scanner fileScanner = new Scanner(new FileReader(inputFile));

            // process all the unit tests in this graph test input file...
            int passedUnitTests = 0;
            int failedUnitTests = 0;
            int unitTestNum = 0;
            while (fileScanner.hasNextLine()) {
            	Scanner lineScanner = new Scanner(fileScanner.nextLine());
                if (lineScanner.hasNext()) {
                    String nextToken = lineScanner.next();
                    if (nextToken.equals("begin")) {
                        // process the next unit test...
                        if (processUnitTest(fileScanner, ++unitTestNum)) {
                        	passedUnitTests++;
                        } else {
                        	failedUnitTests++;
                        }
                    }
                }
            }
            
            // print out the final stats
            int totalUnitTests = passedUnitTests + failedUnitTests;
            System.out.println("");
            System.out.println("==================================================");
            System.out.println("Total number unit test run : " + totalUnitTests);
            System.out.println("   Unit tests that PASSED  : " + passedUnitTests);
            System.out.println("   Unit tests that FAILED  : " + failedUnitTests);
            System.out.println("==================================================");
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
    }

    /**
     * processUnitTest
     * 
     * Processes a unit test from the graph test input file
     * 
     * @param   fileScanner
     *          the Scanner for reading in each line from the input file
     *          
     * @param	unitTestNum
     * 			the unit test number of the input file being processed
     */
    private static boolean processUnitTest(Scanner fileScanner, int unitTestNum) {
        // we have a new unit test to process - show the user which one it is...
        System.out.println("");
        System.out.println("//==========================================================================");
        System.out.println("// Processing Graph Unit Test # " + unitTestNum);
        System.out.println("//==========================================================================");

        // create a new Graph object that has no Nodes and no Edges
        Graph graph = new Graph();

        // allocate some space for our input Nodes and expected output Nodes
        ArrayList<Node> inputNodes = new ArrayList<Node>();
        Vector<Path> expectedMinPaths = new Vector<Path>();
        
        // get all the Nodes, all the Edges, the input Nodes, and the expected
        // output Nodes for this Graph
        getAllNodes(graph, fileScanner);
        getAllEdges(graph, fileScanner);
        inputNodes = getInputNodes(graph, fileScanner);
        expectedMinPaths = getExpectedMinPaths(graph, fileScanner);
        
        // print input node paths
        System.out.println("");
        System.out.println("All input node paths:");
        for (Vector<Path> paths : GraphOperations.getInputNodePaths(graph,
                inputNodes)) {
            for (Path path : paths) {
                System.out.println(path);
            }
            System.out.println("---");
        }

        // print final set of required input node paths
        System.out.println("");
        System.out.println("Paths used to connect all the input Nodes:");
        long time = System.currentTimeMillis();
        Vector<Path> minPaths = GraphOperations.minPaths(graph, inputNodes);
        time = System.currentTimeMillis() - time;
        for (Path path : minPaths) {
            System.out.println(path);
        }
        System.out.println("(paths found in " + time + " milliseconds)");
        
        // verify the results of running this unit test
        System.out.println("");
        System.out.println("Verifying results...");
        boolean actualMinPathsOkay = true;
        if ( minPaths.size() != expectedMinPaths.size() ) {
            System.out.println("...BUMMER! The expectedMinPaths != minPaths. This unit test has FAILED!");
            System.out.println("...minPaths size = " + minPaths.size() + ", expectedMinPaths size = " + expectedMinPaths.size());
        } else {
            for (Path minPath : minPaths) {
                int minPathIndex = minPaths.indexOf(minPath);
                Path expectedMinPath = expectedMinPaths.get(minPathIndex);
                String minPathStr = minPath.toStringWithoutWeight();
                String expectedMinPathStr = expectedMinPath.toStringWithoutWeight();
                if ( !minPathStr.equals(expectedMinPathStr )) {
                    System.out.println("...BUMMER! The expectedMinPaths != minPaths. This unit test has FAILED!");
                    System.out.println("...expectedMinPath = " + expectedMinPathStr + ", minPath = " + minPathStr);
                    actualMinPathsOkay = false;
                    break;
                } else {
                    System.out.println("   ...expectedMinPath = '" + expectedMinPathStr + "', minPath = '" + minPathStr + "'");                	
                }
            }
            if (actualMinPathsOkay) {
                System.out.println("...COOL! The expectedMinPaths == minPaths. This unit test has PASSED!");
            }
        }
        
        return actualMinPathsOkay;
    }

    /**
     * getAllNodes
     * 
     * Gets all the Nodes for a Graph object from the graph test input file
     * 
     * @param   graph
     *          the Graph to add all the Nodes to
     * 
     * @param   fileScanner
     *          the Scanner for reading in each line from the input file
     */
    private static void getAllNodes(Graph graph, Scanner fileScanner) {
        // show the tester what we are about to process...
        System.out.println("");
        System.out.println("//----------------------");
        System.out.println("// The full set of Nodes");
        System.out.println("//----------------------");

        boolean allDone = false;
        while (!allDone) {
            if (fileScanner.hasNextLine()) {
                Scanner lineScanner = new Scanner(fileScanner.nextLine());
                if (lineScanner.hasNext()) {
                    String nextToken = lineScanner.next();
                    if (nextToken.equals("done")) {
                        allDone = true;
                        break;
                    } else if (nextToken.startsWith("//")) {
                        continue;
                    } else {
                        // create a new mock dataset object and add it as a new
                        // Node to the Graph
                        graph.addNode(new Node(new MockDataSet(nextToken)));
                        System.out.println(nextToken);
                    }
                }
            }
        }
    }

    /**
     * getAllEdges
     * 
     * Gets all the Edges for a Graph object from the graph test input file
     * 
     * @param   graph
     *          the Graph to add all the Edges to
     * 
     * @param   fileScanner
     *          the Scanner for reading in each line from the input file
     */
    private static void getAllEdges(Graph graph, Scanner fileScanner) {
        // show the tester what we are about to process...
        System.out.println("");
        System.out.println("//--------------------------------");
        System.out.println("// Edges for the full set of Nodes");
        System.out.println("//--------------------------------");

        boolean allDone = false;
        while (!allDone) {
            if (fileScanner.hasNextLine()) {
                Scanner lineScanner = new Scanner(fileScanner.nextLine());
                if (lineScanner.hasNext()) {
                    String nextToken = lineScanner.next();
                    if (nextToken.equals("done")) {
                        allDone = true;
                        break;
                    } else if (nextToken.startsWith("//")) {
                        continue;
                    } else {
                        // if the "from" Node is not yet in the Graph, then add
                        // it as a convenience
                        String fromStr = nextToken;
                        Node fromNode;
                        if (null == (fromNode = graph.getNodeByName(fromStr))) {
                        	MockDataSet mockDS = new MockDataSet(fromStr);
                            graph.addNode(new Node(mockDS));
                            fromNode = graph.getNode(mockDS);
                        }

                        // if the "to" Node is not yet in the Graph, then add it
                        // as a convenience
                        String toStr = lineScanner.next();
                        Node toNode;
                        if (null == (toNode = graph.getNodeByName(toStr))) {
                        	MockDataSet mockDS = new MockDataSet(toStr);                        	
                            graph.addNode(new Node(mockDS));
                            toNode = graph.getNode(mockDS);
                        }

                        // add an Edge to the Graph for the "from -> to" Node pair -
                        // and note that the Graph object will actually create the 2 Edges for
                        // the pair of Nodes and add them to the respective Node objects...
                        int weight = lineScanner.nextInt();
                        graph.addEdge(fromNode, toNode, weight);
                        System.out.println(fromStr + " " + toStr + " " + weight);
                    }
                }
            }
        }
    }

    /**
     * getInputNodes
     * 
     * Gets all the input Nodes for a Graph object from the graph test input file
     * 
     * @param   graph
     *          the Graph to search for all the input Nodes
     * 
     * @param   fileScanner
     *          the Scanner for reading in each line from the input file
     *          
     * @return	the list of input Nodes to search for in the Graph
     */
    private static ArrayList<Node> getInputNodes(Graph graph, Scanner fileScanner) {
        // show the tester what we are about to process...
        System.out.println("");
        System.out.println("//------------------------");
        System.out.println("// The list of input Nodes");
        System.out.println("//------------------------");

        ArrayList<Node> inputNodes = new ArrayList<Node>();
        boolean allDone = false;
        while (!allDone) {
            if (fileScanner.hasNextLine()) {
                Scanner lineScanner = new Scanner(fileScanner.nextLine());
                if (lineScanner.hasNext()) {
                    String nextToken = lineScanner.next();
                    if (nextToken.equals("done")) {
                        allDone = true;
                        break;
                    } else if (nextToken.startsWith("//")) {
                        continue;
                    } else {
                        // add another input Node to the list of input Nodes
                        inputNodes.add(graph.getNodeByName(nextToken));
                        System.out.println(nextToken);
                    }
                }
            }
        }
        return inputNodes;
    }

    /**
     * getExpectedMinPaths
     * 
     * Gets all the expected minPaths for a unit test from the input text file.
     * 
     * @param   graph
     *          the Graph that holds all the Nodes that were read from
     *          the input text file
     * 
     * @param   fileScanner
     *          the Scanner used to read from the input text file
     *          
     * @return  nothing
     */
    private static Vector<Path> getExpectedMinPaths(Graph graph, Scanner fileScanner) {
        System.out.println("");
        System.out.println("//--------------------------------");
        System.out.println("// The vector of expected minPaths");
        System.out.println("//--------------------------------");

        Vector<Path> expectedMinPaths = new Vector<Path>();
        boolean allDone = false;
        while (!allDone) {
            if (fileScanner.hasNextLine()) {
                Scanner lineScanner = new Scanner(fileScanner.nextLine());
                if (lineScanner.hasNext()) {
                    String nextToken = lineScanner.next();
                    if (nextToken.equals("done")) {
                        allDone = true;
                        break;
                    } else if (nextToken.startsWith("//")) {
                        continue;
                    } else {
                        String firstNodeInPath = nextToken;
                        Path minPath = new Path( new Node(firstNodeInPath));
                        
                        while ( lineScanner.hasNext() ) {
                            String nextNodeInPath = lineScanner.next();
                            minPath.addNode(new Node(nextNodeInPath), 1);
                        }
                        expectedMinPaths.add(minPath);
                        System.out.println(minPath);
                    }
                }
            }
        }
        return expectedMinPaths;
    }
}
