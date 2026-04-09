package com.jef.justenoughfakepixel.features.scoreboard;

public class BankParser {

    private static String cachedBank = null;
    private static String cachedPurse = null;

    public static String getBank() {
        return cachedBank;
    }

    public static void setBank(String bank) {
        cachedBank = bank;
    }

    public static String getPurse() {
        return cachedPurse;
    }

    public static void setPurse(String purse) {
        cachedPurse = purse;
    }

    public static void clear() {
        cachedBank = null;
        cachedPurse = null;
    }
}