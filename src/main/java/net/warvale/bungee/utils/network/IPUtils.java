package net.warvale.bungee.utils.network;

public class IPUtils {

    public static long toLongIP(byte[] bytes) {
        long val = 0;
        for (int i = 0; i < bytes.length; i++) {
            val <<= 8;
            val |= bytes[i] & 0xff;
        }
        return val;
    }

}
