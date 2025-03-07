package me.TheNocht.magicWhitelist.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ChatUtils {

    public static Component format(String message, Object... format) {
        return MiniMessage.miniMessage().deserialize(String.format(message, format));
    }
}