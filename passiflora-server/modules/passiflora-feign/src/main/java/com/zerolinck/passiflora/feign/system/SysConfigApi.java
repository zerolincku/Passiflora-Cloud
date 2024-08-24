package com.zerolinck.passiflora.feign.system;

import com.zerolinck.passiflora.common.api.ListWithPage;
import com.zerolinck.passiflora.common.api.Result;
import com.zerolinck.passiflora.common.util.QueryCondition;
import com.zerolinck.passiflora.feign.config.FeignConfiguration;
import com.zerolinck.passiflora.model.system.entity.SysConfig;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 林常坤
 * @since 2024-08-24
 */
@Tag(name = "系统配置")
@FeignClient(value = "sysConfig", contextId = "sysConfig", path="/passiflora/system-api/sysConfig", configuration = FeignConfiguration.class)
public interface SysConfigApi {

    @Nonnull
    @Operation(summary = "分页查询")
    @GetMapping("page")
    Result<ListWithPage<SysConfig>> page(@Nullable QueryCondition<SysConfig> condition);

    @Nonnull
    @Operation(summary = "新增")
    @PostMapping("add")
    Result<String> add(@Nonnull @RequestBody @Validated(Insert.class) SysConfig sysConfig);

    @Nonnull
    @Operation(summary = "更新")
    @PostMapping("update")
    Result<String> update(@Nonnull @RequestBody @Validated(Update.class) SysConfig sysConfig);

    @Nonnull
    @Operation(summary = "详情")
    @GetMapping("detail")
    Result<SysConfig> detail(@Nonnull @RequestParam(value = "configId") String configId);

    @Nonnull
    @Operation(summary = "删除")
    @PostMapping("delete")
    Result<String> delete(@Nonnull @RequestBody List<String> configIds);
}