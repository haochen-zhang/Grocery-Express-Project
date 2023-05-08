package edu.gatech.cs6310;

import java.util.Set;
import java.util.HashSet;

public class RouteNode {
    
    private Point thisPoint;
    private Set<RouteNode> connections;

    public RouteNode(Point thisPoint) {
        this.thisPoint = thisPoint;
        this.connections = new HashSet<RouteNode>();
    }

    public int getDistance(RouteNode node) {
        return thisPoint.getDistance(node.thisPoint);
    }

    public void addConnection(RouteNode other) {
        if (!this.connections.contains(other)) {
            this.connections.add(other);
        }
    }

    public Set<RouteNode> getConnections() {
        return this.connections;
    }

    public Point getPoint() {
        return this.thisPoint;
    }
}