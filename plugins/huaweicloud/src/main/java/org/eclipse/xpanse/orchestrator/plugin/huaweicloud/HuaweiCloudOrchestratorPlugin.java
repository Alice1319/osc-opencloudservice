/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.orchestrator.plugin.huaweicloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.xpanse.modules.ocl.loader.data.models.Ocl;
import org.eclipse.xpanse.modules.ocl.loader.data.models.OclResources;
import org.eclipse.xpanse.orchestrator.OrchestratorPlugin;
import org.eclipse.xpanse.orchestrator.OrchestratorStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Plugin to deploy managed services on Huawei cloud.
 */
@Slf4j
@Component
@Profile(value = "huaweicloud")
public class HuaweiCloudOrchestratorPlugin implements OrchestratorPlugin {

    private final Map<String, Ocl> managedOcl = new HashMap<>();

    private final Environment environment;

    private final OrchestratorStorage orchestratorStorage;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public HuaweiCloudOrchestratorPlugin(Environment environment,
        OrchestratorStorage orchestratorStorage) {
        this.environment = environment;
        this.orchestratorStorage = orchestratorStorage;
    }

    @Override
    public void registerManagedService(Ocl ocl) {
        log.info("Register managed service, creating  Huawei Cloud resource");
        if (ocl == null) {
            throw new IllegalArgumentException("registering invalid ocl. ocl = null");
        }
        managedOcl.put(ocl.getName(), ocl);
        OclResources oclResources = getOclResources(ocl.getName());
        if (oclResources.getState().equals("success")) {
            log.info("Managed service {} already in active.", ocl.getName());
        }
        oclResources.setState("registered");
        storeOclResources(ocl.getName(), oclResources);
    }

    @Override
    public void updateManagedService(String managedServiceName, Ocl ocl) {
        log.info("Updating managed service {} on Huawei Cloud", managedServiceName);
        if (ocl == null) {
            throw new IllegalArgumentException("Invalid ocl. ocl = null");
        }
        managedOcl.put(managedServiceName, ocl);
    }

    @Override
    public void startManagedService(String managedServiceName) {
        log.info("Start managed service {} on Huawei Cloud", managedServiceName);
        if (!managedOcl.containsKey(managedServiceName)) {
            throw new IllegalArgumentException("Service:" + managedServiceName + "not registered.");
        }

        Ocl ocl = managedOcl.get(managedServiceName).deepCopy();
        if (ocl == null) {
            throw new IllegalStateException("Ocl object is null.");
        }

        OclResources oclResources = getOclResources(managedServiceName);
        if (oclResources != null && oclResources.getState().equals("success")) {
            log.info("Managed service {} already in active.", managedServiceName);
            return;
        }
        BuilderContext ctx = new BuilderContext();
        ctx.setEnvironment(this.environment);
        ctx.setServiceName(managedServiceName);
        String pluginName = HuaweiCloudOrchestratorPlugin.class.getSimpleName();
        ctx.setPluginName(pluginName);

        BuilderFactory factory = new BuilderFactory();
        AtomBuilder envBuilder = factory.createBuilder(BuilderFactory.ENV_BUILDER, ocl);
        ctx.getBuilderMap().put(BuilderFactory.ENV_BUILDER, envBuilder);

        AtomBuilder basicBuilder = factory.createBuilder(BuilderFactory.BASIC_BUILDER,
            ocl);
        ctx.getBuilderMap().put(BuilderFactory.BASIC_BUILDER, basicBuilder);

        ctx.setStorage(this.orchestratorStorage);
        envBuilder.build(ctx);
        basicBuilder.build(ctx);
    }

    @Override
    public void stopManagedService(String managedServiceName) {
        log.info("Stop managed service {} on Huawei Cloud", managedServiceName);
        if (!managedOcl.containsKey(managedServiceName)) {
            throw new IllegalArgumentException("Service:" + managedServiceName + "not registered.");
        }

        BuilderContext ctx = new BuilderContext();
        ctx.setEnvironment(this.environment);

        Ocl ocl = managedOcl.get(managedServiceName).deepCopy();
        if (ocl == null) {
            throw new IllegalStateException("Ocl object is null.");
        }

        BuilderFactory factory = new BuilderFactory();
        AtomBuilder envBuilder = factory.createBuilder(BuilderFactory.ENV_BUILDER, ocl);
        AtomBuilder basicBuilder = factory.createBuilder(BuilderFactory.BASIC_BUILDER,
            ocl);
        envBuilder.rollback(ctx);
        basicBuilder.rollback(ctx);
        ctx.getOclResources().setState("stopped");
        storeOclResources(managedServiceName, new OclResources());
    }

    @Override
    public void unregisterManagedService(String managedServiceName) {
        log.info("Destroy managed service {} from Huawei Cloud", managedServiceName);
        if (!managedOcl.containsKey(managedServiceName)) {
            throw new IllegalArgumentException("Service:" + managedServiceName + "not registered.");
        }
        managedOcl.remove(managedServiceName);
    }

    private AtomBuilder createBuilder(String managedServiceName) {
        Ocl ocl = managedOcl.get(managedServiceName).deepCopy();
        if (ocl == null) {
            throw new IllegalStateException("Ocl object is null.");
        }

        BuilderFactory factory = new BuilderFactory();
        return factory.createBuilder(BuilderFactory.BASIC_BUILDER, ocl);
    }

    private void storeOclResources(String managedServiceName, OclResources oclResources) {
        String oclResourceStr;
        try {
            oclResourceStr = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(oclResources);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Serial OCL object to json failed.", ex);
        }

        if (this.orchestratorStorage != null) {
            this.orchestratorStorage.store(managedServiceName,
                HuaweiCloudOrchestratorPlugin.class.getSimpleName(), "state", oclResourceStr);
        } else {
            log.warn("storage is null.");
        }
    }

    private OclResources getOclResources(String managedServiceName) {
        OclResources oclResources;
        String oclResourceStr;
        try {
            if (this.orchestratorStorage != null) {
                oclResourceStr = this.orchestratorStorage.getKey(managedServiceName,
                    HuaweiCloudOrchestratorPlugin.class.getSimpleName(), "state");
                oclResources = objectMapper.readValue(oclResourceStr, OclResources.class);
            } else {
                oclResources = new OclResources();
            }
        } catch (JsonProcessingException ex) {
            log.error("Serial OCL object to json failed.", ex);
            oclResources = new OclResources();
        }
        return oclResources;
    }
}
