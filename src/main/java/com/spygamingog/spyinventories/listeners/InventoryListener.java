package com.spygamingog.spyinventories.listeners;

import com.spygamingog.spycore.api.SpyAPI;
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
    private final SpyInventories plugin;
    private final java.util.Set<String> failedLoads = new java.util.HashSet<>();

    public InventoryListener(SpyInventories plugin) {
        this.plugin = plugin;
    }

    private boolean isIgnored(World world) {
        if (world == null) return true;
        
        // Get the alias (represented as real name)
        String alias = SpyAPI.getAliasForWorld(world);
        if (alias == null) alias = world.getName();
        
        FileConfiguration config = plugin.getConfig();
        
        // Check ignored-worlds
        List<String> ignoredWorlds = config.getStringList("ignored-worlds");
        for (String ignored : ignoredWorlds) {
            if (alias.equalsIgnoreCase(ignored)) return true;
        }
        
        // Check ignored-suffixes (match_, temp_)
        List<String> ignoredSuffixes = config.getStringList("ignored-suffixes");
        for (String suffix : ignoredSuffixes) {
            if (alias.toLowerCase().contains(suffix.toLowerCase())) return true;
        }
        
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom();
        World to = player.getWorld();

        String fromGroup = plugin.getGroupManager().getInventoryGroup(from.getName());
        String toGroup = plugin.getGroupManager().getInventoryGroup(to.getName());

        // If they changed to a world in a different inventory group
        if (!fromGroup.equalsIgnoreCase(toGroup)) {
            // Save data for the old group (only if old world was NOT ignored)
            if (!isIgnored(from)) {
                savePlayerData(player, fromGroup);
            } else {
                plugin.getLogger().info("Skipping inventory save for " + player.getName() + " - World " + from.getName() + " (from) is ignored.");
            }
            
            // Load data for the new group (only if new world is NOT ignored)
            if (!isIgnored(to)) {
                loadPlayerData(player, toGroup);
            } else {
                plugin.getLogger().info("Skipping inventory load for " + player.getName() + " - World " + to.getName() + " (to) is ignored.");
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        
        if (isIgnored(world)) {
            plugin.getLogger().info("Skipping inventory load for " + player.getName() + " on join - World " + world.getName() + " is ignored.");
            return;
        }
        
        String group = plugin.getGroupManager().getInventoryGroup(world.getName());
        loadPlayerData(player, group);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        
        if (isIgnored(world)) {
            plugin.getLogger().info("Skipping inventory save for " + player.getName() + " on quit - World " + world.getName() + " is ignored.");
            return;
        }
        
        String group = plugin.getGroupManager().getInventoryGroup(world.getName());
        
        // Ensure data folder exists before saving
        File playerDir = new File(plugin.getDataFolder(), "players" + File.separator + player.getUniqueId());
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
            plugin.getLogger().warning("Skipping save for " + player.getName() + " in group " + groupName + " because loading failed previously.");
            return;
        }

        File playerDir = new File(plugin.getDataFolder(), "players" + File.separator + player.getUniqueId());
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
            plugin.getLogger().info("Saved inventory for " + player.getName() + " in group " + groupName);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save inventory for " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPlayerData(Player player, String groupName) {
        String fileName = groupName.replace("/", "_") + ".yml";
        File playerDir = new File(plugin.getDataFolder(), "players" + File.separator + player.getUniqueId());
        File file = new File(playerDir, fileName);
        
        if (!file.exists()) {
            // New player or first time in this group, clear inventory
            clearPlayerData(player);
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        try {
            // Load inventory
            List<ItemStack> invItems = (List<ItemStack>) config.get("inventory");
            if (invItems != null) {
                player.getInventory().setContents(invItems.toArray(new ItemStack[0]));
            } else {
                player.getInventory().clear();
            }

            // Load armor
            List<ItemStack> armorItems = (List<ItemStack>) config.get("armor");
            if (armorItems != null) {
                player.getInventory().setArmorContents(armorItems.toArray(new ItemStack[0]));
            } else {
                player.getInventory().setArmorContents(null);
            }

            // Load enderchest
            List<ItemStack> ecItems = (List<ItemStack>) config.get("enderchest");
            if (ecItems != null) {
                player.getEnderChest().setContents(ecItems.toArray(new ItemStack[0]));
            } else {
                player.getEnderChest().clear();
            }

            // Load stats
            player.setExp((float) config.getDouble("exp", 0));
            player.setLevel(config.getInt("level", 0));
            player.setHealth(config.getDouble("health", 20));
            player.setFoodLevel(config.getInt("food", 20));

            // Load gamemode
            String gmName = config.getString("gamemode");
            if (gmName != null) {
                player.setGameMode(GameMode.valueOf(gmName));
            } else {
                // Default to survival if not specified
                player.setGameMode(GameMode.SURVIVAL);
            }

            // Load potion effects
            Collection<PotionEffect> effects = (Collection<PotionEffect>) config.get("potion-effects");
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            if (effects != null) {
                player.addPotionEffects(effects);
            }

            plugin.getLogger().info("Loaded inventory for " + player.getName() + " in group " + groupName);
            failedLoads.remove(player.getUniqueId() + "_" + fileName);
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load inventory for " + player.getName() + ": " + e.getMessage());
            failedLoads.add(player.getUniqueId() + "_" + fileName);
            e.printStackTrace();
        }
    }

    private void clearPlayerData(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItemInOffHand(null); // Ensure offhand is cleared
        player.getEnderChest().clear();
        player.setExp(0);
        player.setLevel(0);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }
}
