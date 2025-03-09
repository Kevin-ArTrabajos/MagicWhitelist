package me.TheNocht.magicWhitelist.commands;

import me.TheNocht.magicWhitelist.structures.Roles;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MagicWhitelistCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("add", "remove", "list", "level", "setlevel", "parent", "setparent");
        }
        var sub = args[0];

        if (sub.equalsIgnoreCase("add") && args.length == 3) {
            return Roles.list().stream().toList();
        }

        if (sub.equalsIgnoreCase("remove") && args.length == 2) {
            return List.of("all");
        }

        if (sub.equalsIgnoreCase("setlevel")) {
            return List.of("0", "1", "2", "3");
        }

        if (sub.equalsIgnoreCase("setparent") && args.length == 3) {
            return Roles.list().stream().toList();
        }


        return List.of();
    }
}
