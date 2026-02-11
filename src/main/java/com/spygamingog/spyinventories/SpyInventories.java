package com.spygamingog.spyinventories;

import com.spygamingog.spyinventories.listeners.InventoryListener;
import com.spygamingog.spyinventories.managers.GroupManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SpyInventories extends JavaPlugin {

    @Getter
    private static SpyInventories instance;

    @Getter
    private GroupManager groupManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Create data folder
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Initialize managers
        this.groupManager = new GroupManager(this);

        // Register listeners
        // getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        getLogger().info("SpyInventories v1.0.3 enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SpyInventories v1.0.3 disabled!");
    }
}
