package me.TheNocht.magicWhitelist.commands;

import me.TheNocht.magicWhitelist.MagicWhitelist;
import me.TheNocht.magicWhitelist.structures.Roles;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MagicWhitelistCompleter implements TabCompleter {
    private MagicWhitelist magicWhitelist;

    public MagicWhitelistCompleter(MagicWhitelist magicWhitelist) {
        this.magicWhitelist = magicWhitelist;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("add", "remove", "list", "level", "setlevel", "parent", "setparent");
        }
        var sub = args[0];

        if (sub.equalsIgnoreCase("add")) {
            if (args.length == 2) {
                return this.magicWhitelist.getServer().getOnlinePlayers().stream().map(player -> player.getName()).toList();
            }
            if (args.length == 3) {
                return Roles.list().stream().toList();
            }
        }

        if (sub.equalsIgnoreCase("remove") && args.length == 2) {
            return this.magicWhitelist.getServer().getOnlinePlayers().stream().map(player -> player.getName()).toList();
        }

        if (sub.equalsIgnoreCase("setlevel")) {
            return List.of("0", "1", "2", "3");
        }

        if (sub.equalsIgnoreCase("setparent")) {
            if (args.length == 2) {
                return this.magicWhitelist.whitelistManager.list().values().stream().map(player -> player.username).toList();
            }
            if (args.length == 3) {
                return Roles.list().stream().toList();
            }
        }


        return List.of();
    }
}
