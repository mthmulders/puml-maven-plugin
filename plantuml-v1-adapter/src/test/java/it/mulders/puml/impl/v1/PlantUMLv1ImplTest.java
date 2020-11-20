package it.mulders.puml.impl.v1;

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
import it.mulders.puml.api.PlantUmlInput;
import it.mulders.puml.api.PlantUmlOptions;
import it.mulders.puml.api.PlantUmlOutput;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class PlantUMLv1ImplTest implements WithAssertions {
    private final PlantUmlFacade impl = new PlantUMLv1Impl();

    @Test
    void should_return_success_output() {
        // Arrange
        final PlantUmlInput input = PlantUmlInput.builder().build();
        final PlantUmlOptions options = PlantUmlOptions.builder().build();

        // Act
        final PlantUmlOutput output = impl.process(input, options);


        // Assert
        assertThat(output.isSuccess()).isTrue();

    }
}