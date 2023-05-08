package edu.gatech.cs6310;

public class Point {

    private int x;
    private int y;
    private boolean refuelStation;
    private Refueler associatedStation;

    public Point(int x, int y) {
        this(x, y, false);
    }

    public Point(int x, int y, boolean refuelStation) {
        this.x = x;
        this.y = y;
        this.refuelStation = refuelStation;
        this.associatedStation = null;
    }

    public void setAssociatedStation(Refueler station) {
        this.associatedStation = station;
    }

    public Refueler getAssociatedStation() {
        return this.associatedStation;
    }

    public double getTrueDistance(Point other) {
        return Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2));
    }

    public int getDistance(Point other) {
        return (int) Math.ceil(Math.abs(Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2))));
    }

    public boolean isRefuelStation() {
        return this.refuelStation;
    }

    public void adjustLocation(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Point)) {
            return false;
        }

        Point otherPoint = (Point) obj;
        return this.x == otherPoint.x && this.y == otherPoint.y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void moveTowardPoint(Point target, int travelDistance) {
        int distance = this.getDistance(target);
        if (distance <= travelDistance) {
            this.x = target.x;
            this.y = target.y;
            return;
        }

        // 1) get x and y dif for vector
        double xDif = target.x - this.x;
        double yDif = target.y - this.y;

        // 2) normalize so that these are directions
        double magnitude = Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2));
        double xDir = xDif / magnitude;
        double yDir = yDif / magnitude;

        // 3) calculate resulting true dif when traveling
        double xTrueDif = xDir * travelDistance;
        double yTrueDif = yDir * travelDistance;

        // 4) since its all integers we will round down when traveling
        int destinationX = this.x + (int) Math.floor(xTrueDif);
        int destinationY = this.y + (int) Math.floor(yTrueDif);

        this.x = destinationX;
        this.y = destinationY;
    }
}
