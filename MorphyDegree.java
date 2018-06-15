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
  private TreeMap<String, Integer> players;
  private Graph G;
  private int totalGames;

  public MorphyDegree(String filename) {
    players = new TreeMap<>(new Comparator<String>() {
      public int compare(String s1, String s2) {
        return s1.compareTo(s2);
      }
    });
    G = new Graph();
    totalGames = 0;

    readFile(filename);
  }

  public MorphyDegree(String[] files) {
    // players = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    players = new TreeMap<>(new Comparator<String>() {
      public int compare(String s1, String s2) {
        return s1.compareTo(s2);
      }
    });
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
      br.close();
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
    // System.out.println(db.dbSize());
    // System.out.println(db.games());

    Scanner in = new Scanner(System.in);
    while (true) {
      String player1 = in.nextLine();
      if (player1.equals("q")) break;
      db.queryPlayer(player1);

      String player2 = in.nextLine();
      db.queryPlayer(player2);

      try {
        System.out.println(db.getDegree(player1, player2));
      } catch (IllegalArgumentException iae) {
        System.out.println("Invalid player names " + iae);
      }
    }
    in.close();
  }
}
