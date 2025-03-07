package me.TheNocht.magicWhitelist;

import me.TheNocht.magicWhitelist.structures.WhitelistRole;
import me.TheNocht.magicWhitelist.structures.WhitelistedPlayer;
import me.TheNocht.magicWhitelist.utils.AlreadyWhitelistedEx;
import me.TheNocht.magicWhitelist.utils.PlayerNotFoundEx;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WhitelistManager {
    private final Map<UUID, WhitelistedPlayer> currentPlayers = new HashMap<>();
    private int doorLevel = 0;

    public void addPlayer(String username, WhitelistRole role, CommandSender addBy) throws RuntimeException {
        var uuid = Bukkit.getPlayerUniqueId(username);

        if (uuid == null) {
            throw new PlayerNotFoundEx("Player " + username + " not found");
        }

        if (this.currentPlayers.containsKey(uuid)) {
            throw new AlreadyWhitelistedEx("Player " + username + " already whitelisted");
        }

        this.currentPlayers.put(uuid, new WhitelistedPlayer(username, uuid, addBy, role));
    }

    public void removePlayer(UUID uuid) {
        if (!this.currentPlayers.containsKey(uuid)) {
            return;
        }
        this.currentPlayers.remove(uuid);
    }

    public void removePlayer(String username) {
        this.currentPlayers.entrySet().removeIf(entry -> entry.getValue().username.equals(username));
    }

    public boolean isWhitelisted(UUID uuid) {
        return this.currentPlayers.containsKey(uuid);
    }

    public boolean isAuthorizedToJoin(UUID uuid) {
        if (!this.currentPlayers.containsKey(uuid)) {
            return false;
        }

        var player = this.currentPlayers.get(uuid);

        return player.role.level >= this.getDoorLevel();
    }

    public Map<UUID, WhitelistedPlayer> list() {
        return new HashMap<>(this.currentPlayers);
    }

    public int getDoorLevel() {
        return doorLevel;
    }

    public void setDoorLevel(int newDoorLevel) {
        if (newDoorLevel < 0) {
            throw new IllegalArgumentException("Gates level cannot be negative");
        }

        this.doorLevel = newDoorLevel;
    }
}
