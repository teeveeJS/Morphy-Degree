/*
 * Calculates the degrees of separation between the queried player and Paul Morphy
 * Implementations of Graph.java and BFS based on
 * https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/Graph.java.html
 * https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/BreadthFirstPaths.java.html
 * respectively.
 *
 * Author: Teemu
 *
 * Compilation: javac MorphyDegree.java
 * Execution: java MorphyDegree database.pgn
 */

import java.util.TreeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Scanner;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.IllegalArgumentException;

public class MorphyDegree {
  private final String MORPHY = "Morphy, Paul";
  private TreeMap<String, Integer> players;
  private Graph G;

  public MorphyDegree(String filename) {
    players = new TreeMap<>();
    G = new Graph();

    try {
      Scanner sc = new Scanner(new File(filename));

      Pattern playerWhite = Pattern.compile("\\[White \\\"(.*)\\\"\\]");
      Pattern playerBlack = Pattern.compile("\\[Black \\\"(.*)\\\"\\]");

      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        Matcher whiteMatcher = playerWhite.matcher(line);
        if (whiteMatcher.find()) {
          String white = whiteMatcher.group(1);

          String black = "";
          String line2 = sc.nextLine();
          Matcher blackMatcher = playerBlack.matcher(line2);
          if (blackMatcher.find()) {
              black = blackMatcher.group(1);
          }

          // System.out.println("Found players: " + white + " and " + black);

          int wVal;
          int bVal;
          if (!players.containsKey(white)) {
            players.put(white, players.size());
            wVal = players.size() - 1;
            if (players.size() == G.V()) G.addVertex();
          } else {
            wVal = players.get(white);
          }
          if (!players.containsKey(black)) {
            players.put(black, players.size());
            bVal = players.size() - 1;
            if (players.size() == G.V()) G.addVertex();
          } else {
            bVal = players.get(black);
          }
          if (!G.hasEdge(wVal, bVal)) {
            G.addEdge(wVal, bVal);
          }
        }
      }
      sc.close();
    } catch (IOException ioe) {
      throw new IllegalArgumentException("Cannot open file: " + ioe);
    }
  }

  private void validatePlayer(String player) {
    if (!players.containsKey(player)) {
      throw new IllegalArgumentException("No games by the given player " + player + " exist in the input file");
    }
  }

  /*
   * Calculates the Morphy Degree of the given player
   */
  public int getDegree(String player) {
    return getDegree(player, MORPHY);
  }

  /*
   * Calculates the degree of separation between player and target
   * Uses a Breadth-First Search Algorithm
   */
  public int getDegree(String player, String target) {
    validatePlayer(player);
    validatePlayer(target);

    int start = players.get(player);
    int end = players.get(target);

    boolean[] visited = new boolean[G.V()];
    int[] dist = new int[G.V()];
    for (int i = 0; i < G.V(); i++) {
      dist[i] = -1;
    }
    Queue<Integer> q = new Queue<>();

    visited[start] = true;
    dist[start] = 0;

    int current = start;
    while (current != end) {
      for (int v : G.adj(current)) {
        if (!visited[v]) {
          dist[v] = dist[current] + 1;
          visited[v] = true;
          q.enqueue(v);
        }
      }
      if (q.isEmpty()) break;
      current = q.dequeue();
    }
    return dist[end];
  }

  public void printPlayers() {
    for (String name : players.descendingKeySet()) {
      System.out.println(name + " : " + players.get(name));
    }
  }

  public static void main(String[] args) {
    MorphyDegree db = new MorphyDegree(args[0]);

    // db.printPlayers();

    Scanner in = new Scanner(System.in);
    while (true) {
      String player1 = in.nextLine();
      if (player1.equals("q")) break;
      String player2 = in.nextLine();
      try {
        System.out.println(db.getDegree(player1, player2));
      } catch (IllegalArgumentException iae) {
        System.out.println("Invalid player names " + iae);
      }
    }
  }
}
