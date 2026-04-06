package com.jef.justenoughfakepixel.features.waypoints;

/**
 * Holds all runtime state for the currently loaded waypoint group.
 * Thread-access: advance logic runs on the render thread; commands run on the game thread.
 * Keep mutations simple – no heavy locking needed for a client-side mod.
 */
public class WaypointState {

    private static final WaypointState INSTANCE = new WaypointState();

    public WaypointGroup loadedGroup = null;

    public int currentIndex = 0;

    public boolean setupMode = false;
    public boolean enabled = true;

    public double advanceRange = 5.0;
    public long advanceDelayMs = 2000L;

    public long advanceTimerStart = -1L;

    private WaypointState() {
    }

    public static WaypointState getInstance() {
        return INSTANCE;
    }


    public boolean hasGroup() {
        return loadedGroup != null && !loadedGroup.waypoints.isEmpty();
    }

    public int size() {
        return loadedGroup == null ? 0 : loadedGroup.waypoints.size();
    }

    public WaypointPoint getCurrent() {
        if (!hasGroup()) return null;
        return loadedGroup.waypoints.get(Math.floorMod(currentIndex, size()));
    }

    public WaypointPoint getNext() {
        if (!hasGroup()) return null;
        return loadedGroup.waypoints.get(Math.floorMod(currentIndex + 1, size()));
    }

    public WaypointPoint getPrev() {
        if (!hasGroup()) return null;
        return loadedGroup.waypoints.get(Math.floorMod(currentIndex - 1, size()));
    }

    public int getNextIndex() {
        return Math.floorMod(currentIndex + 1, size());
    }

    public int getPrevIndex() {
        return Math.floorMod(currentIndex - 1, size());
    }


    public void load(WaypointGroup group) {
        this.loadedGroup = group;
        this.currentIndex = 0;
        this.advanceTimerStart = -1L;
    }

    public void unload() {
        this.loadedGroup = null;
        this.currentIndex = 0;
        this.advanceTimerStart = -1L;
    }

    public void reset() {
        this.currentIndex = 0;
        this.advanceTimerStart = -1L;
    }

    public void advance() {
        if (!hasGroup()) return;
        currentIndex = Math.floorMod(currentIndex + 1, size());
        advanceTimerStart = -1L;
    }

    public void skip(int n) {
        if (!hasGroup()) return;
        currentIndex = Math.floorMod(currentIndex + n, size());
        advanceTimerStart = -1L;
    }

    public void skipTo(int index) {
        if (!hasGroup() || index < 0 || index >= size()) return;
        currentIndex = index;
        advanceTimerStart = -1L;
    }
}