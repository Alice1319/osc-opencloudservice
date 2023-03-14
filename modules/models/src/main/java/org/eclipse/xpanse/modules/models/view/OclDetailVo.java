/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */


package org.eclipse.xpanse.modules.models.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.xpanse.modules.models.enums.ServiceState;
import org.eclipse.xpanse.modules.models.resource.Ocl;

/**
 * Define view object for UI Client query registered service.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OclDetailVo extends Ocl {

    @NotNull
    @Schema(description = "ID of the registered service.")
    private UUID id;

    @NotNull
    @Schema(description = "Time of register service.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Time of update service.")
    private Date lastModifiedTime;

    @NotNull
    @Schema(description = "State of service.")
    private ServiceState serviceState;

}
