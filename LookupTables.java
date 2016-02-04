import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class LookupTables {
  protected static Map<String, List<Coordinate>> firingPatterns;
  protected static Map<String, Coordinate> moveDeltas;

  public static void init() {
    firingPatterns = new HashMap<>();
    firingPatterns.put("alpha", Arrays.asList(
      new Coordinate(-1, -1, 0), new Coordinate(-1, 1, 0),
      new Coordinate(1, -1, 0), new Coordinate(1, 1, 0)
    ));
    firingPatterns.put("beta", Arrays.asList(
      new Coordinate(-1, 0, 0), new Coordinate(0, -1, 0),
      new Coordinate(0, 1, 0), new Coordinate(1, 0, 0)
    ));
    firingPatterns.put("gamma", Arrays.asList(
      new Coordinate(-1, 0, 0), new Coordinate(0, 0, 0), 
      new Coordinate(1, 0, 0)
    ));
    firingPatterns.put("delta", Arrays.asList(
      new Coordinate(0, -1, 0), new Coordinate(0, 0, 0), 
      new Coordinate(0, 1, 0)
    ));
    moveDeltas = new HashMap<>();
    moveDeltas.put("north", new Coordinate(0, -1, 0));
    moveDeltas.put("south", new Coordinate(0, 1, 0));
    moveDeltas.put("east", new Coordinate(1, 0, 0));
    moveDeltas.put("west", new Coordinate(-1, 0, 0));
  }
  public static Coordinate getMoveDelta(String directionName) {
    return moveDeltas.get(directionName);
  }
  public static List<Coordinate> getFiringPattern(String patternName) {
    return firingPatterns.get(patternName);
  }

}