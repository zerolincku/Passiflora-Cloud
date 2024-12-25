package com.zerolinck.passiflora.feign.${moduleName};

import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.config.FeignConfiguration;
import com.zerolinck.passiflora.model.${moduleName}.entity.${entityClass};
import com.zerolinck.passiflora.base.valid.Insert;
import com.zerolinck.passiflora.base.valid.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ${author} on ${date}
 */
@Tag(name = "${table.description}")
@FeignClient(value = "passiflora-${moduleName}-app", contextId = "${entityName}", path="${contextPath}/${entityNameUrl}", configuration = FeignConfiguration.class)
public interface ${apiClass} {

    @NotNull
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<List<${entityClass}>> page(@NotNull @SpringQueryMap QueryCondition<${entityClass}> condition);

    @NotNull
    @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@NotNull @RequestBody @Validated(Insert.class) ${entityClass} ${entityName});

    @NotNull
    @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@NotNull @RequestBody @Validated(Update.class) ${entityClass} ${entityName});

    @NotNull
    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<${entityClass}> detail(@Nullable @RequestParam(value = "${table.pkFieldName}", required = false) String ${table.pkFieldName});

    @NotNull
    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<Void> delete(@NotNull @RequestBody List<String> ${table.pkFieldName}s);
}