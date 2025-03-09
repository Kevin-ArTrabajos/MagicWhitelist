package me.TheNocht.magicWhitelist.listeners;

import me.TheNocht.magicWhitelist.MagicWhitelist;
import me.TheNocht.magicWhitelist.WhitelistManager;
import me.TheNocht.magicWhitelist.structures.Roles;
import me.TheNocht.magicWhitelist.utils.ChatUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent.Result;


public class PlayerLoginEvent implements Listener {
    MagicWhitelist magicWhitelist;
    WhitelistManager whitelistManager;


    public PlayerLoginEvent(MagicWhitelist magicWhitelist) {
        this.magicWhitelist = magicWhitelist;
        this.whitelistManager = magicWhitelist.whitelistManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLoginEvent(org.bukkit.event.player.PlayerLoginEvent e) {
        var uuid = e.getPlayer().getUniqueId();

        if (whitelistManager.isEveryoneJoineable()) {
            e.allow();
            return;
        }

        if (e.getPlayer().hasPermission("magicwhitelist.admin") || e.getPlayer().hasPermission("magicwhitelist.bypass")) {
            e.allow();
            return;
        }

        if (!whitelistManager.isWhitelisted(uuid)) {
            e.disallow(Result.KICK_WHITELIST, ChatUtils.format("<red>You are not whitelisted on this server."));
            magicWhitelist.getLogger().info("Player %s is not whitelisted, access level: %d (%s)".formatted(e.getPlayer().getName(), whitelistManager.getAccessLevel(), Roles.safeGetRoleName(whitelistManager.getAccessLevel())));
            return;
        }

        if (!whitelistManager.isAuthorizedToJoin(uuid)) {
            e.disallow(Result.KICK_WHITELIST, ChatUtils.format("<red>You do not have permissions to join the server."));
            magicWhitelist.getLogger().info("Player %s is not authorized to join. Player level: %d (%s), access level: %d (%s)".formatted(
                    e.getPlayer().getName(),
                    whitelistManager.getPlayer(uuid).role.level,
                    Roles.safeGetRoleName(whitelistManager.getPlayer(uuid).role.level),
                    whitelistManager.getAccessLevel(),
                    Roles.safeGetRoleName(whitelistManager.getAccessLevel())));
            return;
        }

        e.allow();
    }
}
