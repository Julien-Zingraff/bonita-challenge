package org.bonitasoft.studio.tests;

import org.bonitasoft.studio.application.LifeCycleManager;
import org.bonitasoft.studio.application.event.ExtensionEvent.ExtensionInstalledEvent;
import org.bonitasoft.studio.application.statistics.StatisticsManager;

public class TestLifeCycleManager extends LifeCycleManager {
    
    @Override
    protected Class<?> getStatisticsManagerImplementation() {
        return NoopStatisticsManager.class;
    }
    
    static class NoopStatisticsManager implements StatisticsManager {

        @Override
        public boolean isEnabled() {
            return false;
        }
        
        @Override
        public void extensionInstalled(ExtensionInstalledEvent extensionInstalledEvent) {
           // DO NOTHING
        }
        
    }

}
