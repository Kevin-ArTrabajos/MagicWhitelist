package me.TheNocht.magicWhitelist;

import me.TheNocht.magicWhitelist.commands.MagicWhitelistCommand;
import me.TheNocht.magicWhitelist.commands.MagicWhitelistCompleter;
import me.TheNocht.magicWhitelist.listeners.PlayerLoginEvent;
import me.TheNocht.magicWhitelist.structures.DatabaseManager;
import me.TheNocht.magicWhitelist.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class MagicWhitelist extends JavaPlugin {
    public WhitelistManager whitelistManager;
    public DatabaseManager databaseManager;
    public final String prefix = "<gold>[<aqua>MagicWhitelist<gold>]<reset> ";
    public final String consolePrefix = "[MagicWhitelist] ";

    @Override
    public void onEnable() {
        var manager = Bukkit.getPluginManager();
        var console = this.getServer().getConsoleSender();

        this.whitelistManager = new WhitelistManager();
        manager.registerEvents(new PlayerLoginEvent(this), this);

        var command = this.getCommand("magicwhitelist");
        if (command != null) {
            command.setExecutor(new MagicWhitelistCommand(this));
            command.setTabCompleter(new MagicWhitelistCompleter(this));
        }

        try {
            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdirs();
            }

            this.databaseManager = new DatabaseManager(this.getDataFolder().getAbsolutePath() + "/whitelist.db", this);
        } catch (Exception e) {
            console.sendMessage(ChatUtils.format("%s<red>Failed to load database. Disabling plugin.".formatted(consolePrefix)));
            Bukkit.getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }

        if (Bukkit.hasWhitelist()) {
            Bukkit.setWhitelist(false);
            console.sendMessage(ChatUtils.format("%s<yellow>Server vanilla whitelist was disabled. Please do not use the vanilla whitelist.".formatted(consolePrefix)));
        }

        try {
            var accessLevel = this.databaseManager.getAccessLevel();
            this.whitelistManager.setAccessLevel(accessLevel);

            var playerList = this.databaseManager.getAllPlayers();
            playerList.forEach(player -> this.whitelistManager.addPlayer(player));
        } catch (Exception e) {
            console.sendMessage(ChatUtils.format("%s<red>Failed to load players from database. Disabling plugin.".formatted(consolePrefix)));
            Bukkit.getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }

        console.sendMessage(ChatUtils.format("%s<green>> successfully loaded.".formatted(consolePrefix)));
    }

    @Override
    public void onDisable() {
        this.whitelistManager = null;

        try {
            if (databaseManager != null) {
                databaseManager.closeConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.getServer().getConsoleSender().sendMessage(ChatUtils.format("%s <red>> successfully unloaded.".formatted(consolePrefix)));
    }
}
