package dungeonmania.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Position implements Serializable {
    private final int x, y, layer;

    public Position(int x, int y, int layer) {
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.layer = 0;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(x, y, layer);
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Position other = (Position) obj;

        // z doesn't matter
        return x == other.x && y == other.y;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final int getLayer() {
        return layer;
    }

    public final Position asLayer(int layer) {
        return new Position(x, y, layer);
    }

    public final Position translateBy(int x, int y) {
        return this.translateBy(new Position(x, y));
    }

    public final Position translateBy(Direction direction) {
        return this.translateBy(direction.getOffset());
    }

    public final Position translateBy(Position position) {
        return new Position(this.x + position.x, this.y + position.y, this.layer + position.layer);
    }

    //the following two methods are used for extension2
    public static List<Position> getNeighbours(Position pos, int distance) {
        List<Position> neighbours = new ArrayList<>();

        neighbours.add(new Position(pos.getX(), pos.getY() + distance));
        neighbours.add(new Position(pos.getX(), pos.getY() - distance));
        neighbours.add(new Position(pos.getX() + distance, pos.getY()));
        neighbours.add(new Position(pos.getX() - distance, pos.getY()));;

        return neighbours;
    }

    public static final Position getInBetween(Position a, Position b) {
        int midX = (a.getX() + b.getX()) / 2;
        int midY = (a.getY() + b.getY()) / 2;
        return new Position(midX, midY);
    }

    // (Note: doesn't include z)

    /**
     * Calculates the position vector of b relative to a (ie. the direction from a
     * to b)
     * @return The relative position vector
     */
    public static final Position calculatePositionBetween(Position a, Position b) {
        return new Position(b.x - a.x, b.y - a.y);
    }

    public static final boolean isAdjacent(Position a, Position b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y) == 1;
    }

    // (Note: doesn't include z)
    public final Position scale(int factor) {
        return new Position(x * factor, y * factor, layer);
    }

    @Override
    public final String toString() {
        return "Position [x=" + x + ", y=" + y + ", z=" + layer + "]";
    }

    // Return Adjacent positions in an array list with the following element positions:
    // 0 1 2
    // 7 p 3
    // 6 5 4
    public List<Position> getAdjacentPositions() {
        List<Position> adjacentPositions = new ArrayList<>();
        adjacentPositions.add(new Position(x-1, y-1));
        adjacentPositions.add(new Position(x  , y-1));
        adjacentPositions.add(new Position(x+1, y-1));
        adjacentPositions.add(new Position(x+1, y));
        adjacentPositions.add(new Position(x+1, y+1));
        adjacentPositions.add(new Position(x  , y+1));
        adjacentPositions.add(new Position(x-1, y+1));
        adjacentPositions.add(new Position(x-1, y));
        return adjacentPositions;
    }

    public List<Position> getCardinallyAdjacentPositions() {
        List<Position> Positions = new ArrayList<>();
        Positions.add(new Position(x  , y-1));
        Positions.add(new Position(x+1, y));
        Positions.add(new Position(x  , y+1));
        Positions.add(new Position(x-1, y));
        return Positions;
    }

    // Take care if you want to use this function for other entity except bomb
    // This return a list of Pos for a square range
    public List<Position> getSquaredRangePos(int range) {
        List<Position> radiusPositions = new ArrayList<>();
        for (int i = 0; i < range + 1; i++) {
            for (int j = 0; j < range + 1; j++) {
                radiusPositions.add(new Position(x + i ,  y + j));
            }
        }
        return radiusPositions;
    }

    public boolean inRange(int radius, Position b) {
        return Math.abs(x - b.getX()) <= radius && Math.abs(y - b.getY()) <= radius;
    }

    // !! DON'T use pow. It return 1 for pow(0, any int)
    public static final double calculateDistanceBetween(Position a, Position b) {
        double distance;
        double x_diff = a.getX() - b.getX();
        double y_diff = a.getY() - b.getY();
        distance = (Double) Math.sqrt( x_diff * x_diff + y_diff * y_diff);
        return distance;
    }

    public static final double cardinallyDistanceBetween(Position a, Position b) {
        double distance;
        double x_diff = a.getX() - b.getX();
        double y_diff = a.getY() - b.getY();
        distance = (Double) Math.abs(x_diff) + Math.abs(y_diff);
        return distance;
    }
}
