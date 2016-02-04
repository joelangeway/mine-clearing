import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SimulationState {
  protected Coordinate ship;
  protected List<Coordinate> mines;
  
  public Coordinate getShip() {
    return ship;
  }
  public List<Coordinate> getMines() {
    return mines;
  }
  public SimulationState(Coordinate ship, List<Coordinate> mines) {
    this.ship = ship;
    this.mines = mines;
  }
  public SimulationState move(Coordinate delta) {
    return new SimulationState(ship.add(delta), mines);
  }
  public SimulationState fall() {
    return new SimulationState(ship.add(new Coordinate(0,0,-1)), mines);
  }
  public SimulationState fire(List<Coordinate> firingPattern) {
    // we use a list to represent x,y coordinates because List's come with an identity implementation for free.
    Set<List<Integer>> firingLocations = firingPattern.stream().map(
        delta -> Arrays.asList(ship.getX() + delta.getX(), 
                              ship.getY() + delta.getY())
      ).collect(Collectors.toSet());
    return new SimulationState(ship,
      mines.stream().filter(
        mine -> ! firingLocations.contains(
                      Arrays.asList(mine.getX(), mine.getY()))
      ).collect(Collectors.toList())
    );
  }
  public boolean missedAny() {
    return mines.stream().anyMatch(mine -> mine.getZ() >= ship.getZ());
  }
  public int countMines() {
    return mines.size();
  }
}