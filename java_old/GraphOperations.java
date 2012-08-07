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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;

/**
 * GraphOperations class
 *
 * This is an abstract class with static methods to do operations on a Graph
 * object. The principal method is minPaths, which takes a Graph and an
 * ArrayList of input Nodes and returns a vector of Paths representing the
 * minimum set of Paths that connect each input Node to each other input Node.
 *
 * @author Steve Rosen
 */
public abstract class GraphOperations {
  static final int INFINITY = Integer.MAX_VALUE;

  /**
   * minPaths
   *
   * Returns the minimum set of Paths required to connect each input Node to
   * each other input Node, possibly (and probably) including extra Nodes not
   * in the input set of Nodes, but required to construct a connected Graph.
   *
   * Here are the key steps of the algorithm:
   *
   * 1. Given a Graph and an array of input Nodes, generate a complete list of
   * shortest Paths from each input Node to every other input Node. The total
   * number of such Paths is (n)(n-1)/2, plus a variable amount of "alternate"
   * paths.
   *
   * Consider an input array of Nodes W, X, Y, and Z. Required is the shortest
   * Path from W to X, W to Y, W to Z, X to Y, X to Z, and Y to Z. So, a
   * complete list of shortest Paths from each input Node to each other input
   * Node for an array of 4 input Nodes is 3 + 2 + 1 paths, or (3)(3-1)/2.
   * However, there may be MORE THAN ONE Path from, say, W to Z, i.e. an
   * "alternate route" of equal Path weight, and these Paths must be
   * considered as well.
   *
   * 2. Sort these Paths by increasing weight (with the Node ordinal as the
   * secondary key).
   *
   * 3. Iterate through the sorted Paths. For each Path, add it to the minimum
   * set of Paths UNTIL we discover that the set of Paths contains ALL of our
   * input Nodes AND all the Nodes in that set of Paths are in a fully
   * connected Graph.
   *
   * 4. Make sure that while we iterate through the sorted Paths, we evaluate
   * ALL the sets of Paths because there might be more than one shortest Path
   * between 2 different Nodes - we use the WEIGHT of those apparently "equal"
   * Paths to decide which shortest Path is the correct one.
   *
   * 5. As a convenience to the caller, we do a little post-processing of our
   * resulting minimum set of Paths - we find the Path that has the TOP Node
   * and move it to be the FIRST Path in the returned set of minimum Paths -
   * this is done strictly as a convenience, and does not affect the results.
   * We also make sure that TOP Node of that FIRST Path is the START Node of
   * the Path (so, in case that Node happens to be the END Node of the Path,
   * we simply reverse the Nodes of the Path) - again, this is strictly for
   * the convenience of the caller and does not affect the results.
   *
   * @param g
   *            the specified Graph that will be searched
   *
   * @param inputNodes
   *            the specified list of input Nodes to search for
   *
   * @return the minimum vector of Paths for a Graph with a specified list of
   *         input Nodes that connects all of the input Nodes
   */
  public static Vector<Path> minPaths(Graph g, ArrayList<Node> inputNodes) {
    Vector<Path> minPaths = new Vector<Path>();
    int minPathsWeight = Integer.MAX_VALUE;

    // handle the degenerate cases (inputNodes is null, or
    // the size of inputNodes is either 0 or 1)
    if (inputNodes == null) {
      return null;
    } else if (inputNodes.size() == 0) {
      return minPaths;
    } else if (inputNodes.size() == 1) {
      Path oneNodePath = new Path(inputNodes.get(0));
      minPaths.add(oneNodePath);
      return minPaths;
    }

    // sort the inputNodes according to their ordinal values so we
    // always create a consistent set of output results for minPath
    Collections.sort(inputNodes);

    // get all the shortest paths for every input Node to every other
    // input Node and then sort the returned vector of vector of Paths
    // by the weight of each vector of Paths (which will be the sum of
    // the weights of all the Nodes that comprise each Path object)
    Vector<Vector<Path>> inputNodePaths = getInputNodePaths(g, inputNodes);
    Collections.sort(inputNodePaths, new Comparator<Vector<Path>>() {
      public int compare(Vector<Path> vecPath1, Vector<Path> vecPath2) {
        return vecPath1.get(0).compareTo(vecPath2.get(0));
      }
    });

    // now that we have sorted all the vectors of vectors of Paths, we
    // need to assign the ordinal values for each vector of Paths - note
    // that for one vector of Paths, the weight of each Path in the
    // vector of Paths will be the same (since they all belong to the
    // same group - or vector - of Path vectors)
    int ordinal = 0;
    for (Vector<Path> paths : inputNodePaths) {
      for (Path path : paths) {
        path.setOrdinal(ordinal);
      }
      ordinal++;
    }

    // now comes the main meat of the logic for this algorithm -
    // we will use a stack-based approach to iterate through
    // all the vectors of Path vectors (remember - they are
    // ordered from shortest to longest now) - we are trying to
    // find the best (shortest) set of Paths that contain ALL of
    // our input Nodes AND all the Nodes in that set of Paths
    // are in a fully connected Graph...

    // first, we create a new stack - it will be a stack of
    // vectors of Paths - and we seed it with the all the
    // Paths in the first vector of Paths
    Stack<Vector<Path>> stack = new Stack<Vector<Path>>();
    for (int i = inputNodePaths.get(0).size(); i > 0; i--) {
      Vector<Path> newVec = new Vector<Path>();
      newVec.add(inputNodePaths.get(0).get(i - 1));
      stack.push(newVec);
    }

    System.out.println("...minPaths => START finding all the shortest paths");

    // now we continue to process our stack until it has been
    // emptied out - when we pop off a vector of Paths from
    // the top of the stack (we call it our current Paths), we
    // check to see if all our input Nodes are in this vector
    // of current Paths - if so, the then verify that those
    // Nodes in the current Paths vector are in a fully connected
    // Graph - and if THAT is also true, we get the weight of the
    // vector of current Paths and compare it to our current
    // minimum Paths weight - if it is smaller (shorter), we
    // set a NEW minimum Paths weight and also set our current
    // minimum Paths (the vector of Paths that we are returning)
    // to this current Paths vector.
        int numTotalIterations = 0;
    Vector<Path> currPaths;
    Graph currGraph;
    while (!stack.isEmpty()) {

          numTotalIterations++;
          currPaths = stack.pop();
      currGraph = new Graph(currPaths);

      // see if all our input Nodes are in the current Paths
      // vector that was just popped off the top of our stack -
      // and also check to see if all the Nodes in the current
      // Paths vector are in a fully connected Graph
      if (allNodesInPathVec(inputNodes, currPaths)
          && currGraph.isConnected()) {
        // we have a potentially better vector of Paths to
        // return to the caller
        int currPathsWeight = getPathsWeight(currPaths);
        if (currPathsWeight < minPathsWeight) {
          // we have a winner! this current Path vector is the
          // best (shortest) so far - so save off the new
          // minimum Paths weight and new minimum set of Paths
          minPathsWeight = currPathsWeight;
          minPaths = currPaths;
        }
      } else {
        // either we don't have all the input Nodes in our current
        // vector of Paths or we have all the input Nodes, but the
        // Nodes in the current vector of Paths is NOT in a fully
        // connected Graph - so we need to add the next vector of
        // Paths to our current vector of Paths (in reverse order
        // that they sit in the vector) and the push this new vector
        // of Paths onto our stack so it can be popped off and then
        // evaluated...
        Vector<Path> nextPaths = inputNodePaths.get(currPaths.get(
            currPaths.size() - 1).ordinal() + 1);
        for (int i = nextPaths.size(); i > 0; i--) {
          Vector<Path> newPaths = new Vector<Path>();
          for (Path path : currPaths) {
            newPaths.add(path);
          }
          newPaths.add(nextPaths.get(i - 1));
          stack.push(newPaths);
        }
      }
    }

        System.out.println("...minPaths => END finding all the shortest paths, numTotalIterations = " + numTotalIterations);

    // we need to find the top Node in our minimum vector of Paths -
    // to do this, we toss all the Nodes of our minPaths into a new
    // Graph and then ask that Graph for a "terminal" Node that has
    // the fewest number of Edges in it (presumably, just 1)...
    Graph minPathsGraph = new Graph(minPaths);
    Node topNode = minPathsGraph.findTopNode(g);

    // now that we have the top Node, we need to move that Path with that
    // top Node to be the FIRST Path of minPaths - and make sure that the
    // top Node in that FIRST Path is also the FIRST Node of that Path
    // (which might mean reversing the Nodes in that Path)
    int pathIndexWithTopNode = 0;
    for (Path path : minPaths) {
      if (path.contains(topNode)) {
        pathIndexWithTopNode = minPaths.indexOf(path);
        if (path.nodes().indexOf(topNode) != 0) {
          Collections.reverse(path.nodes());
        }
        break;
      }
    }

    // if the path in minPaths is NOT the first path in the list, we need
    // to move it to be that first path of the list...
    if (minPaths.size() > 1) {
      if (pathIndexWithTopNode > 0) {
        Path pathWithTopNode = new Path(
            minPaths.get(pathIndexWithTopNode));
        minPaths.removeElementAt(pathIndexWithTopNode);
        minPaths.insertElementAt(pathWithTopNode, 0);
      }
    }

    // success! we have found our minimum vector of Paths needed to
    // connect all of our input Nodes to one another - so let's return
    // it to the caller...
    return minPaths;
  }

