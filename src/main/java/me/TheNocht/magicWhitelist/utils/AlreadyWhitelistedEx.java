package me.TheNocht.magicWhitelist.utils;

public class AlreadyWhitelistedEx extends RuntimeException {
    public AlreadyWhitelistedEx(String message) {
        super(message);
    }
}
