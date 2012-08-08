#ifndef GRAPH_H_
#define GRAPH_H_

#include <set>
#include <vector>

#include "graph_constants.h"
#include "node.h"

typedef std::set<Node> Nodes;
typedef std::vector<Node*> Path;
typedef std::set<Path> Paths;
typedef std::set<Paths> InputNodePaths;

class Graph {
 public:
  // Initializes a Graph with nodes (edges must be added later).
  Graph(const Nodes& nodes);

  ~Graph();

  bool AddEdge(const Node& node1, const Node& node2, int weight);

  // Returns true if this Graph is connected (all nodes can be reached from any
  // node).
  bool IsConnected() const;

  // Returns true if this Graph has a cycle.
  bool HasCycle() const;

  // Returns a set of Paths, the composition of which is a minimum spanning tree
  // connecting all |input_nodes|. A set of Paths rather than a set of Nodes is
  // returned, as the set of Paths is really what's needed when this algorithm
  // is un-abstracted to be used for database joins.
  Paths MinPaths(const Nodes& input_nodes) const;

  // Gets all of the paths from each of |input_nodes| to all others in
  // |input_nodes|. Returns a set of sets (rather than just a set) because there
  // may be two or more optimal paths from one node to another. In this case,
  // the "inner set" will have two or more elements.
  InputNodePaths(const Nodes& input_nodes) const;


  // Accessors
  const Nodes& nodes() const { return nodes_; }
  const size_t num_nodes() const { return num_nodes_; }
  const int[][] edges() const { return edges_; }
  const int[][] distance() const { return distance_; }
  const int[][] next() const { return next_; }

  const Node* GetNode(int ordinal) const;
  const Node* GetNode(const std::string& name) const;
  const Node* GetNode(const Node::Object& object) const;
  const Node* GetTopNode() const;

 private:
  Nodes nodes_;
  size_t num_nodes_;

  // edges_[i][j] represents the weight of the edge between Nodes i and j.
  // graph::NO_EDGE if there exists no edge between Nodes i and j.
  int[][] edges_;

  // distance_[i][j] represents the shortest distance between Nodes i and j.
  // Initialized lazily using Dijkstra's algorithm.
  int[][] distance_;

  // next_[i][j] represents the next Node on the shotest path from Node i to
  // Node j.
  int[][] next_;
}

#endif  // GRAPH_H_
