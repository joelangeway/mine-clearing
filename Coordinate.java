public class Coordinate {
  protected int x, y, z;
  public Coordinate(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  public Coordinate(Coordinate other) {
    this.x = other.x;
    this.y = other.y;
    this.z = other.z;
  }
  public int getX() { return x; }
  public int getY() { return y; }
  public int getZ() { return z; }
  public String toString() {
    return String.format("(%d,%d,%d)", x, y, z);
  }
  public Coordinate add(Coordinate other) {
    return new Coordinate(x + other.x, y + other.y, z + other.z);
  }
  public Coordinate sub(Coordinate other) {
    return new Coordinate(x - other.x, y - other.y, z - other.z);
  }
  public Coordinate abs() {
    return new Coordinate(Math.abs(x), Math.abs(y), Math.abs(z));
  }
  public Coordinate invert() {
    return new Coordinate(-x, -y, -z);
  }
  public Coordinate max(Coordinate other) {
    return new Coordinate(
        Math.max(x, other.x), Math.max(y, other.y), Math.max(z, other.z));
  }
  public Coordinate min(Coordinate other) {
    return new Coordinate(
        Math.min(x, other.x), Math.min(y, other.y), Math.min(z, other.z));
  }
  public Coordinate[] minmax(Coordinate[] acc) {
    return new Coordinate[] { min(acc[0]), max(acc[1]) };
  }
}
