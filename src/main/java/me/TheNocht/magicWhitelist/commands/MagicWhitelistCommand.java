package me.TheNocht.magicWhitelist.commands;

import me.TheNocht.magicWhitelist.MagicWhitelist;
import me.TheNocht.magicWhitelist.WhitelistManager;
import me.TheNocht.magicWhitelist.structures.Roles;
import me.TheNocht.magicWhitelist.structures.WhitelistRole;
import me.TheNocht.magicWhitelist.structures.WhitelistedPlayer;
import me.TheNocht.magicWhitelist.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MagicWhitelistCommand implements CommandExecutor {
    private final WhitelistManager whitelistManager;
    private final MagicWhitelist magicWhitelist;

    public MagicWhitelistCommand(MagicWhitelist magicWhitelist) {
        this.whitelistManager = magicWhitelist.whitelistManager;
        this.magicWhitelist = magicWhitelist;
    }

    private void list(CommandSender sender) {
        Map<String, List<WhitelistedPlayer>> listRoles = new HashMap<>();
        var whitelistMap = whitelistManager.list();
        var sortedValues = whitelistMap.values().stream().sorted(Comparator.comparing(player -> player.role.level));
        var sortedValsI = sortedValues.iterator();


        String lastRole = null;
        while (sortedValsI.hasNext()) {
            var nextPlayer = sortedValsI.next();

            if (lastRole == null || !lastRole.equalsIgnoreCase(nextPlayer.role.name)) {
                lastRole = nextPlayer.role.name;
                listRoles.put(lastRole, new ArrayList<>(List.of(nextPlayer)));
                continue;
            }

            var list = listRoles.get(lastRole);
            list.add(nextPlayer);
        }

        String prefix = "%sLista de usuarios en la lista blanca:\n".formatted(magicWhitelist.prefix);
        if (whitelistMap.isEmpty()) {
            sender.sendMessage(ChatUtils.format("%sNinguno.".formatted(prefix)));
            return;
        }

        var listRolesI = listRoles.values().iterator();
        var returnList = new StringBuilder().append(prefix);
        while (listRolesI.hasNext()) {
            var players = listRolesI.next();
            var role = players.get(0).role;

            returnList.append("-%s%s<reset>:\n".formatted(role.color, role.name));
            returnList.append(String.join("%s, <reset>", players.stream().map(player -> player.username).toList()).formatted(role.color)).append("\n");
        }

        sender.sendMessage(ChatUtils.format(returnList.toString()));
    }

    private void add(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatUtils.format("<white>Usage /magicwhitelist add [username] [role]"));
            return;
        }

        var username = args[1];
        WhitelistRole role;

        try {
            role = Roles.getRole(args[2]);
        } catch (Exception error) {
            var list = Roles.list();
            sender.sendMessage(ChatUtils.format("%s<red>Rol invalido, roles validos:\n<gold>%s".formatted(magicWhitelist.prefix, String.join(", ", list))));
            return;
        }

        WhitelistedPlayer player;

        try {
            player = whitelistManager.addPlayer(username, role, sender);
        } catch (RuntimeException exception) {
            sender.sendMessage(ChatUtils.format("<red>%s".formatted(exception.getMessage())));
            return;
        }

        sender.sendMessage(ChatUtils.format("%s<green>Usuario %s (%s) añadido a la lista blanca del rol %s".formatted(magicWhitelist.prefix, username, player.uuid, role.name)));
        Bukkit.broadcast(ChatUtils.format("%s<gray>Whitelisted player <gold>%s <gray>(%s) with role %s".formatted(magicWhitelist.prefix, username, player.uuid, role.name)), "magicwhitelist.admin");
    }

    private void remove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatUtils.format("<white>Usage /magicwhitelist remove [username]"));
            return;
        }

        var username = args[1];

        var isRemoved = whitelistManager.removePlayer(username);

        if (!isRemoved) {
            sender.sendMessage(ChatUtils.format("%s<red>Usuario %s no encontrado en la lista blanca".formatted(magicWhitelist.prefix, username)));
            return;
        }

        sender.sendMessage(ChatUtils.format("%s<green>Usuario %s removido de la lista blanca".formatted(magicWhitelist.prefix, username)));
        Bukkit.broadcast(ChatUtils.format("%s<gray>Whitelisted player <gold>%s <gray> removed".formatted(magicWhitelist.prefix, username)), "magicwhitelist.admin");
    }

    private void setLevel(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatUtils.format("<white>Usage /magicwhitelist setlevel [level]"));
            return;
        }

        int newLevel;
        WhitelistRole role;

        try {
            newLevel = Integer.parseInt(args[1]);
            role = Roles.getRole(newLevel);
        } catch (NumberFormatException error) {
            sender.sendMessage(ChatUtils.format("<red>El nivel debe ser un numero"));
            return;
        } catch (Exception e) {
            sender.sendMessage(ChatUtils.format("<red>Nivel de rol invalido, valores validos <gold>0 - %s".formatted(Roles.getLimitMax())));
            return;
        }

        whitelistManager.setAccessLevel(newLevel);

        sender.sendMessage(ChatUtils.format("%s<green>Nivel de acceso al server cambio al nivel <gold>%s <green>(%s%s<green>)".formatted(magicWhitelist.prefix, newLevel, role.color, role.name)));
        Bukkit.broadcast(ChatUtils.format("%s<gray>Server access level changed to <gold>%s <gray>(%s%s<gray>)".formatted(magicWhitelist.prefix, newLevel, role.color, role.name)), "magicwhitelist.admin");
    }

    private void level(CommandSender sender) {
        WhitelistRole role;
        try {
            role = Roles.getRole(whitelistManager.getAccessLevel());
        } catch (Exception e) {
            sender.sendMessage(ChatUtils.format("%s<red>Nivel de rol invalido, porfavor ejecuta /mwl setLevel [level]".formatted(magicWhitelist.prefix)));
            return;
        }

        sender.sendMessage(ChatUtils.format("%s<green>El nivel de acceso es de: <gold>%d <green>(%s%s<green>)".formatted(magicWhitelist.prefix, whitelistManager.getAccessLevel(), role.color, role.name)));
    }

    private void parent(@NotNull CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatUtils.format("<white>Usage /magicwhitelist parent [username]"));
            return;
        }

        var player = args[1];

        if (!whitelistManager.isWhitelisted(player)) {
            sender.sendMessage(ChatUtils.format("%s<red>Jugador %s no encontrado en la lista blanca".formatted(magicWhitelist.prefix, player)));
            return;
        }

        var role = whitelistManager.getPlayer(player).role;
        sender.sendMessage(ChatUtils.format("%s<green>El rol de %s es %s%s<green>".formatted(magicWhitelist.prefix, player, role.color, role.name)));
    }

    private void setParent(@NotNull CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatUtils.format("<white>Usage /magicwhitelist setParent [username] [role]"));
            return;
        }

        WhitelistedPlayer player;
        WhitelistRole role;

        try {
            player = whitelistManager.getPlayer(args[1]);
        } catch (Exception e) {
            sender.sendMessage(ChatUtils.format("%s<red>Jugador %s no encontrado en la lista blanca".formatted(magicWhitelist.prefix, args[1])));
            return;
        }


        try {
            role = Roles.getRole(args[2]);
        } catch (Exception e) {
            var list = Roles.list();
            sender.sendMessage(ChatUtils.format("%s<red>Rol invalido, roles validos:\n<gold>%s".formatted(magicWhitelist.prefix, String.join(", ", list))));
            return;
        }

        var oldRole = player.role;
        player.role = role;

        sender.sendMessage(ChatUtils.format("%s<green>El rol de %s cambio, de %s%s <green>a %s%s".formatted(magicWhitelist.prefix,
                player.username,
                oldRole.color,
                oldRole.name,
                role.color,
                role.name
        )));
        Bukkit.broadcast(ChatUtils.format("%s<gray>Player <gold>%s <gray>role changed from %s%s <gray>to %s%s".formatted(magicWhitelist.prefix,
                player.username,
                oldRole.color,
                oldRole.name,
                role.color,
                role.name
        )), "magicwhitelist.admin");
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }
        var a = args[0];

        if (a.equalsIgnoreCase("list")) {
            list(sender);
            return true;
        }

        if (a.equalsIgnoreCase("add")) {
            add(sender, args);
            return true;
        }

        if (a.equalsIgnoreCase("remove")) {
            remove(sender, args);
            return true;
        }

        if (a.equalsIgnoreCase("setlevel")) {
            setLevel(sender, args);
            return true;
        }

        if (a.equalsIgnoreCase("level")) {
            level(sender);
            return true;
        }

        if (a.equalsIgnoreCase("parent")) {
            parent(sender, args);
            return true;
        }

        if (a.equalsIgnoreCase("setparent")) {
            setParent(sender, args);
            return true;
        }
        return false;
    }
}
