package com.zerolinck.passiflora.system.service;

import org.apache.commons.collections4.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.system.entity.SysConfig;
import com.zerolinck.passiflora.system.mapper.SysConfigMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 系统配置 Service
 *
 * @author 林常坤
 * @since 2024-08-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigService extends ServiceImpl<SysConfigMapper, SysConfig> {
    
    private static final String LOCK_KEY = "passiflora:lock:sysConfig:";

    /**
     * 分页查询
     *
     * @param condition 搜索条件
     * @since 2024-08-24
     */
    @Nonnull
    public Page<SysConfig> page(@Nullable QueryCondition<SysConfig> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(condition.page(), condition.searchWrapper(SysConfig.class), condition.sortWrapper(SysConfig.class));
    }

    /**
     * 新增系统配置
     *
     * @param sysConfig 系统配置
     * @since 2024-08-24
     */
    public void add(@Nonnull SysConfig sysConfig) {
        LockUtil.lock(LOCK_KEY,
                new LockWrapper<SysConfig>(), true,
                () -> {
                    OnlyFieldCheck.checkInsert(baseMapper, sysConfig);
                    baseMapper.insert(sysConfig);
                    return null;
                }
        );
    }

    /**
     * 更新系统配置
     *
     * @param sysConfig 系统配置
     * @since 2024-08-24
     */
    public boolean update(@Nonnull SysConfig sysConfig) {
        return LockUtil.lock(LOCK_KEY,
                new LockWrapper<SysConfig>(), true,
                () -> {
                    OnlyFieldCheck.checkUpdate(baseMapper, sysConfig);
                    int changeRowCount = baseMapper.updateById(sysConfig);
                    return changeRowCount > 0;
                }
        );
    }

    /**
     * 删除系统配置
     *
     * @param configIds 系统配置ID集合
     * @since 2024-08-24
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nullable Collection<String> configIds) {
        if (CollectionUtils.isEmpty(configIds)) {
            return 0;
        }
        return baseMapper.deleteByIds(configIds, CurrentUtil.getCurrentUserId());
    }

    /**
     * 系统配置详情
     *
     * @param configId 系统配置ID
     * @since 2024-08-24
     */
    @Nonnull
    public Optional<SysConfig> detail(@Nonnull String configId) {
        return Optional.ofNullable(baseMapper.selectById(configId));
    }
            
}
