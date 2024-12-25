package com.zerolinck.passiflora.model.${moduleName}.entity;

<#if table.extendsBase>
import com.zerolinck.passiflora.base.BaseEntity;
</#if>


import com.zerolinck.passiflora.base.valid.Insert;
import com.zerolinck.passiflora.base.valid.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

/**
 * ${table.description} Entity
 *
 * @author ${author} on ${date}
 */
@Data
@Schema(description = "${table.description}")
<#if table.extendsBase>
@EqualsAndHashCode(callSuper = false)
</#if>
public class ${entityClass} <#if table.extendsBase>extends BaseEntity </#if>{

<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.columnList as column>
    <#if column.pk>
    @Id
    </#if>
    <#if column.fieldType.getSimpleName() == "String" && column.length??>
    <#if column.defaultValue?? && column.defaultValue != "''::character varying">
    @Schema(description = "${column.description}", maxLength = ${column.length}, defaultValue = "${column.defaultValue}")
    <#else>
    @Schema(description = "${column.description}", maxLength = ${column.length})
    </#if>
    @Length(groups = {Insert.class, Update.class}, max = ${column.length}, message = "${column.description}长度不能大于${column.length}")
    <#else>
    <#if column.defaultValue?? && column.defaultValue != "''::character varying">
    @Schema(description = "${column.description}", defaultValue = "${column.defaultValue}")
    <#else>
    @Schema(description = "${column.description}")
    </#if>
    </#if>
    <#if column.nullable>
        <#if column.fieldType.getSimpleName() == "String">
    @NotBlank(groups = {<#if column.pk>Update.class<#else>Insert.class, Update.class</#if>}, message = "${column.description}不能为空")
        <#else>
    @NotNull(groups = {Insert.class, Update.class}, message = "${column.description}不能为空")
        </#if>
    </#if>
    private ${column.fieldType.getSimpleName()} ${column.fieldName};

</#list>
}