  /**
   * getInputNodePaths
   *
   * This method is the heart of the key logic needed to efficiently determine
   * the best set of minimum (shortest or least costly) Paths needed to fully
   * connect a list of input Nodes.
   *
   * The basic approach in this algorithm is to iterate through all the input
   * Nodes - and for each input Node, find the shortest possible Path to every
   * other input Node (again, using an iterative approach that uses a stack) -
   * as each possible Path is discovered for a particular pair of source and
   * destination Nodes, the Path with the least weight (cost) is saved off in
   * a "possibleBestPaths" vector of Paths - when a better Path is discovered
   * during the iteration for a given pair of Nodes, the current best Path is
   * cleared out and replaced by the new "better" best Path.
   *
   * After the final "possibleBestpaths" is determined for each of the input
   * Nodes, that vector of "possibleBestPaths" (which is really now the
   * "actualBestPaths" for a given input Node) is added to the final
   * "allPaths", which is that ultimate total list of all the best Paths for
   * every input Node to every other input Node. The caller of this method
   * will then use that final set of "allPaths" to find the MINIMUM set of
   * Paths that are needed to fully connect all the input Nodes to one
   * another.
   *
   * @param g
   *            the specified Graph that will be searched
   *
   * @param inputNodes
   *            the specified list of input Nodes to search for
   *
   * @return a vector of vector of input Node Paths for a Graph with a
   *         specified list of input Nodes
   */
  public static Vector<Vector<Path>> getInputNodePaths(Graph g, ArrayList<Node> inputNodes) {
    // create an initial empty vector of vector of all Paths to be returned
    Vector<Vector<Path>> allPaths = new Vector<Vector<Path>>();

    // the key processing loop for all the input Nodes - this
    // will be a O(n^2) number of trees that we process, in total...
    //
    // as a small example, if the input Nodes are this:
    //     a
    //     b
    //     c
    //     d
    // then, we would be evaluating all these Paths:
    //     a -> b
    //     a -> c
    //     a -> d
    //     b -> c
    //     b -> d
    //     c -> d
    // note that for each of the above Paths, it is quite
    // possible that other Nodes (that are NOT input Nodes)
    // will be part of each best Path that is discovered
    for (int i = 0; i < inputNodes.size() - 1; i++) {

      // collect some performance metrics...
      int numTotalIterations = 0;

      // for this input Node, we will check all the possible
      // paths to all the REST of the input Nodes
      for (int j = i + 1; j < inputNodes.size(); j++) {

        // collect some stats...
        int numIterationsThisPairNodes = 0;

        // create a working possible best Paths that will be used
        // repeatedly for each of the input Nodes being processed
        Vector<Path> bestPaths = new Vector<Path>();
        int bestPathWeight = Integer.MAX_VALUE;

        Node src = inputNodes.get(i);
        Node dst = inputNodes.get(j);

        // allocate a new stack and seed it with the "src" input
        // Node to evaluate this particular src -> dst Path (we
        // are looking for the best possible Path between the
        // "src" and "dst" input Nodes)
        Stack<Path> stack = new Stack<Path>();
        stack.push(new Path(src));

        Path currPath;
        // take this new stack and keep processing it until
        // the stack is empty - note that the stack only
        // starts with one element (the "src" input Node) -
        // but we'll be pushing new Paths that we discover
        // have to be evaluated as another possible "best" Path...
        while (!stack.isEmpty()) {

          // bump our iteration counters...
          numIterationsThisPairNodes++;
          numTotalIterations++;

          // pop off our current path
          currPath = stack.pop();

          // if the last Node in this current Path is equal to
          // our destination Node, we can evaluate the current
          // Path and see if it is any better than the best
          // Path we have found so far...
          if (currPath.lastNode().equals(dst)) {
            int currPathWeight = currPath.weight();
            if (currPathWeight < bestPathWeight) {
              // this current Path is better than what we have
              // found so far, so we can save off a new "best
              // weight" and also clear out the old "best Path"...
              bestPathWeight = currPathWeight;
              bestPaths.clear();
            }

            // we add this current Path to our list of possible
            // best Paths - there is a case where there could be
            // multiple best Paths (equal weights for 2 or more
            // Paths that can get from the "src" Node to the
            // "dst" Node
            bestPaths.add(currPath);
          } else {
            // we have NOT yet reached our "dst" Node from this
            // "src" Node - so we now process every Edge that
            // belongs to this last Node of the current Path
            //
            // note that we need to process each of the Edges in
            // their reverse order because we will potentially
            // add new Paths to the stack - and since the stack
            // is LIFO (by definition of a stack), we want to make
            // sure we process the new Paths in the correct order
            // as we pop them off the top of the stack...
            Node lastNode = currPath.lastNode();
            for (int edgeIndex = lastNode.edges().size(); edgeIndex > 0; edgeIndex--) {
              // get the other Node (the toNode) for this Edge...
              Edge curEdge = lastNode.edges().get(edgeIndex - 1);
              Node toNode = curEdge.to();

              // if our current Path does NOT contain this toNode,
              // we need to add it to our current Path...
              if (!currPath.contains(toNode)) {
                Path newPath = new Path(currPath);
                newPath.addNode(toNode, curEdge.weight());

                // we've added this toNode to our current Path,
                // but we only push this new Path onto our stack
                // for evaluation if - and ONLY if - the weight
                // of this new Path is the same or better than
                // the current best Path weight (where "better"
                // is defined as a lower weight, or cost)...
                if (newPath.weight() <= bestPathWeight) {
                  stack.push(newPath);
                }
              }
            }
          }
        }

        // show the total number of iterations we took to find all the
        // possible best Paths for this pair of input Nodes..
        System.out.println("...for src node " + src.toString() + " to dst node " + dst.toString() + ", numIterationsThisPairNodes = " + numIterationsThisPairNodes);

        // okay - we've now found all the possible best Paths for this
        // particular set of "src" and "dst" pair of input Nodes - so we
        // can add all of them to our final vector of all Paths that
        // will
        // be returned to the caller...
        allPaths.add(bestPaths);
      }
            System.out.println("...numTotalIterations = " + numTotalIterations);

      // we've processed all the possible paths for this input Node - now
      // we can continue to process the next input Node in our list...
    }

    // finally! we now have ALL the possible shortest Paths for every input
    // Node to every other input Node - so let's return them to the
    // caller...
    return allPaths;
  }

