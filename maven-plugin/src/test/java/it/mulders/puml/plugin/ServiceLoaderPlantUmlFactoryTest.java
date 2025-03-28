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

import com.github.valfirst.slf4jtest.LoggingEvent;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.github.valfirst.slf4jtest.TestLoggerFactoryExtension;
import it.mulders.puml.api.PlantUmlFacade;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.event.Level;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(TestLoggerFactoryExtension.class)
class ServiceLoaderPlantUmlFactoryTest implements WithAssertions {
    private final TestLogger logger = TestLoggerFactory.getTestLogger(ServiceLoaderPlantUmlFactory.class);
    private final ServiceLoaderPlantUmlFactory factory = new ServiceLoaderPlantUmlFactory();

    @Test
    void should_locate_PlantUML_implementation() {
        Optional<PlantUmlFacade> result = factory.findPlantUmlImplementation();

        assertThat(result).isPresent();
    }

    @Test
    void should_return_empty_optional_when_no_implementation_found() {
        Iterator<PlantUmlFacade> implementations = Collections.emptyIterator();
        Optional<PlantUmlFacade> result = factory.findSuitableImplementation(implementations);
        assertThat(result).isEmpty();
    }

    @Test
    void should_warn_when_more_than_one_implementation_found() {
        Iterator<PlantUmlFacade> implementations = Arrays.asList(impl1, impl2).iterator();
        factory.findSuitableImplementation(implementations);
        long warningCount = logger.getLoggingEvents().stream()
                .filter(e -> e.getLevel() == Level.WARN)
                .count();
        assertThat(warningCount).isEqualTo(3);
    }

    @Test
    void should_return_first_implementation_when_more_than_one_implementation_found() {
        Iterator<PlantUmlFacade> implementations = Arrays.asList(impl1, impl2).iterator();
        Optional<PlantUmlFacade> result = factory.findSuitableImplementation(implementations);
        assertThat(result).isPresent().hasValue(impl1);
    }

    @Test
    void should_log_location_of_alternative_implementation() {
        Iterator<PlantUmlFacade> implementations = Arrays.asList(impl1, impl2).iterator();
        factory.findSuitableImplementation(implementations);
        Optional<LoggingEvent> result = logger.getLoggingEvents().stream()
                .filter(e -> e.getMessage().contains("adapter found in"))
                .findFirst();
        assertThat(result).isPresent().hasValueSatisfying(event -> assertThat(event.getArguments())
                .hasSize(1)
                .satisfies(arg -> assertThat(arg.toString()).contains("test-classes")));
    }

    private final PlantUmlFacade impl1 = (input, options) -> null;
    private final PlantUmlFacade impl2 = (input, options) -> null;
}
