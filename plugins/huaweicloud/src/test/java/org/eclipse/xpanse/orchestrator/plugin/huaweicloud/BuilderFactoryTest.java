/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.orchestrator.plugin.huaweicloud;

import java.util.Objects;
import java.util.Optional;
import org.eclipse.xpanse.modules.ocl.loader.data.models.Ocl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BuilderFactoryTest {

    @Test
    public void basicBuilderTest() {
        BuilderFactory builderFactory = new BuilderFactory();

        AtomBuilder builder = builderFactory.createBuilder(BuilderFactory.BASIC_BUILDER,
            new Ocl());

        Assertions.assertTrue(Objects.nonNull(builder));
    }

    @Test
    public void unsupportedBuilderTest() {
        BuilderFactory builderFactory = new BuilderFactory();

        AtomBuilder builder = builderFactory.createBuilder("invalid", new Ocl());

        Assertions.assertTrue(Objects.isNull(builder));
    }

    @Test
    public void basicBuilderDeprecatedTest() {
        BuilderFactory builderFactory = new BuilderFactory();

        AtomBuilder builder =
            builderFactory.createBuilder(BuilderFactory.BASIC_BUILDER_DEPRECATED, new Ocl());

        Assertions.assertTrue(Objects.nonNull(builder));
    }

    @Test
    public void envBuilderTest() {
        BuilderFactory builderFactory = new BuilderFactory();

        AtomBuilder builder =
            builderFactory.createBuilder(BuilderFactory.ENV_BUILDER, new Ocl());

        Assertions.assertEquals("Huawei-Cloud-env-Builder", builder.name());
    }

    @Test
    public void basicBuilderDeprecatedTest() {
        BuilderFactory builderFactory = new BuilderFactory();

        Optional<AtomBuilder> builder =
            builderFactory.createBuilder(BuilderFactory.BASIC_BUILDER_DEPRECATED, new Ocl());

        Assertions.assertTrue(builder.isPresent());
    }

    @Test
    public void envBuilderTest() {
        BuilderFactory builderFactory = new BuilderFactory();

        Optional<AtomBuilder> builder =
            builderFactory.createBuilder(BuilderFactory.ENV_BUILDER, new Ocl());

        Assertions.assertTrue(builder.isPresent());
    }
}
