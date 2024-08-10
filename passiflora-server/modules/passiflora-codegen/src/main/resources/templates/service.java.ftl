package com.zerolinck.passiflora.${moduleName}.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
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
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.concurrent.TimeUnit;


/**
 * ${table.description} Service
 *
 * @author linck
 * @since ${date}
 */
@Slf4j
@Service
public class ${serviceClass} extends ServiceImpl<${mapperClass}, ${entityClass}> {
    
    private static final String LOCK_KEY = "passiflora:lock:${entityName}:";

    @Nonnull
    public Page<${entityClass}> page(@Nullable QueryCondition<${entityClass}> condition) {
        if (condition == null) {
            condition = new QueryCondition<>();
        }
        return baseMapper.page(condition.page(), condition.searchWrapper(${entityClass}.class), condition.sortWrapper(${entityClass}.class));
    }

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

    public boolean update(@Nonnull ${entityClass} ${entityName}) {
        return (boolean) LockUtil.lock(LOCK_KEY,
                new LockWrapper<${entityClass}>(), true,
                () -> {
                    OnlyFieldCheck.checkUpdate(baseMapper, ${entityName});
                    int changeRowCount = baseMapper.updateById(${entityName});
                    return changeRowCount > 0;
                }
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> ${table.pkFieldName}s) {
        if (CollectionUtil.isEmpty(${table.pkFieldName}s) {
            return 0;
        }
        return baseMapper.deleteByIds(${table.pkFieldName}s, CurrentUtil.getCurrentUserId());
    }

    @Nonnull
    public ${entityClass} detail(@Nonnull String ${table.pkFieldName}) {
        ${entityClass} ${entityName} = baseMapper.selectById(${table.pkFieldName});
        if (${entityName} == null) {
            throw new BizException("无对应${table.description}数据，请刷新后重试");
        }
        return ${entityName};
    }
            
}
