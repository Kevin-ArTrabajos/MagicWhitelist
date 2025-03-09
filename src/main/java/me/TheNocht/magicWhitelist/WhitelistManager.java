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
    private int accessLevel = 0;

    public WhitelistedPlayer addPlayer(String username, WhitelistRole role, CommandSender addBy) throws RuntimeException {
        var uuid = Bukkit.getPlayerUniqueId(username);

        if (uuid == null) {
            throw new PlayerNotFoundEx("Player %s not found".formatted(username));
        }

        if (this.currentPlayers.containsKey(uuid)) {
            throw new AlreadyWhitelistedEx("Player %s already whitelisted".formatted(username));
        }

        var player = new WhitelistedPlayer(username, uuid, addBy, role);
        this.currentPlayers.put(uuid, player);
        return player;
    }

    public boolean removePlayer(UUID uuid) {
        if (!this.currentPlayers.containsKey(uuid)) {
            return false;
        }
        this.currentPlayers.remove(uuid);
        return true;
    }

    public boolean removePlayer(String username) {
        return this.currentPlayers.entrySet().removeIf(entry -> entry.getValue().username.equals(username));
    }

    public boolean isEveryoneJoineable() {
        return this.accessLevel == 0;
    }

    public boolean isWhitelisted(UUID uuid) {
        return this.currentPlayers.containsKey(uuid);
    }

    public boolean isWhitelisted(String username) {
        return this.currentPlayers.values().stream().anyMatch(player -> player.username.equalsIgnoreCase(username));
    }

    public boolean isAuthorizedToJoin(UUID uuid) {
        if (!this.currentPlayers.containsKey(uuid)) {
            return false;
        }

        var player = this.currentPlayers.get(uuid);

        return player.role.level >= this.getAccessLevel();
    }

    public Map<UUID, WhitelistedPlayer> list() {
        return new HashMap<>(this.currentPlayers);
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int newDoorLevel) {
        if (newDoorLevel < 0) {
            throw new IllegalArgumentException("Gates level cannot be negative");
        }

        this.accessLevel = newDoorLevel;
    }

    public WhitelistedPlayer getPlayer(UUID uuid) {
        return this.currentPlayers.get(uuid);
    }

    public WhitelistedPlayer getPlayer(String username) {
        return this.currentPlayers.values().stream().filter(player -> player.username.equalsIgnoreCase(username)).findFirst().orElse(null);
    }
}
