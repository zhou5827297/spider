package com.spider.filter.impl;


import com.spider.filter.FilterManage;

import java.util.BitSet;
import java.util.Collection;

/**
 * bloom过滤管理器
 */
public class BloomFilterManage implements FilterManage {

    //DEFAULT_SIZE为2的29次方，即此处的左移28位
    private static final int DEFAULT_SIZE = 2 << 28;
    /*
     * 不同哈希函数的种子，一般取质数
     * seeds数组共有8个值，则代表采用8种不同的哈希函数
     */
    private int[] seeds = new int[]{3, 5, 7, 11, 13, 31, 37, 61};
    /*
     * 初始化一个给定大小的位集
     * BitSet实际是由“二进制位”构成的一个Vector。
     * 假如希望高效率地保存大量“开－关”信息，就应使用BitSet.
     */
    private BitSet bitSets = new BitSet(DEFAULT_SIZE);
    //构建hash函数对象
    private SimpleHash[] hashFuns = new SimpleHash[seeds.length];

    private static final FilterManage INSTANCE = new BloomFilterManage();

    public BloomFilterManage() {
        /**
         *  给出所有的hash值，共计seeds.length个hash值。共8位。
         *  通过调用SimpleHash.hash(),可以得到根据8种hash函数计算得出hash值。
         *  传入DEFAULT_SIZE(最终字符串的长度），seeds[i](一个指定的质数)即可得到需要的那个hash值的位置。
         */
        for (int i = 0; i < seeds.length; i++) {
            hashFuns[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
        }
    }

    /**
     * 将给定的字符串标记到bitSets中，即设置字符串的8个函数值的位置为1
     */
    @Override
    public void add(String value) {
        synchronized (bitSets) {
            addNoSync(value);
        }
    }

    /**
     * 不加锁添加
     */
    public void addNoSync(String value) {
        if (value == null) {
            return;
        }
        for (SimpleHash hashFun : hashFuns) {
            bitSets.set(hashFun.hash(value), true);
        }
    }

    /**
     * 清空缓存
     */
    @Override
    public void clear() {
        synchronized (bitSets) {
            bitSets.clear();
        }
    }

    @Override
    public void refresh(Collection<String> urls) {
        synchronized (bitSets) {
            // 可能会存在环境不一致，导致新环境数据丢失部分，所以先清空过滤器，直接使用最新数据
            bitSets.clear();
            if (urls != null && !urls.isEmpty()) {
                for (String url : urls) {
                    addNoSync(url);
                }
            }
        }
    }

    /**
     * 判断给定的字符串是否已经存在在bloofilter中，如果存在返回true，不存在返回false
     */
    @Override
    public boolean isExists(String value) {
        //判断传入的值是否为null
        if (null == value) {
            return false;
        }

        for (SimpleHash hashFun : hashFuns) {
            if (!bitSets.get(hashFun.hash(value))) {
                //如果判断8个hash函数值中有一个位置不存在即可判断为不存在Bloofilter中
                return false;
            }
        }

        return true;
    }


    /**
     * 简单hash一下
     */
    private static class SimpleHash {
        /*
         * cap为DEFAULT_SIZE，即用于结果的最大字符串的值
         * seed为计算hash值的一个key值，具体对应上文中的seeds数组
         */
        private int cap;
        private int seed;

        public SimpleHash(int cap, int seed) {
            this.cap = cap;
            this.seed = seed;
        }

        /**
         * 计算hash的函数，用户可以选择其他更好的hash函数
         */
        public int hash(String value) {
            int result = 0;
            int length = value.length();
            for (int i = 0; i < length; i++) {
                result = seed * result + value.charAt(i);
            }
            return (cap - 1) & result;
        }
    }


    /**
     * 单例获取
     */
    public static FilterManage getInstance() {
        return INSTANCE;
    }

}
