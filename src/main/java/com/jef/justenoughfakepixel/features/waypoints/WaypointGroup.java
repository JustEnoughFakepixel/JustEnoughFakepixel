package com.jef.justenoughfakepixel.features.waypoints;

import java.util.ArrayList;
import java.util.List;

/**
 * A named, ordered collection of waypoints (a "route")
 * Serialized by Gson – keep fields accessible with a no-arg constructor
 */
public class WaypointGroup {

    public String name;
    public String description;
    public List<WaypointPoint> waypoints;

    public WaypointGroup() {
        this.waypoints = new ArrayList<>();
        this.description = "";
    }

    public WaypointGroup(String name) {
        this.name = name;
        this.description = "";
        this.waypoints = new ArrayList<>();
    }

    public WaypointGroup(String name, String description) {
        this.name = name;
        this.description = description;
        this.waypoints = new ArrayList<>();
    }
}