package org.example.spawn.aSpawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class ASpawn extends JavaPlugin implements CommandExecutor {

    private Location spawnLocation;
    private int teleportDelay;
    private final String prefix = ChatColor.GRAY + "[" + ChatColor.GREEN + "Spawn" + ChatColor.GRAY + "] ";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        getCommand("setspawn").setExecutor(this);
        getCommand("spawn").setExecutor(this);
    }

    @Override
    public void onDisable() {

    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        teleportDelay = config.getInt("teleport-delay", 3);
        if (config.contains("spawn")) {
            spawnLocation = new Location(
                    Bukkit.getWorld(config.getString("spawn.world")),
                    config.getDouble("spawn.x"),
                    config.getDouble("spawn.y"),
                    config.getDouble("spawn.z"),
                    (float) config.getDouble("spawn.yaw"),
                    (float) config.getDouble("spawn.pitch")
            );
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
        Player player = (Player) sender;

        if (label.equalsIgnoreCase("setspawn")) {
            if (!player.hasPermission("aspawn.setspawn")) {
                player.sendMessage(prefix + ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }
            spawnLocation = player.getLocation();
            FileConfiguration config = getConfig();
            config.set("spawn.world", spawnLocation.getWorld().getName());
            config.set("spawn.x", spawnLocation.getX());
            config.set("spawn.y", spawnLocation.getY());
            config.set("spawn.z", spawnLocation.getZ());
            config.set("spawn.yaw", spawnLocation.getYaw());
            config.set("spawn.pitch", spawnLocation.getPitch());
            saveConfig();
            player.sendMessage(prefix + ChatColor.GREEN + "The spawn point has been set!");
            return true;
        }

        if (label.equalsIgnoreCase("spawn")) {
            if (spawnLocation == null) {
                player.sendMessage(prefix + ChatColor.RED + "The spawn point is not set!");
                return true;
            }
            player.sendMessage(prefix + ChatColor.YELLOW + "You are teleporting to the spawn... in " + teleportDelay + " seconds.");

            for (int i = teleportDelay; i > 0; i--) {
                final int countdown = i;
                Bukkit.getScheduler().runTaskLater(this, () -> player.sendMessage(prefix + ChatColor.GOLD + countdown + "..."), (teleportDelay - i) * 20L);
            }

            Bukkit.getScheduler().runTaskLater(this, () -> {
                player.teleport(spawnLocation);
                player.sendMessage(prefix + ChatColor.GREEN + "You have successfully teleported to the spawn point.!");
            }, teleportDelay * 20L);
            return true;
        }

        return false;
    }
}