package com.zerolinck.passiflora.model.system.mapperstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 系统配置 MapStruct Convert
 *
 * @author 林常坤
 * @since 2024-03-26
 */
@Mapper
@SuppressWarnings("all")
public interface SysConfigConvert {

    SysConfigConvert INSTANCE = Mappers.getMapper(SysConfigConvert.class);

}
