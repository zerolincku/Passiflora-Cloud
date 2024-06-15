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
package com.zerolinck.passiflora.system.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.model.system.entity.SysOrg;
import com.zerolinck.passiflora.model.system.vo.SysOrgVo;
import com.zerolinck.passiflora.system.mapper.SysOrgMapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linck
 * @since 2024-04-09
 */
@Slf4j
@Service
public class SysOrgService extends ServiceImpl<SysOrgMapper, SysOrg> {

    private static final String LOCK_KEY = "passiflora:lock:sysOrg:";

    public Page<SysOrg> page(QueryCondition<SysOrg> condition) {
        if (condition == null) {
            condition = new QueryCondition<>();
        }
        return baseMapper.page(
            condition.page(),
            condition.searchWrapper(SysOrg.class),
            condition.sortWrapper(SysOrg.class)
        );
    }

    public void add(SysOrg sysOrg) {
        LockUtil.lockAndTransactionalLogic(
            LOCK_KEY,
            new LockWrapper<SysOrg>()
                .lock(SysOrg::getOrgName, sysOrg.getOrgName())
                .lock(SysOrg::getOrgCode, sysOrg.getOrgCode()),
            () -> {
                // 同一父机构下，机构名称不能重复
                Long count = baseMapper.selectCount(
                    new LambdaQueryWrapper<SysOrg>()
                        .eq(SysOrg::getOrgName, sysOrg.getOrgName())
                        .eq(SysOrg::getParentOrgId, sysOrg.getParentOrgId())
                );
                if (count > 0) {
                    throw new BizException("机构名称重复，请重新填写");
                }
                generateIadPathAndLevel(sysOrg);
                baseMapper.insert(sysOrg);
                return null;
            }
        );
    }

    public boolean update(SysOrg sysOrg) {
        return (boolean) LockUtil.lockAndTransactionalLogic(
            LOCK_KEY,
            new LockWrapper<SysOrg>()
                .lock(SysOrg::getOrgName, sysOrg.getOrgName())
                .lock(SysOrg::getOrgCode, sysOrg.getOrgCode()),
            () -> {
                OnlyFieldCheck.checkUpdate(baseMapper, sysOrg);

                // 同一父机构下，机构名称不能重复
                SysOrg dbSysOrg = baseMapper.selectById(sysOrg.getOrgId());
                if (
                    sysOrg.getOrgName() != null &&
                    !dbSysOrg.getOrgName().equals(sysOrg.getOrgName())
                ) {
                    Long count = baseMapper.selectCount(
                        new LambdaQueryWrapper<SysOrg>()
                            .eq(SysOrg::getOrgName, sysOrg.getOrgName())
                            .eq(SysOrg::getParentOrgId, sysOrg.getParentOrgId())
                            .ne(SysOrg::getOrgId, sysOrg.getOrgId())
                    );
                    if (count > 0) {
                        throw new BizException("机构名称重复，请重新填写");
                    }
                }
                generateIadPathAndLevel(sysOrg);
                int changeRowCount = baseMapper.updateById(sysOrg);
                // 子机构数据变更
                List<SysOrgVo> sysOrgList = listByParentId(sysOrg.getOrgId());
                sysOrgList.forEach(org -> {
                    generateIadPathAndLevel(org);
                    baseMapper.updateById(org);
                });
                return changeRowCount > 0;
            }
        );
    }

    /** 此方法会级联删除下级机构 */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(Collection<String> orgIds) {
        int rowCount = 0;
        for (String orgId : orgIds) {
            rowCount +=
            baseMapper.deleteById(orgId, CurrentUtil.getCurrentUserId());
        }
        return rowCount;
    }

    public SysOrg detail(String orgId) {
        SysOrg sysOrg = baseMapper.selectById(orgId);
        if (sysOrg == null) {
            throw new BizException("无对应机构数据，请刷新后重试");
        }
        return sysOrg;
    }

    public Map<String, String> orgId2NameMap(List<String> orgIds) {
        if (CollectionUtil.isEmpty(orgIds)) {
            return new HashMap<>();
        }
        return baseMapper
            .selectList(
                new LambdaQueryWrapper<SysOrg>().in(SysOrg::getOrgId, orgIds)
            )
            .stream()
            .collect(Collectors.toMap(SysOrg::getOrgId, SysOrg::getOrgName));
    }

    public SysOrg selectByOrgCode(String orgCode) {
        return baseMapper.selectByOrgCode(orgCode);
    }

    public List<SysOrgVo> listByParentId(String orgParentId) {
        return baseMapper.listByParentId(orgParentId);
    }

    public List<SysOrgVo> orgTree() {
        List<SysOrgVo> sysOrgVos = baseMapper.listByParentId("0");
        sysOrgVos.forEach(this::recursionTree);
        return sysOrgVos;
    }

    private void recursionTree(SysOrgVo sysOrgVo) {
        sysOrgVo.setChildren(listByParentId(sysOrgVo.getOrgId()));
        sysOrgVo.getChildren().forEach(this::recursionTree);
    }

    private void generateIadPathAndLevel(SysOrg sysOrg) {
        StringBuilder codeBuffer = new StringBuilder();
        String orgParentId = sysOrg.getParentOrgId();
        int level = 0;
        if (!"0".equals(orgParentId)) {
            SysOrg parentOrg = detail(orgParentId);
            codeBuffer.append(parentOrg.getOrgIdPath()).append("/");
            level = parentOrg.getOrgLevel() + 1;
        }
        codeBuffer.append(sysOrg.getOrgId());
        sysOrg.setOrgLevel(level);
        sysOrg.setOrgIdPath(codeBuffer.toString());
    }
}
