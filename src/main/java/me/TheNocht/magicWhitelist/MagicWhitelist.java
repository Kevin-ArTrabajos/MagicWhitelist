package me.TheNocht.magicWhitelist;

import me.TheNocht.magicWhitelist.commands.MagicWhitelistCommand;
import me.TheNocht.magicWhitelist.commands.MagicWhitelistCompleter;
import me.TheNocht.magicWhitelist.listeners.PlayerLoginEvent;
import me.TheNocht.magicWhitelist.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MagicWhitelist extends JavaPlugin {
    public WhitelistManager whitelistManager;
    public final String prefix = "<gold>[<aqua>MagicWhitelist<gold>]<reset> ";

    @Override
    public void onEnable() {
        var manager = Bukkit.getPluginManager();

        this.whitelistManager = new WhitelistManager();
        manager.registerEvents(new PlayerLoginEvent(this), this);

        var command = this.getCommand("magicwhitelist");
        if (command != null) {
            command.setExecutor(new MagicWhitelistCommand(this));
            command.setTabCompleter(new MagicWhitelistCompleter());
        }
    }

    @Override
    public void onDisable() {
        this.getServer().getConsoleSender().sendMessage(ChatUtils.format("<gold>%s <green>> successfully unloaded.".formatted(this.getName())));
        this.whitelistManager = null;
    }
}
