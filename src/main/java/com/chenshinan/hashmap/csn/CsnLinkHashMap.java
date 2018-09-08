package com.chenshinan.hashmap.csn;

/**
 * @author shinan.chen
 * @date 2018/9/6
 */
public class CsnLinkHashMap {
    static class Entry<K,V> extends CsnHashMap.Node<K,V> {
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, CsnHashMap.Node<K,V> next) {
            super(hash, key, value, next);
        }
    }
}
