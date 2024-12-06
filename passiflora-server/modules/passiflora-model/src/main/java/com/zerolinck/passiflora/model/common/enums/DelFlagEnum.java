package com.zerolinck.passiflora.model.common.enums;

import com.zerolinck.passiflora.model.common.LabelValueInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 林常坤 on 2024/11/22
 */
@Getter
@AllArgsConstructor
public enum DelFlagEnum implements LabelValueInterface {
    NOT_DELETE("未删除", 0),
    DELETED("已删除", 1);

    private final String label;

    private final Integer value;
}
