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
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

class PlantUMLv1ImplTest implements WithAssertions {
    private final PlantUmlFacade impl = new PlantUMLv1Impl();

    @Test
    void should_return_success_output(@TempDir final Path tempDir) {
        // Arrange
        final PlantUmlInput input = PlantUmlInput.builder()
                .filesForProcessing(singletonList(Paths.get("example.puml")))
                .outputDirectory(tempDir)
                .build();
        final PlantUmlOptions options = PlantUmlOptions.builder().build();

        // Act
        final PlantUmlOutput output = impl.process(input, options);

        // Assert
        assertThat(output.isSuccess()).isTrue();
    }

    @Test
    void should_not_fail_with_null_input_files(@TempDir final Path tempDir) {
        // Arrange
        final PlantUmlInput input = PlantUmlInput.builder()
                .filesForProcessing(null)
                .outputDirectory(tempDir)
                .build();
        final PlantUmlOptions options = PlantUmlOptions.builder().build();

        // Act
        final PlantUmlOutput output = impl.process(input, options);

        // Assert
        assertThat(output.isSuccess()).isTrue();
    }

    @Test
    void should_not_fail_with_zero_input_files(@TempDir final Path tempDir) {
        // Arrange
        final PlantUmlInput input = PlantUmlInput.builder()
                .filesForProcessing(emptyList())
                .outputDirectory(tempDir)
                .build();
        final PlantUmlOptions options = PlantUmlOptions.builder().build();

        // Act
        final PlantUmlOutput output = impl.process(input, options);

        // Assert
        assertThat(output.isSuccess()).isTrue();
    }

    @Test
    void should_fail_when_output_directory_is_existing_file(@TempDir final Path tempDir) throws IOException {
        // Arrange
        final Path outputDirectory = Files.createFile(tempDir.resolve(Paths.get("puml")));
        final PlantUmlInput input = PlantUmlInput.builder()
                .filesForProcessing(singletonList(Paths.get("example.puml")))
                .outputDirectory(outputDirectory)
                .build();
        final PlantUmlOptions options = PlantUmlOptions.builder().build();

        // Act
        final PlantUmlOutput output = impl.process( input, options );

        // Assert
        assertThat(output.isFailure()).isTrue();
    }

    @Test
    void should_create_output_directory(@TempDir final Path tempDir) {
        // Arrange
        // An @TempDir-annotated param is created automatically, so we use a subdirectory which is not created.
        final Path outputDirectory = tempDir.resolve(Paths.get("puml"));
        final PlantUmlInput input = PlantUmlInput.builder()
                .filesForProcessing(singletonList(Paths.get("example.puml")))
                .outputDirectory(outputDirectory)
                .build();
        final PlantUmlOptions options = PlantUmlOptions.builder().build();

        // Act
        impl.process(input, options);

        // Assert
        assertThat(outputDirectory)
                .isDirectory()
                .exists();
    }
}