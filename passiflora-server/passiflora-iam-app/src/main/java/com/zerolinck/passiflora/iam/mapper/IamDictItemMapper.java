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
package com.zerolinck.passiflora.iam.mapper;

import static com.mybatisflex.core.query.QueryMethods.select;
import static com.zerolinck.passiflora.model.iam.entity.table.IamDictItemTableDef.IAM_DICT_ITEM;
import static com.zerolinck.passiflora.model.iam.entity.table.IamDictTableDef.IAM_DICT;

import java.util.Collection;
import java.util.List;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.zerolinck.passiflora.model.iam.entity.IamDictItem;
import com.zerolinck.passiflora.mybatis.util.FlexPage;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 字典项 Mybatis Mapper
 *
 * @author linck
 * @since 2024-04-01
 */
public interface IamDictItemMapper extends BaseMapper<IamDictItem> {

    /**
     * 分页查询
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param queryWrapper 查询条件
     * @author 林常坤 on 2025/1/24
     */
    default com.zerolinck.passiflora.common.api.Page<IamDictItem> page(
            Number pageNum, Number pageSize, QueryWrapper queryWrapper) {
        Page<IamDictItem> paginate = paginate(pageNum, pageSize, queryWrapper);
        return FlexPage.convert(paginate);
    }

    /**
     * 根据字典ID集合删除字典项
     *
     * @param dictIds 字典ID集合
     * @return 删除的字典项数量
     * @since 2024-04-01
     */
    @SuppressWarnings("UnusedReturnValue")
    default int deleteByDictIds(@Nullable Collection<String> dictIds) {
        if (CollectionUtils.isEmpty(dictIds)) {
            return 0;
        }

        return this.deleteByCondition(QueryCondition.createEmpty().and(IAM_DICT_ITEM.DICT_ID.in(dictIds)));
    }

    /**
     * 根据字典ID查询字典项列表
     *
     * @param dictId 字典ID
     * @return 字典项列表
     * @since 2024-04-01
     */
    default List<IamDictItem> listByDictId(@NotNull String dictId) {
        return this.selectListByQuery(QueryWrapper.create().where(IAM_DICT_ITEM.DICT_ID.eq(dictId)));
    }

    /**
     * 根据字典名称查询字典项列表
     *
     * @param dictName 字典名称
     * @return 字典项列表
     * @since 2024-04-01
     */
    default List<IamDictItem> listByDictName(@NotNull String dictName) {
        return this.selectListByQuery(QueryWrapper.create()
                .where(IAM_DICT_ITEM.DICT_ID.in(
                        select(IAM_DICT.DICT_ID).from(IAM_DICT).where(IAM_DICT.DICT_NAME.eq(dictName)))));
    }

    /**
     * 根据字典标签查询字典项列表
     *
     * @param dictTag 字典标签
     * @return 字典项列表
     * @since 2024-04-01
     */
    default List<IamDictItem> listByDictTag(@NotNull String dictTag) {
        return this.selectListByQuery(QueryWrapper.create()
                .where(IAM_DICT_ITEM.DICT_ID.in(
                        select(IAM_DICT.DICT_ID).from(IAM_DICT).where(IAM_DICT.DICT_TAG.eq(dictTag)))));
    }
}
