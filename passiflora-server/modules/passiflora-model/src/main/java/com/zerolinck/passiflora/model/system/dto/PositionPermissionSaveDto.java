package com.zerolinck.passiflora.model.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author linck
 * @since 2024-06-29
 */
@Data
public class PositionPermissionSaveDto {

    @Schema(description = "职位ID", maxLength = 20)
    @Length(
            max = 20,
            message = "职位ID长度不能大于20"
    )
    @NotBlank(message = "职位ID不能为空")
    private String positionId;

    private List<String> permissionIds;
}
