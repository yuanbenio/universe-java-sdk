package com.yuanben.util;

public class StringUtils {
    public static final String EMPTY = "";
    public static boolean isBlank (String s) {
        return null == s || "".equals(s.trim());
    }
    public static boolean isNotBlank (String s) {
        return null != s && !"".equals(s.trim());
    }
    public static boolean isEmpty (String s) {
        return null == s;
    }
    public static boolean isNotEmpty (String s) {
        return null != s;
    }


}
