/* 
 * Copyright (C) 2024 Linck. <zerolinck@foxmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zerolinck.passiflora.common.util.lock.suppert;

import java.util.*;
import java.util.function.Function;

/**
 * Collection工具类
 *
 * @author Caratacus
 * @since 2016-09-19
 */
public class CollectionUtils {

    private static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    private static boolean isJdk8;

    static {
        // Java 8
        // Java 9+: 9,11,17
        try {
            isJdk8 = System.getProperty("java.version").startsWith("1.8.");
        } catch (Exception ignore) {
            isJdk8 = true;
        }
    }

    /**
     * 校验集合是否为空
     *
     * @param coll 入参
     * @return boolean
     */
    public static boolean isEmpty(Collection<?> coll) {
        return (coll == null || coll.isEmpty());
    }

    /**
     * 校验集合是否不为空
     *
     * @param coll 入参
     * @return boolean
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * 判断Map是否为空
     *
     * @param map 入参
     * @return boolean
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * 判断Map是否不为空
     *
     * @param map 入参
     * @return boolean
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 创建默认HashMap
     *
     * @param <K> K
     * @param <V> V
     * @return HashMap
     * @see com.google.common.collect.Maps#newHashMap()
     * @since 3.4.0
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    /**
     * 根据预期大小创建HashMap.
     *
     * @param expectedSize 预期大小
     * @param <K> K
     * @param <V> V
     * @return HashMap
     * @see com.google.common.collect.Maps#newHashMapWithExpectedSize
     * @since 3.4.0
     */
    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(
        int expectedSize
    ) {
        return new HashMap<>(capacity(expectedSize));
    }

    /**
     * 用来过渡下Jdk1.8下ConcurrentHashMap的性能bug https://bugs.openjdk.java.net/browse/JDK-8161372
     *
     * <p>A temporary workaround for Java 8 ConcurrentHashMap#computeIfAbsent specific performance
     * issue: JDK-8161372.</br>
     *
     * @see <a
     *     href="https://bugs.openjdk.java.net/browse/JDK-8161372">https://bugs.openjdk.java.net/browse/JDK-8161372</a>
     * @param concurrentHashMap ConcurrentHashMap 没限制类型了，非ConcurrentHashMap就别调用这方法了
     * @param key key
     * @param mappingFunction function
     * @param <K> k
     * @param <V> v
     * @return V
     * @since 3.4.0
     */
    public static <K, V> V computeIfAbsent(
        Map<K, V> concurrentHashMap,
        K key,
        Function<? super K, ? extends V> mappingFunction
    ) {
        Objects.requireNonNull(mappingFunction);
        if (isJdk8) {
            V v = concurrentHashMap.get(key);
            if (null == v) {
                // issue#11986 lock bug
                // v = map.computeIfAbsent(key, func);

                // this bug fix methods maybe cause `func.apply` multiple calls.
                v = mappingFunction.apply(key);
                if (null == v) {
                    return null;
                }
                final V res = concurrentHashMap.putIfAbsent(key, v);
                if (null != res) {
                    // if pre value present, means other thread put value already, and putIfAbsent
                    // not effect
                    // return exist value
                    return res;
                }
                // if pre value is null, means putIfAbsent effected, return current value
            }
            return v;
        } else {
            return concurrentHashMap.computeIfAbsent(key, mappingFunction);
        }
    }

    /**
     * Returns a capacity that is sufficient to keep the map from being resized as long as it grows no
     * larger than expectedSize and the load factor is >= its default (0.75).
     *
     * @see com.google.common.collect.Maps#capacity(int)
     * @since 3.4.0
     */
    private static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            if (expectedSize < 0) {
                throw new IllegalArgumentException(
                    "expectedSize cannot be negative but was: " + expectedSize
                );
            }
            return expectedSize + 1;
        }
        if (expectedSize < MAX_POWER_OF_TWO) {
            // This is the calculation used in JDK8 to resize when a putAll
            // happens; it seems to be the most conservative calculation we
            // can make.  0.75 is the default load factor.
            return (int) ((float) expectedSize / 0.75F + 1.0F);
        }
        return Integer.MAX_VALUE; // any large value
    }

    // 提供处理Map多key取值工具方法

    /**
     * 批量取出Map中的值
     *
     * @param map map
     * @param keys 键的集合
     * @param <K> key的泛型
     * @param <V> value的泛型
     * @return value的泛型的集合
     */
    public static <K, V> List<V> getCollection(
        Map<K, V> map,
        Iterable<K> keys
    ) {
        List<V> result = new ArrayList<>();
        if (map != null && !map.isEmpty() && keys != null) {
            keys.forEach(key ->
                Optional.ofNullable(map.get(key)).ifPresent(result::add)
            );
        }
        return result;
    }

    /**
     * 批量取出Map中的值
     *
     * @param map map
     * @param keys 键的集合
     * @param comparator 排序器
     * @param <K> key的泛型
     * @param <V> value的泛型
     * @return value的泛型的集合
     */
    public static <K, V> List<V> getCollection(
        Map<K, V> map,
        Iterable<K> keys,
        Comparator<V> comparator
    ) {
        Objects.requireNonNull(comparator);
        List<V> result = getCollection(map, keys);
        Collections.sort(result, comparator);
        return result;
    }
}
