/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.orchestrator.plugin.huaweicloud;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.xpanse.modules.ocl.loader.data.models.OclResources;
import org.eclipse.xpanse.orchestrator.OrchestratorStorage;
import org.springframework.core.env.Environment;

/**
 * Class to hold all runtime information of the builder.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BuilderContext extends HashMap<String, Map<String, String>> {

    private Environment environment;

    private OclResources oclResources = new OclResources();

    private String serviceName;

    private String pluginName;

    private Map<String, AtomBuilder> builderMap = new HashMap<>();

    private OrchestratorStorage storage;
}
