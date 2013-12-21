package fr.areku.minecraft.sec;

import java.util.HashMap;
import java.util.Map;

public class Volatile {
    private static Map<String, Object> d = new HashMap<String, Object>();

    public static Object get(String key) {
        if (!d.containsKey(key)) return null;
        return d.get(key);
    }

    public static void set(String key, Object o) {
        d.put(key, o);
    }

    public static void delete(String key) {
        if (d.containsKey(key))
            d.remove(key);
    }

    public static boolean contains(String key) {
        return d.containsKey(key);
    }
}
