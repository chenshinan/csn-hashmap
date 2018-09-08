package com.chenshinan.hashmap.csn;

import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/9/6
 */
public class hashmapMain {
    public static void main(String[] args){
        Map<String,String> myMap = new CsnHashMap<>();
        myMap.put("date","2018");
        System.out.println(myMap.get("date"));

        /**
         * 测试(h = key.hashCode()) ^ (h >>> 16)
         */
        String[] strings = new String[]{"x","12","as","sad","po","90wwdw"};
        System.out.println("hashCode:");
        for(String str:strings){
            System.out.println(toFullBinaryString(str.hashCode()));
        }
        System.out.println("无符号右移16位：");
        for(String str:strings){
            System.out.println(toFullBinaryString(str.hashCode() >>> 16));
        }
        System.out.println("高16位与低16位异或：");
        for(String str:strings){
            int h;
            System.out.println(toFullBinaryString((h = str.hashCode()) ^ (h >>> 16)));
        }
        /**
         * 通过(n - 1) & hash计算出在数组中的存储位置
         */
        System.out.println("通过(n - 1) & hash计算出在数组中的存储位置:");
        for(String str:strings){
            System.out.println((strings.length-1)&hash(str));
        }
    }
    private static String toFullBinaryString(int x) {
        int[] buffer = new int[Integer.SIZE];
        for (int i = (Integer.SIZE - 1); i >= 0; i--) {
            buffer[i] = x >> i & 1;
        }
        String s = "";
        for (int j = (Integer.SIZE - 1); j >= 0; j--) {
            s = s + buffer[j];
        }
        return s;
    }
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
}
