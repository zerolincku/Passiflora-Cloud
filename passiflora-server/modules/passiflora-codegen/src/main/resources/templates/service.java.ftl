package com.zerolinck.passiflora.${moduleName}.service;

import org.apache.commons.collections4.CollectionUtils;


import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.${moduleName}.entity.${entityClass};
import com.zerolinck.passiflora.${moduleName}.mapper.${mapperClass};
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * ${table.description} Service
 *
 * @author ${author} on ${date}
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
    @NotNull
    public Page<${entityClass}> page(@Nullable QueryCondition<${entityClass}> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return mapper.paginate(condition.getPageNumber(), condition.getPageSize(), condition.searchWrapper(${entityClass}.class));
    }

    /**
     * 新增${table.description}
     *
     * @param ${entityName} ${table.description}
     * @since ${date}
     */
    public void add(@NotNull ${entityClass} ${entityName}) {
        LockUtil.lock(LOCK_KEY,
                new LockWrapper<>(), true,
                () -> {
                    OnlyFieldCheck.checkInsert(mapper, ${entityName});
                    mapper.insert(${entityName});
                }
        );
    }

    /**
     * 更新${table.description}
     *
     * @param ${entityName} ${table.description}
     * @since ${date}
     */
    public boolean update(@NotNull ${entityClass} ${entityName}) {
        return LockUtil.lock(LOCK_KEY,
                new LockWrapper<>(), true,
                () -> {
                    OnlyFieldCheck.checkUpdate(mapper, ${entityName});
                    int changeRowCount = mapper.update(${entityName});
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
        return mapper.deleteBatchByIds(${table.pkFieldName}s, CurrentUtil.getCurrentUserId());
    }

    /**
     * ${table.description}详情
     *
     * @param ${table.pkFieldName} ${table.description}ID
     * @since ${date}
     */
    @NotNull
    public Optional<${entityClass}> detail(@NotNull String ${table.pkFieldName}) {
        return Optional.ofNullable(mapper.selectOneById(${table.pkFieldName}));
    }
            
}
