package com.spygamingog.spyinventories;

import com.spygamingog.spyinventories.listeners.InventoryListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SpyInventories extends JavaPlugin {

    @Getter
    private static SpyInventories instance;

    @Override
    public void onEnable() {
        instance = this;
        
        // Create data folder
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File groupsDir = new File(getDataFolder(), "groups");
        if (!groupsDir.exists()) {
            groupsDir.mkdirs();
        }

        // Register listeners
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        getLogger().info("SpyInventories v1.0.1 enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SpyInventories v1.0.1 disabled!");
    }
}
