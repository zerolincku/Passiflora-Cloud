package com.zerolinck.passiflora.feign.${moduleName};

import com.zerolinck.passiflora.common.api.ListWithPage;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.config.FeignConfiguration;
import com.zerolinck.passiflora.model.${moduleName}.entity.${entityClass};
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linck
 * @since ${date}
 */
@Tag(name = "${table.description}")
@FeignClient(value = "${entityName}", contextId = "${entityName}", path="${contextPath}/${entityName}", configuration = FeignConfiguration.class)
public interface ${apiClass} {

    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<ListWithPage<${entityClass}>> page(QueryCondition<${entityClass}> condition);

    @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@RequestBody @Validated(Insert.class) ${entityClass} ${entityName});

    @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@RequestBody @Validated(Update.class) ${entityClass} ${entityName});

    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<${entityClass}> detail(@RequestParam(value = "${table.pkFieldName}") String ${table.pkFieldName});

    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<String> delete(@RequestBody List<String> ${table.pkFieldName}s);
}