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

import java.util.*;
import java.util.stream.Collectors;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.OnlyFieldCheck;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.common.util.lock.LockUtil;
import com.zerolinck.passiflora.common.util.lock.LockWrapper;
import com.zerolinck.passiflora.iam.mapper.IamOrgMapper;
import com.zerolinck.passiflora.model.iam.entity.IamOrg;
import com.zerolinck.passiflora.model.iam.mapperstruct.IamOrgConvert;
import com.zerolinck.passiflora.model.iam.resp.IamOrgResp;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-04-09 */
@Slf4j
@Service
public class IamOrgService extends ServiceImpl<IamOrgMapper, IamOrg> {

    private static final String LOCK_KEY = "passiflora:lock:iamOrg:";

    @NotNull public Page<IamOrg> page(@Nullable QueryCondition<IamOrg> condition) {
        condition = Objects.requireNonNullElse(condition, new QueryCondition<>());
        return mapper.paginate(
                condition.getPageNumber(), condition.getPageSize(), condition.searchWrapper(IamOrg.class));
    }

    public void add(@NotNull IamOrg iamOrg) {
        LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<IamOrg>()
                        .lock(IamOrg::getOrgName, iamOrg.getOrgName())
                        .lock(IamOrg::getOrgCode, iamOrg.getOrgCode()),
                true,
                () -> {
                    // 同一父机构下，机构名称不能重复
                    long count = mapper.selectCountByQuery(new QueryWrapper()
                            .eq(IamOrg::getOrgName, iamOrg.getOrgName())
                            .eq(IamOrg::getParentOrgId, iamOrg.getParentOrgId()));
                    if (count > 0) {
                        throw new BizException("机构名称重复，请重新填写");
                    }
                    generateIadPathAndLevel(iamOrg);
                    mapper.insert(iamOrg);
                });
    }

    public boolean update(@NotNull IamOrg iamOrg) {
        return LockUtil.lock(
                LOCK_KEY,
                new LockWrapper<IamOrg>()
                        .lock(IamOrg::getOrgName, iamOrg.getOrgName())
                        .lock(IamOrg::getOrgCode, iamOrg.getOrgCode()),
                true,
                () -> {
                    OnlyFieldCheck.checkUpdate(mapper, iamOrg);

                    // 同一父机构下，机构名称不能重复
                    IamOrg dbIamOrg = mapper.selectOneById(iamOrg.getOrgId());
                    if (iamOrg.getOrgName() != null && !dbIamOrg.getOrgName().equals(iamOrg.getOrgName())) {
                        long count = mapper.selectCountByQuery(new QueryWrapper()
                                .eq(IamOrg::getOrgName, iamOrg.getOrgName())
                                .eq(IamOrg::getParentOrgId, iamOrg.getParentOrgId())
                                .ne(IamOrg::getOrgId, iamOrg.getOrgId()));
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

    /** 此方法会级联删除下级机构 */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(@NotNull Collection<String> orgIds) {
        int rowCount = 0;
        for (String orgId : orgIds) {
            rowCount += mapper.deleteById(orgId);
        }
        return rowCount;
    }

    @NotNull public Optional<IamOrg> detail(@NotNull String orgId) {
        return Optional.ofNullable(mapper.selectOneById(orgId));
    }

    @NotNull public Map<String, String> orgId2NameMap(@NotNull Collection<String> orgIds) {
        if (CollectionUtils.isEmpty(orgIds)) {
            return new HashMap<>();
        }
        return mapper.selectListByQuery(new QueryWrapper().in(IamOrg::getOrgId, orgIds)).stream()
                .collect(Collectors.toMap(IamOrg::getOrgId, IamOrg::getOrgName));
    }

    @Nullable @SuppressWarnings("unused")
    public IamOrg selectByOrgCode(@NotNull String orgCode) {
        return mapper.selectByOrgCode(orgCode);
    }

    @NotNull public List<IamOrgResp> listByParentId(@NotNull String orgParentId) {
        return mapper.listByParentId(orgParentId);
    }

    @Nullable public List<IamOrgResp> orgTree() {
        List<IamOrgResp> iamOrgResps = mapper.listByParentId("0");
        iamOrgResps.forEach(this::recursionTree);
        return iamOrgResps;
    }

    private void recursionTree(@NotNull IamOrgResp iamOrgResp) {
        iamOrgResp.setChildren(listByParentId(iamOrgResp.getOrgId()));
        iamOrgResp.getChildren().forEach(this::recursionTree);
    }

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
