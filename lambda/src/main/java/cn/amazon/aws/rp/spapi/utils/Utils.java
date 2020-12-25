package cn.amazon.aws.rp.spapi.utils;

import java.util.Collection;

public class Utils {
    public static String getEnv(String name){
        return System.getenv(name);
    }

    public static String getEnv(String name, String value) {
        String val = System.getenv(name);
        return val == null ? value : val;
    }

    public static <T> boolean isNullOrEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }
}
