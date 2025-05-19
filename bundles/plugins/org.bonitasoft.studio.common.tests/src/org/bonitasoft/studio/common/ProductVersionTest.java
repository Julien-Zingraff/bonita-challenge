package org.bonitasoft.studio.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.osgi.framework.Version;

public class ProductVersionTest {
    
    @Test
    public void shouldCurrentProductVersionEquals_POMVersionIgnoringQualifier() throws Exception {
        final Version osgiVersion = loadBundleVersion();
        final Version current = new Version(ProductVersion.CURRENT_VERSION);
        assertThat(current.getMajor()).isEqualTo(osgiVersion.getMajor());
        assertThat(current.getMinor()).isEqualTo(osgiVersion.getMinor());
        assertThat(current.getMicro()).isEqualTo(osgiVersion.getMicro());
    }

    private Version loadBundleVersion() {
        return Platform.getBundle("org.bonitasoft.studio.common").getVersion();
    }

    @Test
    public void should_not_import_version_below_6_0_0() throws Exception {
        assertThat(ProductVersion.canBeImported("5.10.0")).isFalse();
        assertThat(ProductVersion.canBeImported("6.0.0")).isTrue();
    }
    @Test
    public void should_detect_timestamp_qualifier() throws Exception {
        assertThat(ProductVersion.hasTimestampQualifier(new Version("1.0.0.1233274"))).isTrue();
        assertThat(ProductVersion.hasTimestampQualifier(new Version("1.0.0"))).isFalse();
        assertThat(ProductVersion.hasTimestampQualifier(new Version("1.0.0.alpha-01"))).isFalse();
        assertThat(ProductVersion.hasTimestampQualifier(new Version("1.0.0.qualifier"))).isTrue();
    }

}
