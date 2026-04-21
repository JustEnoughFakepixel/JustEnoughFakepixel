package com.jef.justenoughfakepixel.features.storage.utils;

public class StorageUtils {

    public static int getSlotCountFromRenderHeight(int renderH) {
        switch (renderH) {
            case 70:
                return 9;
            case 100:
                return 18;
            case 140:
                return 27;
            case 170:
                return 36;
            case 230:
                return 54;
            case 200:
            default:
                return 45;
        }
    }

    public static int calculateRenderHeight(int rows) {
        switch (rows) {
            case 1:
                return 70;
            case 2:
                return 100;
            case 3:
                return 140;
            case 4:
                return 170;
            case 6:
                return 230;
            case 5:
            default:
                return 200;
        }
    }

    public static int getBackpackRenderHeight(String sizeType) {
        if (sizeType == null || sizeType.isEmpty()) return 200;

        String size = sizeType.toLowerCase();
        switch (size) {
            case "small":
                return 70;
            case "medium":
                return 100;
            case "large":
                return 140;
            case "greater":
                return 170;
            case "jumbo":
            default:
                return 200;
        }
    }
}