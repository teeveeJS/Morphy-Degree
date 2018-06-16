import java.util.TreeMap;
import java.util.Stack;

/*
 * Helper class for calculating all paths and degrees emanating from the source player
 */
public class PlayerDegrees {
  private String sourcePlayer;
  private TreeMap<String, Integer> players;
  private TreeMap<Integer, String> ids;
  private int[] paths;
  private int[] degrees;

  public PlayerDegrees(String sourcePlayer,
                        TreeMap<String, Integer> players,
                        TreeMap<Integer, String> ids,
                        int[] paths,
                        int[] degrees)
  {
    this.sourcePlayer = sourcePlayer;
    this.players = players;
    this.ids = ids;
    this.paths = paths;
    this.degrees = degrees;
  }

  private void validatePlayer(String player) {
    if (player == null) {
      throw new IllegalArgumentException("Argument is null");
    }
    if (!players.containsKey(player)) {
      throw new IllegalArgumentException("No games by the given player " + player + " exist in the input file");
    }
  }

  public Iterable<String> queryPath(String player) {
    validatePlayer(player);

    int goal = players.get(player);
    int start = players.get(sourcePlayer);
    if (paths[goal] == -1) return null;

    Stack<String> playerSequence = new Stack<>();
    playerSequence.push(player);
    int pathNode = goal;
    while (pathNode != start) {
      pathNode = paths[pathNode];
      playerSequence.push(ids.get(pathNode));
    }
    return playerSequence;
  }

  public int queryDegree(String player) {
    validatePlayer(player);
    return degrees[players.get(player)];
  }
}
