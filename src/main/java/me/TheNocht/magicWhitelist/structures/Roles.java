package me.TheNocht.magicWhitelist.structures;

public record Roles() {
    public static final WhitelistRole DEFAULT = new WhitelistRole("default", "white");
    public static final WhitelistRole COMPETITOR = new WhitelistRole("competitor", 1, "green");
    public static final WhitelistRole DEV = new WhitelistRole("developer", 2, "blue");
    public static final WhitelistRole ADMIN = new WhitelistRole("admin", 3, "red");
}
