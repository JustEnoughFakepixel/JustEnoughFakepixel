package com.jef.justenoughfakepixel.features.waypoints;

public class WaypointPoint {

    public double x;
    public double y;
    public double z;
    public String name;

    /**
     * Required by Gson.
     */
    public WaypointPoint() {
    }

    public WaypointPoint(double x, double y, double z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    /**
     * Euclidean distance to a world position.
     */
    public double distanceTo(double ox, double oy, double oz) {
        double dx = x - ox, dy = y - oy, dz = z - oz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public String toString() {
        return name + " (" + (int) x + ", " + (int) y + ", " + (int) z + ")";
    }
}