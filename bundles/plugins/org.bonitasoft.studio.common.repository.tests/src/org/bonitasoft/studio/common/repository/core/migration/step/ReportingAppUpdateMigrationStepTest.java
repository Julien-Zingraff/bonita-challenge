/**
 * Copyright (C) 2022 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.studio.common.repository.core.migration.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.IOException;

import org.apache.maven.model.Model;
import org.bonitasoft.studio.common.repository.core.maven.model.ProjectMetadata;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.MavenPlugin;
import org.junit.jupiter.api.Test;

class ReportingAppUpdateMigrationStepTest {

    private static ProjectMetadata metadata = ProjectMetadata.defaultMetadata();

    @Test
    void shouldUpdateOldReportingAppArtifact() throws Exception {
        var step = new ReportingAppUpdateMigrationStep();
        var model = load("/pom.xml.reporting-app.test");
        var report = step.migrate(model, metadata);

        assertThat(report.updates()).hasSize(1);
        assertThat(model.getDependencies())
                .extracting("groupId", "artifactId", "version")
                .contains(tuple("com.bonitasoft.web.application", "bonita-reporting-application", "2.0.0"));
    }

    @Test
    void shouldDoNothing() throws Exception {
        var step = new ReportingAppUpdateMigrationStep();
        var model = remove(load("/pom.xml.reporting-app.test"), "bonita-reporting-application");
       
        var report = step.migrate(model, metadata);

        assertThat(report.updates()).isEmpty();
        assertThat(model.getDependencies())
                .extracting("groupId", "artifactId", "version")
                .doesNotContain(tuple("com.bonitasoft.web.application", "bonita-reporting-application", "2.0.0"));
    }

    private static Model remove(Model model, String... artifactIds) {
        for(var artifactId : artifactIds) {
            model.getDependencies().removeIf(dep -> dep.getArtifactId().equals(artifactId));
        }
        return model;
    }

    @Test
    void shouldAppliesToProjectOldReportingAppArtifacts() throws Exception {
        var step = new ReportingAppUpdateMigrationStep();
        var model = load("/pom.xml.reporting-app.test");

        assertThat(step.appliesTo(model, metadata)).isTrue();
    }

    private static Model load(String resourcePath) throws IOException, CoreException {
        try (var is = ReportingAppUpdateMigrationStepTest.class.getResourceAsStream(resourcePath)) {
            return MavenPlugin.getMaven().readModel(is);
        }
    }

}
