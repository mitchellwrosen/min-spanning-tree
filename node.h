#ifndef _NODE_H_
#define _NODE_H_

#include "graph.h"

class Node {
 public:
  Node();

  ~Node();

  class Object {
   public:
    Object();
    ~Object();
  }

  const Graph* graph() const { return graph_; }
  const Object& object() const { return object_; }

  const int ordinal() const { return ordinal_; }
  void set_ordinal(int ordinal) { ordinal_ = ordinal; }

 private:
  Graph* graph_;
  Object object_;
  int ordinal_;
}

#endif  // _NODE_H_