  /**
   * allNodesInPathSet
   *
   * Returns whether or not an entire list of Nodes can be found in a
   * specified vector of Path objects.
   *
   * @param nodes
   *            the list of Nodes to search for
   *
   * @param pathSet
   *            the vector of Path objects to search through
   *
   * @return true if the list of Nodes can be found in the vector of Paths;
   *         otherwise false
   */
  public static boolean allNodesInPathVec(ArrayList<Node> nodes,
      Vector<Path> pathSet) {
    // create an array of booleans to hold a 'found' flag for
    // each of the input Nodes
    boolean found[] = new boolean[nodes.size()];

    // now search for Node "node" in all the Paths in the specified
    // pathSet - and if we find it, then set its 'found' flag...
    for (Node node : nodes) {
      for (Path path : pathSet) {
        if (path.contains(node)) {
          found[nodes.indexOf(node)] = true;
          break;
        }
      }
    }

    // we've searched for all the input Nodes - now let's check to see
    // if each of them was actually found in out specified set of Paths...
    for (boolean b : found) {
      if (!b) {
        // oops! it looks like we could not find at least one of our
        // input Nodes - so we're done - tell the caller the bad news...
        return false;
      }
    }

    // woohoo! we were able to find all the input Nodes in the specified
    // set of Paths - send back the good news to the caller... :)
    return true;
  }

  /**
   * getPathsWeight
   *
   * Returns the total weight of all the Nodes in a vector of Paths.
   *
   * @param paths
   *            the input vector of Paths to find the total weight
   *
   * @return the total weight of all the input Paths
   */
  public static int getPathsWeight(Vector<Path> paths) {
    HashSet<Edge> edgeSet = new HashSet<Edge>();
    int pathsWeight = 0;

    for (Path path : paths) {
      for (int i = 0; i < path.size() - 1; i++) {
        Edge edgeToAdd = path.get(i).edgeTo(path.get(i + 1));
        if (edgeSet.add(edgeToAdd)) {
          pathsWeight += edgeToAdd.weight();
        }
      }
    }

    // return the total weight of all the Nodes in the specified
    // vector of Paths...
    return pathsWeight;
  }
}
