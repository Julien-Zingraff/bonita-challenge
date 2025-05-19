/**
 * Copyright (C) 2024 BonitaSoft S.A.
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
package org.bonitasoft.studio.application.event;

import static java.util.Map.entry;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.bonitasoft.studio.application.statistics.StatisticEvent;
import org.bonitasoft.studio.application.ui.control.model.dependency.ArtifactType;
import org.bonitasoft.studio.common.ProductVersion;
import org.osgi.service.event.Event;

public abstract class ExtensionEvent extends Event implements StatisticEvent {

    public static final String INSTALL_TOPIC = "application/extension/installed";

    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String extensionType;

    protected ExtensionEvent(String topic,
            String groupId,
            String artifactId,
            String version,
            String extensionType) {
        super(topic, Map.ofEntries(entry("groupId", groupId),
                entry("artifactId", artifactId),
                entry("version", version),
                entry("extensionType", extensionType)));
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.extensionType = extensionType;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getExtensionType() {
        return extensionType;
    }

    @Override
    public Map<String, String> getParameters() {
        var parameters = new LinkedHashMap<String, String>();
        parameters.put("groupId", URLEncoder.encode(groupId, StandardCharsets.UTF_8));
        parameters.put("artifactId", URLEncoder.encode(artifactId, StandardCharsets.UTF_8));
        parameters.put("version", URLEncoder.encode(version, StandardCharsets.UTF_8));
        parameters.put("extensionType", URLEncoder.encode(extensionType, StandardCharsets.UTF_8));
        return parameters;
    }

    @Override
    public String toString() {
        return "ExtensionEvent [groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version
                + ", extensionType=" + extensionType + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(artifactId, extensionType, groupId, version);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExtensionEvent other = (ExtensionEvent) obj;
        return Objects.equals(artifactId, other.artifactId) && Objects.equals(extensionType, other.extensionType)
                && Objects.equals(groupId, other.groupId) && Objects.equals(version, other.version);
    }

    public static class ExtensionInstalledEvent extends ExtensionEvent {

        public ExtensionInstalledEvent(String groupId, String artifactId, String version,
                ArtifactType extensionType) {
            super(INSTALL_TOPIC, groupId, artifactId, version == null ? ProductVersion.BONITA_RUNTIME_VERSION : version,
                    extensionType.name().toLowerCase());
        }

    }

}
