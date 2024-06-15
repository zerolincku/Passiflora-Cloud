package com.zerolinck.passiflora.${moduleName}.controller;

import com.zerolinck.passiflora.common.api.ListWithPage;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.util.AssertUtil;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.${moduleName}.${apiClass};
import com.zerolinck.passiflora.model.${moduleName}.entity.${entityClass};
import com.zerolinck.passiflora.${moduleName}.service.${serviceClass};
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ${table.description} Controller
 *
 * @author linck
 * @since ${date}
 */
@Slf4j
@RestController
@RequestMapping("${entityName}")
public class ${controllerClass} implements ${apiClass} {

    @Resource
    private ${serviceClass} ${serviceName};

    @Override
    public Result<ListWithPage<${entityClass}>> page(QueryCondition<${entityClass}> condition) {
        return Result.page(${serviceName}.page(condition));
    }

    @Override
    public Result<String> add(${entityClass} ${entityName}) {
        ${entityName}.set${table.pkFieldName[0..0]?upper_case}${table.pkFieldName[1..]}(null);
        ${serviceName}.add(${entityName});
        return Result.ok(${entityName}.get${table.pkFieldName[0..0]?upper_case}${table.pkFieldName[1..]}());
    }

    @Override
    public Result<String> update(${entityClass} ${entityName}) {
        boolean success = ${serviceName}.update(${entityName});
        if (success) {
            return Result.ok(${entityName}.get${table.pkFieldName[0..0]?upper_case}${table.pkFieldName[1..]}());
        } else {
            return Result.failed(ResultCodeEnum.COMPETE_FAILED);
        }
    }

    @Override
    public Result<${entityClass}> detail(String ${table.pkFieldName}) {
        AssertUtil.notBlank(${table.pkFieldName});
        return Result.ok(${serviceName}.detail(${table.pkFieldName}));
    }

    @Override
    public Result<String> delete(List<String> ${table.pkFieldName}s) {
        AssertUtil.notEmpty(${table.pkFieldName}s);
        ${serviceName}.deleteByIds(${table.pkFieldName}s);
        return Result.ok();
    }

}
