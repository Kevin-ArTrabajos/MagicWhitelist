package me.TheNocht.magicWhitelist.structures;

import java.util.Collection;
import java.util.List;

public record Roles() {
    public static final WhitelistRole DEFAULT = new WhitelistRole("default", "white");
    public static final WhitelistRole PLAYER = new WhitelistRole("player", 1, "dark_purple");
    public static final WhitelistRole STAFF = new WhitelistRole("staff", 2, "blue");
    public static final WhitelistRole ADMIN = new WhitelistRole("admin", 3, "red");

    public static WhitelistRole getRole(String name) throws Exception {
        return switch (name.toLowerCase()) {
            case "default" -> DEFAULT;
            case "player" -> PLAYER;
            case "staff" -> STAFF;
            case "admin" -> ADMIN;
            default -> throw new Exception("Invalid role");
        };
    }

    public static WhitelistRole getRole(Integer level) throws Exception {
        return switch (level) {
            case 0 -> DEFAULT;
            case 1 -> PLAYER;
            case 2 -> STAFF;
            case 3 -> ADMIN;
            default -> throw new Exception("Invalid role");
        };
    }

    public static String safeGetRoleName(Integer level) {
        return switch (level) {
            case 0 -> DEFAULT.name;
            case 1 -> PLAYER.name;
            case 2 -> STAFF.name;
            case 3 -> ADMIN.name;
            default -> "invalid";
        };
    }

    public static Number getLimitMax() {
        return ADMIN.level;
    }

    public static Collection<String> list() {
        return List.of(DEFAULT.n(), PLAYER.n(), STAFF.n(), ADMIN.n());
    }
}
