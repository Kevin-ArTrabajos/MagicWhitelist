package me.TheNocht.magicWhitelist.listeners;

import me.TheNocht.magicWhitelist.WhitelistManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent.Result;


public class PlayerLoginEvent implements Listener {
    WhitelistManager whitelistManager;


    public PlayerLoginEvent(WhitelistManager whitelistManager) {
        this.whitelistManager = whitelistManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLoginEvent(org.bukkit.event.player.PlayerLoginEvent e) {
        var uuid = e.getPlayer().getUniqueId();

        if (whitelistManager.isAuthorizedToJoin(uuid)) {
            e.disallow(Result.KICK_WHITELIST, "You are not whitelisted on this server.");
        }

        e.allow();
    }
}
