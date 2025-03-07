package me.TheNocht.magicWhitelist.structures;

public class WhitelistRole {
    public final String name;
    public final Integer level;
    public final String color;

    public WhitelistRole(String name, Integer level, String color) {
        this.name = name;
        this.level = level;
        this.color = "<" + color + ">";
    }

    public WhitelistRole(String name, String color) {
        this.name = name;
        this.level = 0;
        this.color = "<" + color + ">";
    }
}
