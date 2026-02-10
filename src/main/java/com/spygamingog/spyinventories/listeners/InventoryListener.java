package com.spygamingog.spyinventories.listeners;

import com.spygamingog.spyinventories.SpyInventories;
import com.spygamingog.spyinventories.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class InventoryListener implements Listener {
    private final java.util.Set<String> failedLoads = new java.util.HashSet<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom();
        World to = player.getWorld();

        String fromGroup = SpyInventories.getInstance().getGroupManager().getInventoryGroup(from.getName());
        String toGroup = SpyInventories.getInstance().getGroupManager().getInventoryGroup(to.getName());

        // If they changed to a world in a different inventory group
        if (!fromGroup.equalsIgnoreCase(toGroup)) {
            savePlayerData(player, fromGroup);
            loadPlayerData(player, toGroup);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String group = SpyInventories.getInstance().getGroupManager().getInventoryGroup(player.getWorld().getName());
        loadPlayerData(player, group);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String group = SpyInventories.getInstance().getGroupManager().getInventoryGroup(player.getWorld().getName());
        
        // Ensure data folder exists before saving
        File playerDir = new File(SpyInventories.getInstance().getDataFolder(), "players" + File.separator + player.getUniqueId());
        if (!playerDir.exists()) {
            playerDir.mkdirs();
        }
        
        savePlayerData(player, group);
    }

    private void savePlayerData(Player player, String groupName) {
        // Sanitize group name for file path (replace / with _)
        String fileName = groupName.replace("/", "_") + ".yml";
        
        // Prevent saving if loading failed previously to avoid overwriting data with empty inventory
        if (failedLoads.contains(player.getUniqueId() + "_" + fileName)) {
            SpyInventories.getInstance().getLogger().warning("Skipping save for " + player.getName() + " in group " + groupName + " because loading failed previously.");
            return;
        }

        File playerDir = new File(SpyInventories.getInstance().getDataFolder(), "players" + File.separator + player.getUniqueId());
        if (!playerDir.exists()) playerDir.mkdirs();
        
        File file = new File(playerDir, fileName);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("inventory", player.getInventory().getContents());
        config.set("armor", player.getInventory().getArmorContents());
        config.set("enderchest", player.getEnderChest().getContents());
        config.set("exp", player.getExp());
        config.set("level", player.getLevel());
        config.set("health", player.getHealth());
        config.set("food", player.getFoodLevel());
        config.set("gamemode", player.getGameMode().name());
        config.set("potion-effects", player.getActivePotionEffects());

        try {
            config.save(file);
            SpyInventories.getInstance().getLogger().info("Saved inventory for " + player.getName() + " in group " + groupName);
        } catch (IOException e) {
            SpyInventories.getInstance().getLogger().severe("Failed to save inventory for " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPlayerData(Player player, String groupName) {
        String fileName = groupName.replace("/", "_") + ".yml";
        String loadKey = player.getUniqueId() + "_" + fileName;
        failedLoads.remove(loadKey); // Reset failure state

        File playerDir = new File(SpyInventories.getInstance().getDataFolder(), "players" + File.separator + player.getUniqueId());
        File file = new File(playerDir, fileName);

        if (!file.exists()) {
            // No data for this group, clear everything to ensure isolation
            player.getInventory().clear();
            player.getEnderChest().clear();
            player.setExp(0);
            player.setLevel(0);
            player.setHealth(20);
            player.setFoodLevel(20);
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Verify data integrity
        if (!config.contains("inventory") || config.get("inventory") == null) {
            SpyInventories.getInstance().getLogger().severe("Failed to load inventory for " + player.getName() + " in group " + groupName + ": Missing 'inventory' key.");
            failedLoads.add(loadKey);
            return; // Abort loading to preserve current state (or maybe we should clear? But preserving is safer if it was a glitch)
        }

        try {
            // Clear existing potion effects
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            // Load data
            List<?> invList = (List<?>) config.get("inventory");
            if (invList != null) {
                ItemStack[] inv = invList.toArray(new ItemStack[0]);
                player.getInventory().setContents(inv);
            }

            List<?> armorList = (List<?>) config.get("armor");
            if (armorList != null) {
                ItemStack[] armor = armorList.toArray(new ItemStack[0]);
                player.getInventory().setArmorContents(armor);
            }

            List<?> ecList = (List<?>) config.get("enderchest");
            if (ecList != null) {
                ItemStack[] ec = ecList.toArray(new ItemStack[0]);
                player.getEnderChest().setContents(ec);
            }
            
            player.setExp((float) config.getDouble("exp"));
            player.setLevel(config.getInt("level"));
            player.setHealth(config.getDouble("health", 20));
            player.setFoodLevel(config.getInt("food", 20));
            
            String gmName = config.getString("gamemode");
            if (gmName != null) {
                try {
                    player.setGameMode(GameMode.valueOf(gmName));
                } catch (IllegalArgumentException ignored) {}
            }

            Collection<PotionEffect> effects = (Collection<PotionEffect>) config.get("potion-effects");
            if (effects != null) {
                player.addPotionEffects(effects);
            }
        } catch (Exception e) {
            SpyInventories.getInstance().getLogger().severe("Error loading data for " + player.getName() + " in group " + groupName + ": " + e.getMessage());
            e.printStackTrace();
            failedLoads.add(loadKey);
        }
    }
}
