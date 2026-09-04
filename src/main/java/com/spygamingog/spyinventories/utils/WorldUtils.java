package com.spygamingog.spyinventories.utils;

public class WorldUtils {

    /**
     * Gets the base name of a world by removing suffixes like _nether and _the_end.
     * Works with container paths as well (e.g., spycore-worlds/Surv/survival_nether -> spycore-worlds/Surv/survival)
     */
    public static String getBaseWorldName(String worldName) {
        if (worldName == null) return null;
        
        String lower = worldName.toLowerCase();
        if (lower.endsWith("_nether")) {
            return worldName.substring(0, worldName.length() - 7);
        } else if (lower.endsWith("_the_end")) {
            return worldName.substring(0, worldName.length() - 8);
        }
        
        return worldName;
    }

    /**
     * Determines if two worlds belong to the same inventory group.
     */
    public static boolean isSameGroup(String world1, String world2) {
        if (world1 == null || world2 == null) return false;
        return getBaseWorldName(world1).equalsIgnoreCase(getBaseWorldName(world2));
    }
}
