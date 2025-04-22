/* 
 * Copyright (C) 2025 Linck. <zerolinck@foxmail.com>
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

import java.util.*;
import java.util.stream.Collectors;

import com.zerolinck.passiflora.common.api.Page;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.Condition;
import com.zerolinck.passiflora.common.util.lock.LockUtils;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamOrgMapper;
import com.zerolinck.passiflora.model.iam.entity.IamOrg;
import com.zerolinck.passiflora.model.iam.mapperstruct.IamOrgConvert;
import com.zerolinck.passiflora.model.iam.resp.IamOrgResp;
import com.zerolinck.passiflora.mybatis.util.UniqueFieldChecker;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-04-09 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IamOrgService {
    private final IamOrgMapper mapper;
    private static final String LOCK_KEY = "passiflora:lock:iamOrg:";

    /**
     * 分页查询组织
     *
     * @param condition 查询条件
     * @return 组织的分页结果
     * @since 2024-04-09
     */
    @NotNull public Page<IamOrg> page(@Nullable Condition<IamOrg> condition) {
        return mapper.page(condition);
    }

    /**
     * 新增组织
     *
     * @param iamOrg 组织实体
     * @since 2024-04-09
     */
    public void add(@NotNull IamOrg iamOrg) {
        LockUtils.lock(
                LOCK_KEY,
                new LockWrapper<IamOrg>()
                        .lock(IamOrg::getOrgName, iamOrg.getOrgName())
                        .lock(IamOrg::getOrgCode, iamOrg.getOrgCode()),
                true,
                () -> {
                    // 同一父机构下，机构名称不能重复
                    long count = mapper.countByName(iamOrg.getOrgName(), iamOrg.getParentOrgId(), null);
                    if (count > 0) {
                        throw new BizException("机构名称重复，请重新填写");
                    }
                    generateIadPathAndLevel(iamOrg);
                    mapper.insert(iamOrg);
                });
    }

    /**
     * 更新组织
     *
     * @param iamOrg 组织实体
     * @return 如果更新成功返回true，否则返回false
     * @since 2024-04-09
     */
    public boolean update(@NotNull IamOrg iamOrg) {
        return LockUtils.lock(
                LOCK_KEY,
                new LockWrapper<IamOrg>()
                        .lock(IamOrg::getOrgName, iamOrg.getOrgName())
                        .lock(IamOrg::getOrgCode, iamOrg.getOrgCode()),
                true,
                () -> {
                    UniqueFieldChecker.checkUpdate(mapper, iamOrg);

                    // 同一父机构下，机构名称不能重复
                    IamOrg dbIamOrg = mapper.selectOneById(iamOrg.getOrgId());
                    if (iamOrg.getOrgName() != null && !dbIamOrg.getOrgName().equals(iamOrg.getOrgName())) {
                        long count =
                                mapper.countByName(iamOrg.getOrgName(), iamOrg.getParentOrgId(), iamOrg.getOrgId());
                        if (count > 0) {
                            throw new BizException("机构名称重复，请重新填写");
                        }
                    }
                    generateIadPathAndLevel(iamOrg);
                    int changeRowCount = mapper.update(iamOrg);
                    // 子机构数据变更
                    List<IamOrgResp> iamOrgList = listByParentId(iamOrg.getOrgId());
                    iamOrgList.forEach(orgResp -> {
                        IamOrg org = IamOrgConvert.INSTANCE.respToEntity(orgResp);
                        generateIadPathAndLevel(org);
                        mapper.update(org);
                    });
                    return changeRowCount > 0;
                });
    }

    /**
     * 根据组织ID集合删除组织 此方法会级联删除下级机构
     *
     * @param orgIds 组织ID集合
     * @return 删除的组织数量
     * @since 2024-04-09
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@NotNull Collection<String> orgIds) {
        int rowCount = 0;
        for (String orgId : orgIds) {
            rowCount += mapper.deleteById(orgId);
        }
        return rowCount;
    }

    /**
     * 根据组织ID获取组织的详细信息
     *
     * @param orgId 组织ID
     * @return 包含组织的Optional对象
     * @since 2024-04-09
     */
    @NotNull public Optional<IamOrg> detail(@NotNull String orgId) {
        return Optional.ofNullable(mapper.selectOneById(orgId));
    }

    /**
     * 根据组织ID集合获取组织ID到名称的映射
     *
     * @param orgIds 组织ID集合
     * @return 组织ID到名称的映射
     * @since 2024-04-09
     */
    @NotNull public Map<String, String> orgId2NameMap(@NotNull Collection<String> orgIds) {
        if (CollectionUtils.isEmpty(orgIds)) {
            return new HashMap<>();
        }
        return mapper.listByOrgIds(orgIds).stream().collect(Collectors.toMap(IamOrg::getOrgId, IamOrg::getOrgName));
    }

    /**
     * 根据组织代码获取组织
     *
     * @param orgCode 组织代码
     * @return 组织实体
     * @since 2024-04-09
     */
    @Nullable @SuppressWarnings("unused")
    public IamOrg selectByOrgCode(@NotNull String orgCode) {
        return mapper.selectByOrgCode(orgCode);
    }

    /**
     * 根据父组织ID获取子组织列表
     *
     * @param orgParentId 父组织ID
     * @return 子组织列表
     * @since 2024-04-09
     */
    @NotNull public List<IamOrgResp> listByParentId(@NotNull String orgParentId) {
        return mapper.listByParentId(orgParentId);
    }

    /**
     * 获取组织树
     *
     * @return 组织树
     * @since 2024-04-09
     */
    @Nullable public List<IamOrgResp> orgTree() {
        List<IamOrgResp> iamOrgResps = mapper.listByParentId("0");
        iamOrgResps.forEach(this::recursionTree);
        return iamOrgResps;
    }

    /**
     * 递归生成组织树
     *
     * @param iamOrgResp 组织响应对象
     * @since 2024-04-09
     */
    private void recursionTree(@NotNull IamOrgResp iamOrgResp) {
        iamOrgResp.setChildren(listByParentId(iamOrgResp.getOrgId()));
        iamOrgResp.getChildren().forEach(this::recursionTree);
    }

    /**
     * 生成组织路径和层级
     *
     * @param iamOrg 组织实体
     * @since 2024-04-09
     */
    private void generateIadPathAndLevel(@NotNull IamOrg iamOrg) {
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
