package org.bonitasoft.studio.sqlbuilder.ex.wizard;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.studio.common.extension.BonitaStudioExtensionRegistryManager;
import org.eclipse.core.runtime.IConfigurationElement;
import org.junit.jupiter.api.Test;

class CustomConnectorDefinitionWizardTest {
    
    private static final String SPECIFIC_CONNECTOR_WIZARD = "org.bonitasoft.studio.connectors.connectorWizard";

    private static final Map<String,String> WIZARD_IDS ;
    static{
        WIZARD_IDS = new HashMap<>();
        WIZARD_IDS.put("database-mysql","1.0.0");
        WIZARD_IDS.put("database-db2","1.0.0");
        WIZARD_IDS.put("database-h2","1.0.0");
        WIZARD_IDS.put("database-hsqldb","1.0.0");
        WIZARD_IDS.put("database-oracle10g","1.0.0");
        WIZARD_IDS.put("database-oracle11g","1.0.0");
        WIZARD_IDS.put("database-postgresql84","1.0.0");
        WIZARD_IDS.put("database-postgresql92","1.0.0");
        WIZARD_IDS.put("database-mssqlserver","1.2.1");
    }

    @Test
    void testSpecificWizardDefinitionId() throws Exception {
        for (String id: WIZARD_IDS.keySet()) {
            boolean found = false ;
            for(IConfigurationElement element : BonitaStudioExtensionRegistryManager.getInstance().getConfigurationElements(SPECIFIC_CONNECTOR_WIZARD)){
                String defId = element.getAttribute("DefinitionId");
                if (defId.equals(id)) {
                    found = true ;
                }
            }
            assertTrue(found, "No specific wizard page found for "+id);
        }
    }

}
