package com.yuanben.util;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>map工具类</p>
 */
public class MapUtil {
    /**
     * 使用 Map按key进行排序
     * @param map
     * @return
     */
    public static TreeMap<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        TreeMap<String, Object> sortMap = new TreeMap<String, Object>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }
}


/**
 * <p>map比较器</p>
 */
class MapKeyComparator implements Comparator<String> {

    @Override
    public int compare(String str1, String str2) {

        return str1.compareTo(str2);
    }
}
