/**
 * Copyright (C) 2009-2012 BonitaSoft S.A.
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
package org.bonitasoft.studio.importer.handler;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Optional;

import org.bonitasoft.studio.common.log.BonitaStudioLog;
import org.bonitasoft.studio.common.repository.RepositoryManager;
import org.bonitasoft.studio.common.ui.PlatformUtil;
import org.bonitasoft.studio.common.ui.jface.BonitaErrorDialog;
import org.bonitasoft.studio.common.ui.jface.CustomWizardDialog;
import org.bonitasoft.studio.diagram.custom.repository.DiagramFileStore;
import org.bonitasoft.studio.diagram.custom.repository.DiagramRepositoryStore;
import org.bonitasoft.studio.importer.ImporterPlugin;
import org.bonitasoft.studio.importer.ImporterUtil;
import org.bonitasoft.studio.importer.bpmn.BPMNImporterUtil;
import org.bonitasoft.studio.importer.bpmn.BPMNToolExporter;
import org.bonitasoft.studio.importer.i18n.Messages;
import org.bonitasoft.studio.importer.processors.ImportFileOperation;
import org.bonitasoft.studio.importer.ui.wizard.ImportFileWizard;
import org.bonitasoft.studio.ui.dialog.SkippableProgressMonitorJobsDialog;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Romain Bioteau
 */
public class ImportOtherHandler {

    @Execute
    public void execute() {
        final ImportFileWizard importFileWizard = createImportWizard();
        CustomWizardDialog customWizardDialog = new CustomWizardDialog(Display.getDefault().getActiveShell(), importFileWizard, Messages.importButtonLabel);
        if (customWizardDialog.open() == Dialog.OK) {
            final File selectedFile = new File(importFileWizard.getSelectedFilePath());
            final SkippableProgressMonitorJobsDialog progressManager = new SkippableProgressMonitorJobsDialog(
                    Display.getDefault().getActiveShell());

            final ImportFileOperation operation = createImportFileOperation(importFileWizard, selectedFile, progressManager);

            try {
                // 1 & 2 => Manage Tool Exporter
                BPMNToolExporter bpmnToolExporter = this.manageToolExporter(selectedFile, customWizardDialog, importFileWizard);

                progressManager.run(false, false, operation);

                // 3 => Display Tool Exporter after Import
                //use customWizardDialog to display BPMNToolExporter

                // 4 => Log the import Event
                this.logImportEvent(bpmnToolExporter);
            } catch (final InvocationTargetException | InterruptedException e) {
                final Throwable t = e instanceof InvocationTargetException
                        ? ((InvocationTargetException) e).getTargetException() : e;
                BonitaStudioLog.error("Import has failed for file " + selectedFile.getName(), ImporterPlugin.PLUGIN_ID);
                BonitaStudioLog.error(e, ImporterPlugin.PLUGIN_ID);
                String message = Messages.errorWhileImporting_message;
                if (t != null && !isNullOrEmpty(t.getMessage())) {
                    message = t.getMessage();
                }
                new BonitaErrorDialog(Display.getDefault().getActiveShell(), Messages.errorWhileImporting_title, message, e)
                        .open();
            }
            for (final DiagramFileStore fileStore : operation.getFileStoresToOpen()) {
                fileStore.open();
            }
            PlatformUtil.openIntroIfNoOtherEditorOpen();
            Display.getDefault().asyncExec(openStatusDialog(operation));
        }
    }

    private Runnable openStatusDialog(final ImportFileOperation operation) {
        return new Runnable() {

            @Override
            public void run() {
                operation.getImportStatusDialogHandler(operation.getStatus()).open(Display.getDefault().getActiveShell());
            }
        };
    }

    protected DiagramRepositoryStore getDiagramRepositoryStore() {
        return RepositoryManager.getInstance().getRepositoryStore(DiagramRepositoryStore.class);
    }

    protected ImportFileOperation createImportFileOperation(final ImportFileWizard importFileWizard, final File selectedFile,
            final SkippableProgressMonitorJobsDialog progressManager) {
        return new ImportFileOperation(importFileWizard.getSelectedTransfo(),
                selectedFile, progressManager);
    }

    protected ImportFileWizard createImportWizard() {
        return new ImportFileWizard();
    }
    
    @CanExecute
    public boolean isEnabled() {
        if (RepositoryManager.getInstance().hasActiveRepository()) {
            return true;
        }
        return false;
    }

    private BPMNToolExporter manageToolExporter(File selectedFile, CustomWizardDialog customWizardDialog, ImportFileWizard importFileWizard) throws InterruptedException, InvocationTargetException {

        try {
            Optional<BPMNToolExporter> bpmnToolExporterOptional = BPMNImporterUtil.getToolExporterFromImportFile(selectedFile.toURI().toURL());
            if (bpmnToolExporterOptional.isEmpty()) {
                // TODO: prompt User
                // use customWizardDialog with updated importFileWizard
                // Update ImportFileWizardPage to manage Selection of ToolExporter or Others from User

                //Set with user values
                if (customWizardDialog.open() == Dialog.CANCEL) {
                    //Abort Import
                    //  status = new Status(IStatus.ERROR, ImporterPlugin.PLUGIN_ID, "ImportFileWizard Aborted";
                    //  throw new InvocationTargetException("ImportFileWizard Aborted");
                }

                String toolExporter = importFileWizard.getToolExporterName();
                return new BPMNToolExporter(toolExporter, null);
            } else {
                return bpmnToolExporterOptional.get();
            }
        } catch (MalformedURLException e) {
            throw new InvocationTargetException(e);
        }
    }

    private void logImportEvent(BPMNToolExporter bpmnToolExporter) {
        BonitaStudioLog.info(String.format("BPMN file imported from %s version %s", bpmnToolExporter.getName(), bpmnToolExporter.getVersion()));
    }

}
