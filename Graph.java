import java.lang.IllegalArgumentException;

public class Graph {
  private static final int INITV = 4; // the initial number of vertices

  private int V; // the number of vertices
  private int E; // the number of edges

  private Bag<Integer>[] adj; // adjacency list of connected vertices

  public Graph() {
    V = INITV;
    E = 0;
    adj = (Bag<Integer>[]) new Bag[INITV];
    for (int i = 0; i < INITV; i++) {
      adj[i] = new Bag<Integer>();
    }
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) {
      throw new IllegalArgumentException("vertex " + v + " out of bounds");
    }
  }

  /*
   * Resizes the adjacency list to accommodate a different number of vertices
   * Currently only supports increasing the array's size
   */
  private void resize(int newSize) {
    Bag<Integer>[] copy = (Bag<Integer>[]) new Bag[newSize];
    for (int i = 0; i < adj.length; i++) {
      copy[i] = adj[i];
    }
    for (int j = adj.length; j < newSize; j++) {
      copy[j] = new Bag<Integer>();
    }
    adj = copy;
  }

  public void addVertex() {
    if (V++ == adj.length) resize(V * 2);
  }

  public void addEdge(int v, int w) {
    validateVertex(v);
    validateVertex(w);
    adj[v].add(w);
    adj[w].add(v);
    E++;
  }

  public boolean hasEdge(int v, int w) {
    validateVertex(v);
    validateVertex(w);
    // if condition to make checking a bit faster
    if (adj[v].size() < adj[w].size()) {
      return adj[v].contains(w);
    } else {
      return adj[w].contains(v);
    }
  }

  public Iterable<Integer> adj(int v) {
    validateVertex(v);
    return adj[v];
  }

  public int V() {
    return this.V;
  }

  public int E() {
    return this.E;
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(V + " vertices, " + E + " edges " + "\n");
    for (int v = 0; v < V; v++) {
      s.append(v + ": ");
      for (int w : adj[v]) {
        s.append(w + " ");
      }
      s.append("\n");
    }
    return s.toString();
  }
}
