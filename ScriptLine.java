import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ScriptLine {
  protected String moveName;
  protected String firingPatternName;
  protected Coordinate move;
  protected List<Coordinate> firingPattern;
  protected ScriptLine() {
    move = null;
    firingPattern = null;
  }
  public Coordinate getMove() { return move; }
  public List<Coordinate> getFiringPattern() { return firingPattern; }
  public String toString() {
    return Arrays.asList(firingPatternName, moveName).stream()
        .filter(s -> s != null).collect(Collectors.joining(" "));
  }

  public class SyntaxError extends Exception {
    public SyntaxError(String msg) { super(msg); }
  }
  protected void parseLine(String line) throws SyntaxError {
    String[] words = line.toLowerCase().split("\\s+");
    for(String word : words) {
      if(word.length() == 0) continue;
      Coordinate maybeMove = LookupTables.getMoveDelta(word);
      if(maybeMove != null) {
        if(move != null) {
          throw new SyntaxError("Move specified twice.");
        }
        moveName = word;
        move = maybeMove;
        continue;
      }
      List<Coordinate> maybeFiringPattern = LookupTables.getFiringPattern(word);
      if(maybeFiringPattern != null) {
        if(firingPattern != null) {
          throw new SyntaxError("Firing pattern specified twice.");
        }
        firingPatternName = word;
        firingPattern = maybeFiringPattern;
        continue;
      }
      throw new SyntaxError("Unrecognized command: '" + word + "'");
    }
  }
  public static ScriptLine fromLine(String line) throws SyntaxError {
    ScriptLine sl = new ScriptLine();
    sl.parseLine(line);
    return sl;
  }
}
