package org.bonitasoft.studio.importer.bpmn;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Optional;
import org.omg.spec.bpmn.model.TDefinitions;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class BPMNImporterUtil {

    public static Optional<BPMNToolExporter> getToolExporterFromImportFile(URL fileToImport) throws InvocationTargetException {
        try {
            TDefinitions docRootDefinitions = getTDefinitionsFromFile(fileToImport.toURI().toURL());
        } catch (URISyntaxException | UnsupportedEncodingException | MalformedURLException e) {
            throw new InvocationTargetException(e);
        }
        return getBPMNToolExporter(docRootDefinitions);
    }

    public static TDefinitions getTDefinitionsFromFile(URL sourceBPMNUrl) throws UnsupportedEncodingException, MalformedURLException {
        final File f = new File(URLDecoder.decode(sourceBPMNUrl.getFile(),
            "UTF-8"));
        final Resource resource = resourceSet.getResource(
            URI.createURI(f.toURI().toString()), true);

        final EObject rootContent = resource.getContents().get(0);
        if (rootContent == null || !(rootContent instanceof DocumentRoot)) {
            throw new MalformedURLException("Document type not supported");
        }

        final DocumentRoot docRoot = (DocumentRoot) rootContent;

        final TDefinitions docRootDefinitions = docRoot.getDefinitions();
        if (docRootDefinitions == null) {
            throw new MalformedURLException("Document type not supported");
        }
        return docRootDefinitions;
    }

    private static Optional<BPMNToolExporter> getBPMNToolExporter(TDefinitions definitions) {
        Optional<BPMNToolExporter> bpmnToolExporterOptional;
        String toolName = definitions.getExporter();
        String toolVersion = definitions.getExporterVersion();
        if (toolName == null) {
            bpmnToolExporterOptional = Optional.empty();
        } else {
            BPMNToolExporter exporter = new BPMNToolExporter(toolName, toolVersion);
            bpmnToolExporterOptional = Optional.of(exporter);
        }
        return bpmnToolExporterOptional;
    }

}