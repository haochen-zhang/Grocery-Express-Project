package edu.gatech.cs6310;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Deque;
import java.util.ArrayDeque;

public class PointManager {

    // Fields
    private static PointManager instance;
    private List<Point> points;

    // Public Methods
    public PointManager() {
        this.points = new ArrayList<Point>();
    }

    public static PointManager getInstance() {
        if (instance == null) {
            instance = new PointManager();
        }
        return instance;
    }

    public Point makePoint(int x, int y, boolean refuelingStation) {
        Point newPoint = new Point(x, y, refuelingStation);
        this.points.add(newPoint);
        return newPoint;
    }

    public Point makePoint(int x, int y) {
        return this.makePoint(x, y, false);
    }

    public Point duplicatePoint(Point other) {
        Point duplicatePoint = new Point(other.getX(), other.getY());
        this.points.add(duplicatePoint);
        return duplicatePoint;
    }

    public Point getClosestRefuelingStation(Point currentLocation) {
        int minDistance = Integer.MAX_VALUE;
        Point closestStation = null;
        for (Point currentPoint : this.points) {
            if (currentPoint != currentLocation && currentPoint.isRefuelStation()) {
                int distance = currentPoint.getDistance(currentLocation);
                if (distance < minDistance) {
                    closestStation = currentPoint;
                    minDistance = distance;
                }
            }
        }
        return closestStation;
    }

    private Set<Point> getAllRefuelingStations() {
        Set<Point> stations = new HashSet<Point>();

        for (Point current : this.points) {
            if (current.isRefuelStation()) {
                stations.add(current);
            }
        }

        return stations;
    }

    public boolean isRefuelingStationAtPoint(Point target) {
        if (target.isRefuelStation()) {
            return true;
        }

        for (Point current : this.points) {
            if (current.equals(target) && current.isRefuelStation()) {
                return true;
            }
        }

        return false;
    }

    public Refueler getRefuelingStationAtPoint(Point target) {
        for (Point current : this.points) {
            if (current.equals(target) && current.isRefuelStation()) {
                return current.getAssociatedStation();
            }
        }

        return null;
    }

    public Queue<Point> calculateRouteToPoint(Point start, Point target, int travelDistance, int fuelCapacity) {
        // Assumption: fuelCapacity can be used immediately and after each stop at a refueling station
        // Assumption: the start is a refueling station

        // Methodology: we will construct a graph that contains all refueling stations, the start location (which is a station), and the destination
        // We will connect all points to each other based on if they can be traveled between each other
        // If we can find a path from the start to the target, we have a path.
        // To find this path, we can use DFS (Dijkstra's would be better but not necessary for this project)

        Queue<Point> route = new LinkedList<Point>();
        Set<RouteNode> nodes = new HashSet<RouteNode>();
        Set<Point> stations = this.getAllRefuelingStations();
        RouteNode begin = null;
        RouteNode end = new RouteNode(target);

        // Create all of the graph nodes
        for (Point station : stations) {
            RouteNode newNode = new RouteNode(station);
            if (station == start) {
                begin = newNode;
            }
            nodes.add(newNode);
//            System.out.println("Created node");
        }

        nodes.add(end);

        // Connect all of the graph nodes if possible to traverse
        for (RouteNode node1 : nodes) {
            for (RouteNode node2 : nodes) {
                int distance = node1.getDistance(node2);
                if (distance <= travelDistance * fuelCapacity) {
                    node1.addConnection(node2);
                    node2.addConnection(node1);
//                    System.out.println("Connected nodes");
                }
            }
        }

        // perform the search (recursive DFS algorithm)
        Set<RouteNode> explored = new HashSet<RouteNode>();
        Deque<RouteNode> path = new ArrayDeque<RouteNode>();
        search(begin, end, path, explored, route);

        return route;
    }

    private void search(RouteNode begin, RouteNode end, Deque<RouteNode> path, Set<RouteNode> explored, Queue<Point> route) {
        if (explored.contains(begin)) {
//            System.out.println("Adding to explored");
            return;
        }

        explored.add(begin);
        path.add(begin);
//        System.out.println("Adding to path and explored");

        if (begin == end) {
//            System.out.println("Found path");
            for (RouteNode current : path) {
                route.add(current.getPoint());
            }
            return;
        }

        for (RouteNode connection : begin.getConnections()) {
//            System.out.println("Exploring child");
            search(connection, end, path, explored, route);
        }

//        System.out.println("Removing from path and explored");

        path.removeLast();
        explored.remove(begin);
    }
}