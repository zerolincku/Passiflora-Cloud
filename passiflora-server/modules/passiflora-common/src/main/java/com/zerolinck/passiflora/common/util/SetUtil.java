package com.zerolinck.passiflora.common.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author linck
 * @since 2024-06-29
 */
public class SetUtil {

    /**
     * 返回 set2 相对于 set1 多出的值
     */
    public static Set<String> differenceSet2FromSet1(Set<String> set1, Set<String> set2) {
        Set<String> difference = new HashSet<>(set2);
        difference.removeAll(set1);
        return difference;
    }

}
