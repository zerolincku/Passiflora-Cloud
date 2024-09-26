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
package com.zerolinck.passiflora.iam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamOrgMapper;
import com.zerolinck.passiflora.model.iam.entity.IamOrg;
import com.zerolinck.passiflora.model.iam.vo.IamOrgVo;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linck
 * @since 2024-04-09
 */
@Slf4j
@Service
public class IamOrgService extends ServiceImpl<IamOrgMapper, IamOrg> {

    private static final String LOCK_KEY = "passiflora:lock:iamOrg:";

    @Nonnull
    public Page<IamOrg> page(@Nullable QueryCondition<IamOrg> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return baseMapper.page(
                condition.page(), condition.searchWrapper(IamOrg.class), condition.sortWrapper(IamOrg.class));
    }

    public void add(@Nonnull IamOrg iamOrg) {
        LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<IamOrg>()
                        .lock(IamOrg::getOrgName, iamOrg.getOrgName())
                        .lock(IamOrg::getOrgCode, iamOrg.getOrgCode()),
                true,
                () -> {
                    // 同一父机构下，机构名称不能重复
                    Long count = baseMapper.selectCount(new LambdaQueryWrapper<IamOrg>()
                            .eq(IamOrg::getOrgName, iamOrg.getOrgName())
                            .eq(IamOrg::getParentOrgId, iamOrg.getParentOrgId()));
                    if (count > 0) {
                        throw new BizException("机构名称重复，请重新填写");
                    }
                    generateIadPathAndLevel(iamOrg);
                    baseMapper.insert(iamOrg);
                });
    }

    public boolean update(@Nonnull IamOrg iamOrg) {
        return LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<IamOrg>()
                        .lock(IamOrg::getOrgName, iamOrg.getOrgName())
                        .lock(IamOrg::getOrgCode, iamOrg.getOrgCode()),
                true,
                () -> {
                    OnlyFieldCheck.checkUpdate(baseMapper, iamOrg);

                    // 同一父机构下，机构名称不能重复
                    IamOrg dbIamOrg = baseMapper.selectById(iamOrg.getOrgId());
                    if (iamOrg.getOrgName() != null && !dbIamOrg.getOrgName().equals(iamOrg.getOrgName())) {
                        Long count = baseMapper.selectCount(new LambdaQueryWrapper<IamOrg>()
                                .eq(IamOrg::getOrgName, iamOrg.getOrgName())
                                .eq(IamOrg::getParentOrgId, iamOrg.getParentOrgId())
                                .ne(IamOrg::getOrgId, iamOrg.getOrgId()));
                        if (count > 0) {
                            throw new BizException("机构名称重复，请重新填写");
                        }
                    }
                    generateIadPathAndLevel(iamOrg);
                    int changeRowCount = baseMapper.updateById(iamOrg);
                    // 子机构数据变更
                    List<IamOrgVo> iamOrgList = listByParentId(iamOrg.getOrgId());
                    iamOrgList.forEach(org -> {
                        generateIadPathAndLevel(org);
                        baseMapper.updateById(org);
                    });
                    return changeRowCount > 0;
                });
    }

    /** 此方法会级联删除下级机构 */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@Nonnull Collection<String> orgIds) {
        int rowCount = 0;
        for (String orgId : orgIds) {
            rowCount += baseMapper.deleteById(orgId, CurrentUtil.getCurrentUserId());
        }
        return rowCount;
    }

    @Nonnull
    public Optional<IamOrg> detail(@Nonnull String orgId) {
        return Optional.ofNullable(baseMapper.selectById(orgId));
    }

    @Nonnull
    public Map<String, String> orgId2NameMap(@Nonnull Collection<String> orgIds) {
        if (CollectionUtils.isEmpty(orgIds)) {
            return new HashMap<>();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<IamOrg>().in(IamOrg::getOrgId, orgIds)).stream()
                .collect(Collectors.toMap(IamOrg::getOrgId, IamOrg::getOrgName));
    }

    @Nullable @SuppressWarnings("unused")
    public IamOrg selectByOrgCode(@Nonnull String orgCode) {
        return baseMapper.selectByOrgCode(orgCode);
    }

    @Nonnull
    public List<IamOrgVo> listByParentId(@Nonnull String orgParentId) {
        return baseMapper.listByParentId(orgParentId);
    }

    @Nullable public List<IamOrgVo> orgTree() {
        List<IamOrgVo> iamOrgVos = baseMapper.listByParentId("0");
        iamOrgVos.forEach(this::recursionTree);
        return iamOrgVos;
    }

    private void recursionTree(@Nonnull IamOrgVo iamOrgVo) {
        iamOrgVo.setChildren(listByParentId(iamOrgVo.getOrgId()));
        iamOrgVo.getChildren().forEach(this::recursionTree);
    }

    private void generateIadPathAndLevel(@Nonnull IamOrg iamOrg) {
        StringBuilder codeBuffer = new StringBuilder();
        String orgParentId = iamOrg.getParentOrgId();
        int level = 0;
        if (!"0".equals(orgParentId)) {
            IamOrg parentOrg =
                    detail(orgParentId).orElseThrow(() -> new BizException("机构数据错误，%s无上级机构", iamOrg.getOrgName()));
            codeBuffer.append(parentOrg.getOrgIdPath()).append("/");
            level = parentOrg.getOrgLevel() + 1;
        }
        codeBuffer.append(iamOrg.getOrgId());
        iamOrg.setOrgLevel(level);
        iamOrg.setOrgIdPath(codeBuffer.toString());
    }
}
