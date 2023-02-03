/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.orchestrator.plugin.huaweicloud.builders;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.xpanse.modules.ocl.loader.data.models.Artifact;
import org.eclipse.xpanse.modules.ocl.loader.data.models.BaseImage;
import org.eclipse.xpanse.modules.ocl.loader.data.models.Ocl;
import org.eclipse.xpanse.orchestrator.plugin.huaweicloud.AtomBuilder;
import org.eclipse.xpanse.orchestrator.plugin.huaweicloud.BuilderContext;
import org.eclipse.xpanse.orchestrator.plugin.huaweicloud.builders.terraform.OclTerraformExecutor;
import org.eclipse.xpanse.orchestrator.plugin.huaweicloud.exceptions.BuilderException;

/**
 * Class to build all resources on Huawei cloud.
 */
@Slf4j
public class HuaweiResourceBuilder extends AtomBuilder {

    public HuaweiResourceBuilder(Ocl ocl) {
        super(ocl);
    }

    @Override
    public String name() {
        return "Huawei-Cloud-resource-Builder";
    }

    @Override
    public boolean create(BuilderContext ctx) {
        log.info("Creating Huawei Cloud resources.");
        if (ctx == null) {
            log.error("BuilderContext is invalid.");
            throw new BuilderException(this, "Builder context is null.");
        }

        Map<String, String> envCtx = ctx.get(new HuaweiEnvBuilder(ocl).name());
        if (envCtx == null) {
            log.error("Dependent builder: {} must build first.", new HuaweiEnvBuilder(ocl).name());
            throw new BuilderException(this, "HuaweiEnvBuilder context is null.");
        }

        for (Artifact artifact : ocl.getImage().getArtifacts()) {
            List<BaseImage> baseList = ocl.getImage().getBase();
            for (BaseImage image : baseList) {
                String id = image.getFilters().getId();
                if (StringUtils.isNoneBlank(id)) {
                    artifact.setId(id);
                    break;
                }
            }
        }

        OclTerraformExecutor tfExecutor = new OclTerraformExecutor(ocl, envCtx);

        tfExecutor.createWorkspace();
        tfExecutor.createTerraformScript();

        if (!tfExecutor.tfInit()) {
            throw new BuilderException(this, "TFExecutor.tfInit failed." + name());
        }
        if (!tfExecutor.tfPlan()) {
            throw new BuilderException(this, "TFExecutor.tfPlan failed." + name());
        }
        if (!tfExecutor.tfApply()) {
            throw new BuilderException(this, "TFExecutor.tfApply failed." + name());
        }

        tfExecutor.updateOclResources(ctx.getOclResources());

        return true;
    }

    @Override
    public boolean destroy(BuilderContext ctx) {
        log.info("Destroying Huawei Cloud resources.");
        OclTerraformExecutor tfExecutor = new OclTerraformExecutor(ocl,
            ctx.get(new HuaweiEnvBuilder(ocl).name()));

        tfExecutor.createWorkspace();
        tfExecutor.createTerraformScript();

        if (!tfExecutor.tfInit()) {
            log.error("ResourceBuilder Init failed {}.", name());
            throw new BuilderException(this, "TFExecutor.tfInit failed " + name());
        }
        if (!tfExecutor.tfPlan()) {
            throw new BuilderException(this, "TFExecutor.tfPlan failed." + name());
        }
        if (!tfExecutor.tfDestroy()) {
            log.error("ResourceBuilder destroy failed {}.", name());
            return false;
        }

        return true;
    }

}
