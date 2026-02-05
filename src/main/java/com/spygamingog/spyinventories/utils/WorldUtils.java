package com.spygamingog.spyinventories.utils;

public class WorldUtils {

    /**
     * Gets the base name of a world by removing suffixes like _nether and _the_end.
     * Works with container paths as well (e.g., spycore-worlds/Surv/survival_nether -> spycore-worlds/Surv/survival)
     */
    public static String getBaseWorldName(String worldName) {
        if (worldName == null) return null;
        
        String base = worldName;
        if (base.endsWith("_nether")) {
            base = base.substring(0, base.length() - 7);
        } else if (base.endsWith("_the_end")) {
            base = base.substring(0, base.length() - 8);
        }
        
        return base;
    }

    /**
     * Determines if two worlds belong to the same inventory group.
     */
    public static boolean isSameGroup(String world1, String world2) {
        if (world1 == null || world2 == null) return false;
        return getBaseWorldName(world1).equalsIgnoreCase(getBaseWorldName(world2));
    }
}
