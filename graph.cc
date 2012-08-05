#include "graph.h"

Graph::Graph(const Nodes& nodes) : nodes_(nodes),
                                   num_nodes_(nodes_.size()),
                                   edges_(new int[num_nodes_][num_nodes_]) {
  int i = 0;
  for (Nodes::iterator iter = nodes_.begin(); iter != nodes_.end(); ++iter)
    iter->set_ordinal(i++);
}

/*
Graph::Graph(const std::vector<Path>& paths) : nodes_(new std::vector<Node>()) {
  Node* prev_node = NULL;
  Node* cur_node = NULL;

  for (std::vector<Path>::const_iterator path_iter = paths.nodes().begin();
       path_iter != paths.nodes().end(); ++path_iter) {
    std::pair<Nodes::iterator, bool> ret =
        nodes_.insert(path_iter->nodes().at(0));
    prev_node = &(*ret.first);

    for (size_t i = 1; i < path_iter->nodes().size(); ++i) {
      std::pair<Nodes::iterator, bool> ret =
          nodes_.insert(path_iter->nodes().at(i));
      cur_node = &(*ret.first);

      // The 0th Edge connects the 0th and 1st Nodes
      prev_node->AddEdge(path_iter->edges(i-1));
      cur_node->AddEdge(path_iter->edges(i-1));

      prev_node = cur_node;
    }
  }
}
*/

Graph::~Graph() {
  if (edges_)
    delete edges_;
}

bool Graph::IsConnected() const {
  if (num_nodes_ == 0)
    return true;

  bool touched[num_nodes_];
  for (size_t i = 0; i < num_nodes_; ++i)
    touched[i] = false;

  // Perform a BFS and touch nodes as we go. If any nodes remain untouched, this
  // Graph is not connected.
  std::stack<int> nodes_to_visit;
  nodes_to_visit.push(0);
  while (!nodes_to_visit.empty()) {
    int node = nodes_to_visit.pop();
    touched[node] = true;

    for (size_t i = 0; i < num_nodes_; ++i) {
      if (edges[node][i] != graph::NO_EDGE && !touched[i]) {
        nodes_to_visit.push(i);
      }
    }
  }

  for (size_t i = 0; i < num_nodes_; ++i) {
    if (!touched[i])
      return false;
  }

  return true;
}

bool Graph::HasCycle() const {
  if (num_nodes_ == 0)
    return false;

  bool touched[num_nodes_];
  int parent[num_nodes_];
  for (size_t i = 0; i < num_nodes_; ++i) {
    touched[i] = false;
    parent[num_nodes_] = graph::NO_PARENT;
  }

  // Perform a BFS and touch nodes as we go. If ever we touch an already-touched
  // node, this Graph has a cycle. Ignore the "parent" of each node (if we
  // arrive at node B from node A, don't consider the same edge from B to A).
  std::stack<int> nodes_to_visit;
  nodes_to_visit.push(0);

  // Visit all nodes reachable from this node.
visit:
  while (!nodes_to_visit.empty()) {
    int node = nodes_to_visit.pop();
    touched[node] = true;

    for (size_t i = 0; i < num_nodes_; ++i) {
      if (edges[node][i] != graph::NO_EDGE && parent[node] != i) {
        if (!touched[i]) {
          touched[i] = true;
          parent[i] = node;
        } else {
          return true;
        }
      }
    }
  }

  // If we didn't reach every node from the seed node, re-seed and search again
  // for cycles.
  for (size_t i = 0; i < num_nodes_; ++i) {
    if (!touched[i]) {
      nodes_to_visit.push_back[i];
      goto visit; // My first goto... wow.
    }
  }

  return false;
}

std::set<Path> MinPaths(const Nodes& input_nodes) const {
  std::set<Path> min_paths;
  int min_paths_weight = graph::INFINITY;

  InputNodePaths input_node_paths = GetInputNodePaths(input_nodes);

  // More shit
}

InputNodePaths GetInputNodePaths(const Nodes& input_nodes) const {
  InputNodePaths input_node_paths;

  Nodes::const_iterator end_iter = input_nodes.end(); // Save calls to .end().
  for (Nodes::const_iterator from_iter = input_nodes.begin();
       from_iter != end_iter; ++from_iter) {
    for (Nodes::const_iterator to_iter = from_iter + 1;
         to_iter != end_iter; ++to_iter) {
      // Fill best_paths with the optimal paths from *from_iter to *to_iter.
      // Most likely it will be a set of only one element, unless there are two
      // or more optimal paths.
      Paths best_paths;
      int best_path_weight = graph::INFINITY;

      std::stack<Path> stack;
      Path seed;
      seed.push(&(*input_nodes.begin()));
      stack.push(seed);
      while (!stack.empty()) {
        Path cur_path = stack.pop();

        // We found
        if (cur_path->back() == *to_iter) {
          int cur_path_weight = GetPathWeight(cur_path);
          if (cur_path_weight < best_path_weight) {
            best_path_weight = cur_path_weight;
            if (!best_paths.empty())
              best_paths.clear();
          }

          best_paths.insert(cur_path);
        }
      }


    }
  }


}






const Node* Graph::GetNode(int ordinal) const {
  for (Nodes::const_iterator itr = nodes_.begin(); itr != nodes_.end(); ++itr) {
    if (iter->ordinal() == ordinal)
      return &(*itr);
  }

  return NULL;
}

const Node* Graph::GetNode(const std::string& name) const {
  for (Nodes::const_iterator itr = nodes_.begin(); itr != nodes_.end(); ++itr) {
    if (iter->name() == name)
      return &(*itr);
  }

  return NULL;
}

const Node* Graph::GetNode(const Node::Object& object) const {
  for (Nodes::const_iterator itr = nodes_.begin(); itr != nodes_.end(); ++itr) {
    if (iter->object() == object)
      return &(*iter);
  }

  return NULL;
}

const Node* Graph::GetTopNode() const {
  std::cout << "GetTopNode not implemented" << std::endl;
  exit(1);
  return NULL;
}

