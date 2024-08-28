package org.kt.hw.repository.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Generate short UUID (13 characters)
 */
public class ShortUUID {
    public static String shortUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }
}
