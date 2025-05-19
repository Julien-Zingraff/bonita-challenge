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

import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.time.Duration;
import java.util.function.Supplier;

import org.eclipse.e4.core.di.annotations.Creatable;

import jakarta.inject.Singleton;

@Creatable
@Singleton
public class StatisticsHttpClientBuilder implements Supplier<HttpClient> {

    private HttpClient httpClient;

    @Override
    public HttpClient get() {
        if (httpClient == null) {
            httpClient = createHttpClient();
        }
        return httpClient;
    }

    protected HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .followRedirects(Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

}
