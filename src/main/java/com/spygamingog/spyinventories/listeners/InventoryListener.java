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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom();
        World to = player.getWorld();

        String fromBase = WorldUtils.getBaseWorldName(from.getName());
        String toBase = WorldUtils.getBaseWorldName(to.getName());

        // If they changed to a world in a different group
        if (!fromBase.equalsIgnoreCase(toBase)) {
            savePlayerData(player, fromBase);
            loadPlayerData(player, toBase);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String base = WorldUtils.getBaseWorldName(player.getWorld().getName());
        loadPlayerData(player, base);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String base = WorldUtils.getBaseWorldName(player.getWorld().getName());
        savePlayerData(player, base);
    }

    private void savePlayerData(Player player, String groupName) {
        // Sanitize group name for file path (replace / with _)
        String fileName = groupName.replace("/", "_") + ".yml";
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlayerData(Player player, String groupName) {
        String fileName = groupName.replace("/", "_") + ".yml";
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

        // Clear existing potion effects
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        // Load data
        ItemStack[] inv = ((List<ItemStack>) config.get("inventory")).toArray(new ItemStack[0]);
        ItemStack[] armor = ((List<ItemStack>) config.get("armor")).toArray(new ItemStack[0]);
        ItemStack[] ec = ((List<ItemStack>) config.get("enderchest")).toArray(new ItemStack[0]);

        player.getInventory().setContents(inv);
        player.getInventory().setArmorContents(armor);
        player.getEnderChest().setContents(ec);
        
        player.setExp((float) config.getDouble("exp"));
        player.setLevel(config.getInt("level"));
        player.setHealth(config.getDouble("health"));
        player.setFoodLevel(config.getInt("food"));
        
        String gmName = config.getString("gamemode");
        if (gmName != null) {
            player.setGameMode(GameMode.valueOf(gmName));
        }

        Collection<PotionEffect> effects = (Collection<PotionEffect>) config.get("potion-effects");
        if (effects != null) {
            player.addPotionEffects(effects);
        }
    }
}
