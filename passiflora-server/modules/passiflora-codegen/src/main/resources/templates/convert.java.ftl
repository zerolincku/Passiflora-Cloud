package com.zerolinck.passiflora.model.${moduleName}.mapperstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * ${table.description} MapStruct Convert
 *
 * @author ${author} on 2024-03-26
 */
@Mapper
@SuppressWarnings("all")
public interface ${entityClass}Convert {

    ${entityClass}Convert INSTANCE = Mappers.getMapper(${entityClass}Convert.class);

}
