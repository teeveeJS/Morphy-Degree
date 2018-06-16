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
 * Execution: java MorphyDegree database.pgn [database2.pgn database3.pgn ...]
 */

import java.util.TreeMap;
import java.util.Map;
import java.util.Stack;
import java.util.Comparator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Scanner;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.IllegalArgumentException;

public class MorphyDegree {
  private final String MORPHY = "Morphy, Paul";

  private TreeMap<String, Integer> players; // symbol table to go to from name to id
  private TreeMap<Integer, String> ids; // symbol table to go from id to name
  private Graph G;

  private int totalGames; // the total number of games processed

  public MorphyDegree(String filename) {
    players = new TreeMap<>(new Comparator<String>() {
      public int compare(String s1, String s2) {
        return s1.compareTo(s2);
      }
    });
    ids = new TreeMap<>();

    G = new Graph();
    totalGames = 0;

    readFile(filename);
  }

  public MorphyDegree(String[] files) {
    players = new TreeMap<>(new Comparator<String>() {
      public int compare(String s1, String s2) {
        return s1.compareTo(s2);
      }
    });
    ids = new TreeMap<>();

    G = new Graph();
    totalGames = 0;

    for (int i = 0; i < files.length; i++) {
      readFile(files[i]);
    }
  }

  private void readFile(String filename) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(new File(filename)));

      Pattern playerWhite = Pattern.compile("\\[White \\\"(.*)\\\"\\]");
      Pattern playerBlack = Pattern.compile("\\[Black \\\"(.*)\\\"\\]");

      String line;
      while ((line = br.readLine()) != null) {
        Matcher whiteMatcher = playerWhite.matcher(line);
        if (whiteMatcher.find()) {
          totalGames++;

          String white = whiteMatcher.group(1);

          String black = "";
          String line2 = br.readLine();
          Matcher blackMatcher = playerBlack.matcher(line2);
          if (blackMatcher.find()) {
              black = blackMatcher.group(1);
          }

          if (!white.equals("NN") && !black.equals("NN")) {
            int wVal;
            int bVal;
            if (!players.containsKey(white)) {
              int id = players.size();
              players.put(white, id);
              ids.put(id, white);
              wVal = id;
              if (id == G.V() - 1) G.addVertex();
            } else {
              wVal = players.get(white);
            }
            if (!players.containsKey(black)) {
              int id = players.size();
              players.put(black, id);
              ids.put(id, black);
              bVal = id;
              if (id == G.V() - 1) G.addVertex();
            } else {
              bVal = players.get(black);
            }
            if (!G.hasEdge(wVal, bVal)) {
              G.addEdge(wVal, bVal);
            }
          }
        }
      }
      br.close();
    } catch (IOException ioe) {
      throw new IllegalArgumentException("Cannot open file: " + ioe);
    }
  }

  private void validatePlayer(String player) {
    if (player == null) {
      throw new IllegalArgumentException("Argument is null");
    }
    if (!players.containsKey(player)) {
      throw new IllegalArgumentException("No games by the given player " + player + " exist in the input file");
    }
  }

  /*
   * Calculates the minimum distance and path of every player with respect to the given player
   * Someone please come up with a better method name
   */
  public PlayerDegrees calculateAll(String player) {
    validatePlayer(player);

    int start = players.get(player);

    boolean[] visited = new boolean[G.V()];
    int[] path = new int[G.V()];
    int[] dist = new int[G.V()];
    for (int i = 0; i < G.V(); i++) {
      path[i] = -1;
      dist[i] = -1;
    }
    Queue<Integer> q = new Queue<>();

    visited[start] = true;
    dist[start] = 0;

    int current = start;
    while (!q.isEmpty()) {
      for (int v : G.adj(current)) {
        if (!visited[v]) {
          path[v] = current;
          dist[v] = dist[current] + 1;
          visited[v] = true;
          q.enqueue(v);
        }
      }
      current = q.dequeue();
    }

    return new PlayerDegrees(player, players, ids, path, dist);
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

  public Iterable<String> getPlayerSequence(String player) {
    return getPlayerSequence(player, MORPHY);
  }

  public Iterable<String> getPlayerSequence(String player, String target) {
    validatePlayer(player);
    validatePlayer(target);

    int start = players.get(player);
    int end = players.get(target);

    boolean[] visited = new boolean[G.V()];
    int[] path = new int[G.V()];
    for (int i = 0; i < G.V(); i++) {
      path[i] = -1;
    }
    Queue<Integer> q = new Queue<>();

    visited[start] = true;

    int current = start;
    while (current != end) {
      for (int v : G.adj(current)) {
        if (!visited[v]) {
          path[v] = current;
          visited[v] = true;
          q.enqueue(v);
        }
      }
      if (q.isEmpty()) break;
      current = q.dequeue();
    }

    if (path[end] == -1) return null; // no path from player to target

    Queue<String> playerSequence = new Queue<>();
    playerSequence.enqueue(target);
    int pathNode = end;
    while (pathNode != start) {
      pathNode = path[pathNode];
      playerSequence.enqueue(ids.get(pathNode));
    }
    return playerSequence;
  }

  private void queryPlayer(String player) {
    if (players.containsKey(player)) {
      int connections = 0;
      for (int i : G.adj(players.get(player))) connections++;
      System.out.println(connections + " connections found for " + player);
    } else {
      System.out.println("Player " + player + " not found");
    }
  }

  public void printPlayers() {
    for (Map.Entry<String, Integer> p : players.entrySet()) {
      System.out.println(p.getValue() + ": " + p.getKey());
    }
    System.out.println(players.size() + " players found");
  }

  public int dbSize() {
    return players.size();
  }

  public int games() {
    return totalGames;
  }

  public static void main(String[] args) {
    String[] files = new String[args.length];
    for (int i = 0; i < args.length; i++) {
      files[i] = args[i];
    }
    MorphyDegree db = new MorphyDegree(files);

    // db.printPlayers();
    System.out.println(db.dbSize());
    System.out.println(db.games());

    Scanner in = new Scanner(System.in);
    while (true) {
      String player1 = in.nextLine();
      if (player1.equals("q")) break;
      db.queryPlayer(player1);

      String player2 = in.nextLine();
      db.queryPlayer(player2);

      try {
        System.out.println(db.getDegree(player1, player2));
        Iterable<String> seq = db.getPlayerSequence(player1, player2);
        for (String s : seq) {
          System.out.print(s + " -> ");
        }
        System.out.print("\n");
      } catch (IllegalArgumentException iae) {
        System.out.println("Invalid player names " + iae);
      }
    }
    in.close();
  }
}
