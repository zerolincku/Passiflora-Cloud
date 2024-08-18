package com.zerolinck.passiflora.${moduleName}.service;

import org.apache.commons.collections4.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.${moduleName}.entity.${entityClass};
import com.zerolinck.passiflora.${moduleName}.mapper.${mapperClass};
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.exception.BizException;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * ${table.description} Service
 *
 * @author ${author}
 * @since ${date}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ${serviceClass} extends ServiceImpl<${mapperClass}, ${entityClass}> {
    
    private static final String LOCK_KEY = "passiflora:lock:${entityName}:";

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since ${date}
     */
    @Nonnull
    public Page<${entityClass}> page(@Nullable QueryCondition<${entityClass}> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(condition.page(), condition.searchWrapper(${entityClass}.class), condition.sortWrapper(${entityClass}.class));
    }

    /**
     * 新增${table.description}
     *
     * @param ${entityName} ${table.description}
     * @since ${date}
     */
    public void add(@Nonnull ${entityClass} ${entityName}) {
        LockUtil.lock(LOCK_KEY,
                new LockWrapper<${entityClass}>(), true,
                () -> {
                    OnlyFieldCheck.checkInsert(baseMapper, ${entityName});
                    baseMapper.insert(${entityName});
                    return null;
                }
        );
    }

    /**
     * 更新${table.description}
     *
     * @param ${entityName} ${table.description}
     * @since ${date}
     */
    public boolean update(@Nonnull ${entityClass} ${entityName}) {
        return LockUtil.lock(LOCK_KEY,
                new LockWrapper<${entityClass}>(), true,
                () -> {
                    OnlyFieldCheck.checkUpdate(baseMapper, ${entityName});
                    int changeRowCount = baseMapper.updateById(${entityName});
                    return changeRowCount > 0;
                }
        );
    }

    /**
     * 删除${table.description}
     *
     * @param ${table.pkFieldName}s ${table.description}ID集合
     * @since ${date}
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> ${table.pkFieldName}s) {
        if (CollectionUtils.isEmpty(${table.pkFieldName}s)) {
            return 0;
        }
        return baseMapper.deleteByIds(${table.pkFieldName}s, CurrentUtil.getCurrentUserId());
    }

    /**
     * ${table.description}详情
     *
     * @param ${table.pkFieldName} ${table.description}ID
     * @since ${date}
     */
    @Nonnull
    public Optional<${entityClass}> detail(@Nonnull String ${table.pkFieldName}) {
        return Optional.ofNullable(baseMapper.selectById(${table.pkFieldName}));
    }
            
}
