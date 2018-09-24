package com.chenshinan.hashmap.csn;

import java.util.LinkedHashMap;

/**
 * @author shinan.chen
 * @date 2018/9/24
 */
public class LRUCache extends LinkedHashMap {
    public LRUCache(int maxSize) {
        super(maxSize, 0.75F, true);
        maxElements = maxSize;
    }

    protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
        return size() > maxElements;
    }

    private static final long serialVersionUID = 1L;
    protected int maxElements;
}
