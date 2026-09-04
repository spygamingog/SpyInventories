package com.spygamingog.spyinventories;

import com.spygamingog.spyinventories.listeners.InventoryListener;
import com.spygamingog.spyinventories.managers.GroupManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SpyInventories extends JavaPlugin {

    private static SpyInventories instance;
    private GroupManager groupManager;

    public static SpyInventories getInstance() {
        return instance;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        // Create data folder
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Initialize managers
        this.groupManager = new GroupManager(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);

        getLogger().info("SpyInventories v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SpyInventories v" + getDescription().getVersion() + " disabled!");
    }
}
