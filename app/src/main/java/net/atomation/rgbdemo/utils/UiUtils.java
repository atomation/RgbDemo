package net.atomation.rgbdemo.utils;

/**
 * Utility class containing UI related Utility methods
 * Created by eyal on 11/01/2017.
 */

public final class UiUtils {
    private static final int[] RSSI_RANGES = new int[] {-90, -75, -55, 0};

    public static int getRssiRange(int rssi) {
        int index = 0;

        for (int i = 0; i < RSSI_RANGES.length; i++) {
            if (rssi <= RSSI_RANGES[i]) {
                index = i;
                break;
            }
        }

        return index;
    }
}
