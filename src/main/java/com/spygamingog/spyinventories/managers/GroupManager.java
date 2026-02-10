package com.spygamingog.spyinventories.managers;

import com.spygamingog.spyinventories.SpyInventories;
import com.spygamingog.spyinventories.utils.WorldUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GroupManager {
    private final SpyInventories plugin;
    private final File configFile;
    private FileConfiguration config;
    private final Map<String, String> worldToGroup = new HashMap<>();

    public GroupManager(SpyInventories plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "groups.yml");
        loadGroups();
    }

    public void loadGroups() {
        if (!configFile.exists()) {
            plugin.saveResource("groups.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        worldToGroup.clear();

        if (config.getConfigurationSection("groups") != null) {
            for (String groupName : config.getConfigurationSection("groups").getKeys(false)) {
                List<String> worlds = config.getStringList("groups." + groupName + ".worlds");
                for (String world : worlds) {
                    worldToGroup.put(world.toLowerCase(), groupName);
                }
            }
        }
    }

    public String getInventoryGroup(String worldName) {
        if (worldName == null) return "default";
        
        // 1. Check for manual group assignment
        String manualGroup = worldToGroup.get(worldName.toLowerCase());
        if (manualGroup != null) {
            return manualGroup;
        }

        // 2. Fallback to automatic suffix grouping (_nether, _the_end)
        return WorldUtils.getBaseWorldName(worldName);
    }

    public void addWorldToGroup(String worldName, String groupName) {
        List<String> worlds = config.getStringList("groups." + groupName + ".worlds");
        if (!worlds.contains(worldName)) {
            worlds.add(worldName);
            config.set("groups." + groupName + ".worlds", worlds);
            saveConfig();
            worldToGroup.put(worldName.toLowerCase(), groupName);
        }
    }

    public void removeWorldFromGroup(String worldName) {
        String groupName = worldToGroup.remove(worldName.toLowerCase());
        if (groupName != null) {
            List<String> worlds = config.getStringList("groups." + groupName + ".worlds");
            worlds.remove(worldName);
            config.set("groups." + groupName + ".worlds", worlds);
            saveConfig();
        }
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save groups.yml!");
        }
    }
}
