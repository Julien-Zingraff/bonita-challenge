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
package org.bonitasoft.studio.application.statistics;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import org.bonitasoft.studio.application.event.ExtensionEvent;
import org.bonitasoft.studio.application.event.ExtensionEvent.ExtensionInstalledEvent;
import org.bonitasoft.studio.common.RedirectURLBuilder;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class DefaultStatisticsManager implements StatisticsManager, EventHandler {

    private static final String REDIRECT_ID = "765";
    
    private IEventBroker eventBroker;
    private StatisticsHttpClientBuilder httpClientBuilder;
    
    @Inject
    public DefaultStatisticsManager(IEventBroker eventBroker, StatisticsHttpClientBuilder httpClientBuilder) {
        this.eventBroker = eventBroker;
        this.httpClientBuilder = httpClientBuilder;
    }

    @PostConstruct
    protected void initialize() {
        eventBroker.subscribe(ExtensionEvent.INSTALL_TOPIC, this);
    }

    @Override
    public void extensionInstalled(ExtensionInstalledEvent extensionInstalledEvent) {
        if (isEnabled()) {
            eventBroker.post(ExtensionEvent.INSTALL_TOPIC, extensionInstalledEvent);
        }
    }

    protected void pushEvent(StatisticEvent event) {
        var uri = RedirectURLBuilder.createURI(REDIRECT_ID, event.getParameters());
        httpClientBuilder.get().sendAsync(HttpRequest.newBuilder(uri).build(), BodyHandlers.discarding());
    }

    @Override
    public final void handleEvent(Event event) {
        var eventData = event.getProperty(IEventBroker.DATA);
        if (eventData instanceof StatisticEvent e) {
            pushEvent(e);
        } else {
            throw new IllegalArgumentException("Recieved event data must implement " + StatisticEvent.class.getName());
        }
    }

}
