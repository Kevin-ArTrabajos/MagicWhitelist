package me.TheNocht.magicWhitelist.structures;

import org.bukkit.command.CommandSender;

import java.util.UUID;

public class WhitelistedPlayer {
    public String username = "";
    public UUID uuid;
    public Long timestamp = 0L;
    public WhitelistRole role;
    public CommandSender addBy;

    public WhitelistedPlayer(String username, UUID uuid, CommandSender addBy) {
        this.username = username;
        this.uuid = uuid;
        this.timestamp = System.currentTimeMillis();
        this.addBy = addBy;
        this.role = Roles.DEFAULT;
    }

    public WhitelistedPlayer(String username, UUID uuid, CommandSender addBy, WhitelistRole role) {
        this.username = username;
        this.uuid = uuid;
        this.timestamp = System.currentTimeMillis();
        this.role = role;
        this.addBy = addBy;
    }

    public WhitelistedPlayer(String username, UUID uuid, Long timestamp, CommandSender addBy, WhitelistRole role) {
        this.username = username;
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.addBy = addBy;
        this.role = role;
    }
}
