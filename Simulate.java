import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class Simulate {

  private SimulationState state;
  private List<ScriptLine> script;
  private int t;
  private int nInitialMines, nFires, nMoves;

  private boolean load(String fieldFilename, 
                      String scriptFilename) throws IOException {
    try(BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(fieldFilename), "UTF-8")
        )) {
      state = MineFieldUtils.parseField(br.lines().toArray(String[]::new));
    }
    script = new ArrayList<>();
    boolean scriptErrors = false;
    try(BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(scriptFilename), "UTF-8")
        )) {
      for(String line : br.lines().toArray(String[]::new)) {
        ScriptLine sl = null;
        try {
          sl = ScriptLine.fromLine(line);
        } catch(ScriptLine.SyntaxError e) {
          System.err.printf("Syntax error at line %d of '%s': %s", 
            script.size() + 1, scriptFilename, e.getMessage());
          scriptErrors = true;
        }
        script.add(sl);
      }
    }
    if(scriptErrors) {
      return false;
    }
    nInitialMines = state.countMines();
    nFires = 0;
    nMoves = 0;
    t = 0;
    return true;
  }

  /**
   *  For the case where the ship has destroyed all the mines with no more steps left in the script, we compute the score as a function of the initial number of mines, the number of volleys fired, and the number of moves performed.
   */
  private int computeScore() {
    int initScore = 10 * nInitialMines;
    int nFiresPenalty = Math.min(5 * nFires, 5 * nInitialMines);
    int nMovesPenalty = Math.min(2 * nMoves, 3 * nInitialMines);
    return initScore - nFiresPenalty - nMovesPenalty;
  }

  /**
   *  Perform one whole time step of the simulation and produce output. Return true if another timestep should happen, false otherwise. Produce final output on last timestep.
   */
  private boolean step() {
    ScriptLine sl = script.get(t);
    System.out.printf("Step %d\n\n", t + 1);
    System.out.printf("%s\n\n", 
        String.join("\n", MineFieldUtils.renderField(state)));
    System.out.printf("%s\n\n", sl.toString());
    List<Coordinate> firingPattern = sl.getFiringPattern();
    if(firingPattern != null) {
      nFires++;
      state = state.fire(firingPattern);
    }
    Coordinate move = sl.getMove();
    if(move != null) {
      nMoves++;
      state = state.move(move);
    }
    state = state.fall();
    System.out.printf("%s\n\n", 
        String.join("\n", MineFieldUtils.renderField(state)));
    
    t++;
    
    if(state.missedAny()) {
      System.out.printf("fail (0)\n");
      return false;
    }
    boolean clearedMines = state.countMines() == 0;
    boolean stepsRemaining = script.size() > t;
    if(clearedMines && stepsRemaining) {
      System.out.printf("pass (1)\n");
      return false;
    }
    if(!clearedMines && !stepsRemaining) {
      System.out.printf("fail (0)\n");
      return false;
    }
    if(clearedMines && !stepsRemaining) {
      System.out.printf("pass (%d)\n", computeScore());
      return false;
    }
    return true;
  }


  public static void main(String[] args) {
    if(args.length != 2) {
      System.err.printf("Usage:\njava Simulate <field file> <script file>\n");
      System.exit(1);
    }
    String fieldFilename = args[0], scriptFilename = args[1];
    File fieldFile = new File(fieldFilename);
    if(!fieldFile.exists()) {
      System.err.printf("Unable to find field file: '%s'\n", fieldFilename);
      System.exit(2);
    }
    if(!fieldFile.canRead()) {
      System.err.printf("Unable to read field file: '%s'\n", fieldFilename);
      System.exit(2);
    }
    File scriptFile = new File(scriptFilename);
    if(!scriptFile.exists()) {
      System.err.printf("Unable to find script file: '%s'\n", scriptFilename);
      System.exit(2);
    }
    if(!scriptFile.canRead()) {
      System.err.printf("Unable to read script file: '%s'\n", scriptFilename);
      System.exit(2);
    }
    LookupTables.init();
    Simulate sim = new Simulate();
    try {
      if(! sim.load(fieldFilename, scriptFilename)) {
        System.exit(3);
      }
    } catch(IOException e) {
      System.err.printf("Unexpected exception!\n");
      // This will look ugly to users, but it is the easiest way to make sure we don't lose valuable debugging info.
      throw new RuntimeException(e);
    }

    while(sim.step());
  }
}