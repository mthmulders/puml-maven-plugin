package it.mulders.puml.plugin;

/*
 * Copyright 2020 Maarten Mulders
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import it.mulders.puml.api.PlantUmlFacade;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Singleton;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * {@inheritDoc}
 */
@Named
@Singleton
@Slf4j
public class ServiceLoaderPlantUmlFactory implements PlantUmlFactory {

    /**
     * {@inheritDoc}.
     *
     * <strong>Implementation note</strong>: This implementation selects the first implementation returned by the
     * {@link ServiceLoader} mechanism. If that mechanism finds more than one implementation, this method will log a
     * warning for each additional implementation.
     */
    public Optional<PlantUmlFacade> findPlantUmlImplementation() {
        final ServiceLoader<PlantUmlFacade> loader = ServiceLoader.load(PlantUmlFacade.class);
        final Iterator<PlantUmlFacade> iterator = loader.iterator();

        return findSuitableImplementation(iterator);
    }

    // Visible for testing
    Optional<PlantUmlFacade> findSuitableImplementation(final Iterator<PlantUmlFacade> implementations) {
        if (!implementations.hasNext()) {
            return Optional.empty();
        }

        final PlantUmlFacade implementation = implementations.next();
        if (implementations.hasNext()) {
            final String location = getLocationDescriptionForImplementation(implementation);
            log.warn("More than one PlantUML adapter found.");
            log.warn("Will continue with the first, detected at {}.", location);
            while (implementations.hasNext()) {
                final PlantUmlFacade alternative = implementations.next();
                final String alternativeLocation = getLocationDescriptionForImplementation(alternative);
                log.warn("Alternative PlantUML adapter found in {}.", alternativeLocation);
            }
        }

        return Optional.of(implementation);
    }

    private String getLocationDescriptionForImplementation(final PlantUmlFacade alternative) {
        final Class<?> clazz = alternative.getClass();
        final ProtectionDomain domain = clazz.getProtectionDomain();
        final CodeSource source = domain.getCodeSource();
        return source.getLocation().toString();
    }
}
