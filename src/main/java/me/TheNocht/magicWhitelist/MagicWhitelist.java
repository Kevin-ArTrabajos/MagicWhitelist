package me.TheNocht.magicWhitelist;

import me.TheNocht.magicWhitelist.commands.MagicWhitelistCommand;
import me.TheNocht.magicWhitelist.listeners.PlayerLoginEvent;
import me.TheNocht.magicWhitelist.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MagicWhitelist extends JavaPlugin {
    public WhitelistManager whitelistManager;

    @Override
    public void onEnable() {
        var manager = Bukkit.getPluginManager();

        this.whitelistManager = new WhitelistManager();
        manager.registerEvents(new PlayerLoginEvent(this.whitelistManager), this);

        this.getCommand("magicwhitelist").setExecutor(new MagicWhitelistCommand(this));
    }

    @Override
    public void onDisable() {
        this.getServer().getConsoleSender().sendMessage(ChatUtils.format("<gold>" + this.getName() + " <green>> successfully unloaded."));
    }
}
