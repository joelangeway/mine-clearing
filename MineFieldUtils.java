import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class MineFieldUtils {
  /**
   *  Return the appropriate character to print in a grid position to reflect a Z distance. 
   */
  public static char distanceChar(int z) {
    if( z == -1 ) return '.';
    if( z == 0 ) return '*';
    if( z >= 1 && z <= 26) return (char)(z + (int)'a' - 1);
    if( z >= 27 && z <= 52) return (char)(z + (int)'A' - 27);
    throw new IllegalArgumentException(
                              "No character representation of distance: " + z);
  }

  /**
   *  Return the Z distance represented by a character from a textual grid.
   */
  public static int charDistance(char z) {
    if(z == '.') return -1;
    if(z == '*') return 0;
    if(z >= 'a' && z <= 'z') return 1 + (int)z - (int)'a';
    if(z >= 'A' && z <= 'Z') return 27 + (int)z - (int)'A';
    throw new IllegalArgumentException(
                      "Character does not represent a distance: '" + z + "'");
  }

  public static SimulationState parseField(String[] lines) {
    int h = lines.length;
    if(h == 0) {
      return new SimulationState(
          new Coordinate(0,0,0), 
          Collections.<Coordinate>emptyList()
      );
    }
    List<Coordinate> mines = new ArrayList<>();
    int w = lines[0].length();
    for(int y = 0; y < h; y++) {
      String line = lines[y];
      int lw = line.length();
      if(lw != w) {
        System.err.printf("WARNING Space is not rectangular at y = %d\n", y);
        w = Math.max(w, lw);
      }
      for(int x = 0; x < lw; x++) {
        int z;
        try {
          z = charDistance(line.charAt(x));
        } catch(IllegalArgumentException e) {
          System.err.printf( 
            "WARNING Unrecognized character at (%d, %d) of field.\n", x, y);
          z = -1;
        }
        if(z >= 0) {
          mines.add(new Coordinate(x, y, -z));
        }
      }
    }
    int sx, sy;
    if(h % 2 == 0) {
      System.err.printf("WARNING Space is of even height, " + 
                  "chosing north most coordinate closest to center.\n");
      sy = h / 2 - 1;
    } else {
      sy = (h - 1) / 2;
    }
    if(w % 2 == 0) {
      System.err.printf("WARNING Space is of even width, " + 
                  "chosing west most coordinate closest to center.\n");
      sx = w / 2 - 1;
    } else {
      sx = (w - 1) / 2;
    }
    return new SimulationState(new Coordinate(sx,sy,0), mines);
  }

  public static String[] renderField(SimulationState ss) {
    List<Coordinate> mines = ss.getMines();
    Coordinate ship = ss.getShip();
    Coordinate maxOffsets = mines.stream().map(
        mine -> ship.sub(mine).abs()
      ).reduce(new Coordinate(0,0,0), (d1, d2) -> d1.max(d2));
    int w = 1 + 2 * maxOffsets.getX();
    int h = 1 + 2 * maxOffsets.getY();
    int dx = maxOffsets.getX() - ship.getX();
    int dy = maxOffsets.getY() - ship.getY();

    int[][] field = new int[w][h];
    for(int x = 0; x < w; x++) {
      for(int y = 0; y < h; y++) {
        field[x][y] = -1;
      }
    }
    int z0 = ship.getZ();
    for(Coordinate mine : mines) {
      int x = mine.getX() + dx;
      int y = mine.getY() + dy;
      field[x][y] = z0 - mine.getZ();
    }
    return IntStream.range(0, h).mapToObj(
      y -> IntStream.range(0, w).mapToObj(
        x -> String.valueOf(distanceChar(field[x][y]))
      ).collect(Collectors.joining())
    ).toArray(String[]::new);
  }
}
