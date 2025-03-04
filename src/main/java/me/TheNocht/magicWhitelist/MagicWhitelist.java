package me.TheNocht.magicWhitelist;

import me.TheNocht.magicWhitelist.listeners.PlayerLoginListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class MagicWhitelist extends JavaPlugin {

    @Override
    public void onEnable() {
       var manager = Bukkit.getPluginManager();
       manager.registerEvents(new PlayerLoginListener(), this);
    }

    @Override
    public void onDisable() {
        this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6" + this.getName() + " &b> &7successfully unloaded." ));
    }
}
