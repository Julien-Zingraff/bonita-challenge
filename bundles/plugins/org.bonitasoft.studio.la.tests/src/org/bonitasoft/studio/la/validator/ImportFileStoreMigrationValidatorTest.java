package org.bonitasoft.studio.la.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

import java.io.FileInputStream;

import org.bonitasoft.engine.business.application.exporter.ApplicationNodeContainerConverter;
import org.bonitasoft.studio.junit.rules.FileResource;
import org.bonitasoft.studio.la.application.repository.ApplicationRepositoryStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ImportFileStoreMigrationValidatorTest {

    private static final NullProgressMonitor MONITOR = new NullProgressMonitor();
    private ApplicationRepositoryStore store;
    private IProject proj;

    @Before
    public void prepareStore() throws Exception {
        store = spy(new ApplicationRepositoryStore());
        Mockito.doReturn(new ApplicationNodeContainerConverter()).when(store).getConverter();
        proj = ResourcesPlugin.getWorkspace().getRoot().getProject("ImportFileStoreMigrationValidatorTest");
        proj.create(MONITOR);
        proj.open(MONITOR);
        var folder = proj.getFolder("test");
        folder.create(true, true, MONITOR);
        Mockito.doReturn(folder).when(store).getResource();
    }

    @After
    public void cleanProject() throws Exception {
        proj.delete(true, MONITOR);
    }

    @Test
    public void should_migrate_on_older_schema() throws Exception {
        var appFile = new FileResource(ImportFileStoreMigrationValidatorTest.class.getResource("/myOldApp.xml"));
        var appFileStore = store.importInputStream("myOldApp.xml", new FileInputStream(appFile.getFile()));
        assertThat(appFileStore.getMigrationReport().updates()).contains(
                String.format("%s application descriptor has been migrated to the latest schema version.",
                        "myOldApp.xml"));
        assertThat(appFileStore.getContent()).isNotNull();
        assertThat(appFileStore.getContent().getApplicationLinks()).isEmpty();
    }

    @Test
    public void should_not_migrate_new_schema() throws Exception {
        var appFile = new FileResource(ImportFileStoreMigrationValidatorTest.class.getResource("/myApp.xml"));
        var appFileStore = store.importInputStream("myApp.xml", new FileInputStream(appFile.getFile()));
        assertThat(appFileStore.getMigrationReport().updates()).isEmpty();
        assertThat(appFileStore.getContent()).isNotNull();
        assertThat(appFileStore.getContent().getApplicationLinks()).isNotEmpty();
    }
}
