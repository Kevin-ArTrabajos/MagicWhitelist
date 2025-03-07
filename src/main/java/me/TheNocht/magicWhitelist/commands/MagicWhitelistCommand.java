package me.TheNocht.magicWhitelist.commands;

import me.TheNocht.magicWhitelist.MagicWhitelist;
import me.TheNocht.magicWhitelist.WhitelistManager;
import me.TheNocht.magicWhitelist.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class MagicWhitelistCommand implements CommandExecutor {
    private WhitelistManager whitelistManager;
    private MagicWhitelist magicWhitelist;

    public MagicWhitelistCommand(MagicWhitelist magicWhitelist) {
        this.whitelistManager = magicWhitelist.whitelistManager;
        this.magicWhitelist = magicWhitelist;
    }

    private void list(CommandSender sender) {
        var whitelistMap = whitelistManager.list();


        var sortedValues = whitelistMap.values().stream().sorted(Comparator.comparing(player -> player.role.level));
        var iterator = sortedValues.iterator();

        var returnList = new StringBuilder();
        int total = whitelistMap.size();

        returnList.append("Lista de usuarios en la lista blanca:\n");

        if (total == 0) {
            returnList.append("Ninguno.");
        }

        String lastRole = null;
        while (iterator.hasNext()) {
            var nextPlayer = iterator.next();

            if (lastRole == null) {
                lastRole = nextPlayer.role.name;
                returnList.append(nextPlayer.role.color).append(lastRole).append("<reset>:\n");
            }

            if (lastRole != null && !lastRole.equalsIgnoreCase(nextPlayer.role.name)) {
                returnList.append(nextPlayer.role.color).append(lastRole).append("<reset>:\n");
            }

            returnList.append(nextPlayer.username).append(", ");
        }

        sender.sendMessage(ChatUtils.format(returnList.toString()));
    }

    private void add(CommandSender sender) {

    }

    private void remove(CommandSender sender) {

    }

    private void setLevel(CommandSender sender) {

    }

    private void reload(CommandSender sender) {

    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("list")) {
            list(sender);
        }

        if (args[0].equalsIgnoreCase("add")) {
            add(sender);
        }

        if (args[0].equalsIgnoreCase("remove")) {
            remove(sender);
        }

        if (args[0].equalsIgnoreCase("setlevel")) {
            setLevel(sender);
        }

        if (args[0].equalsIgnoreCase("reload")) {
            reload(sender);
        }


        return false;
    }
}
