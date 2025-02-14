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

import it.mulders.puml.api.PlantUmlOptions;
import it.mulders.puml.api.PlantUmlOutput.Failure;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class OutputDirectorTest implements WithAssertions {
    private final OutputDirector director = new OutputDirector();

    @DisplayName("computeOutputPath")
    @Nested
    class ComputeOutputPath {
        @Test
        void should_strip_paths() {
            // Arrange
            final Path inputFile = Paths.get("src", "test", "resources", "example", "input.puml");
            final Path outputDirectory = Paths.get("target");
            final PlantUmlOptions options =
                    new PlantUmlOptions(PlantUmlOptions.Format.SVG, Paths.get("src", "test", "resources"));

            // Act
            final Path result = director.computeOutputPath(inputFile, outputDirectory, options);

            // Assert
            assertThat(result).isEqualTo(Paths.get("target", "example", "input.svg"));
        }

        @Test
        void should_work_with_all_extensions() {
            // Arrange
            final Path inputFile = Paths.get("target", "generated-docs", "class-diagram.plantuml");
            final Path outputDirectory = Paths.get("target", "site");
            final PlantUmlOptions options =
                    new PlantUmlOptions(PlantUmlOptions.Format.PNG, Paths.get("target", "generated-docs"));

            // Act
            final Path result = director.computeOutputPath(inputFile, outputDirectory, options);

            // Assert
            assertThat(result).isEqualTo(Paths.get("target", "site", "class-diagram.png"));
        }
    }

    @DisplayName("ensureOutputDirectoryExists")
    @Nested
    class EnsureOutputDirectoryExists {
        @Test
        void existing_directory(@TempDir final Path tempDir) {
            // Arrange

            // Act
            final Optional<Failure> output = director.ensureOutputDirectoryExists(tempDir);

            // Assert
            assertThat(output).isNotPresent();
            assertThat(tempDir).exists();
            assertThat(tempDir).isDirectory();
        }

        @Test
        void fails_on_existing_file(@TempDir final Path tempDir) throws IOException {
            // Arrange
            // An @TempDir-annotated param is created automatically, so we use a subdirectory which is not created.
            final Path outputDirectoryThatIsFile = tempDir.resolve(Paths.get("puml"));
            Files.createFile(outputDirectoryThatIsFile);

            // Act
            final Optional<Failure> output = director.ensureOutputDirectoryExists(outputDirectoryThatIsFile);

            // Assert
            assertThat(output).isPresent().hasValueSatisfying(result -> assertThat(result)
                    .isInstanceOf(Failure.class));
            assertThat(outputDirectoryThatIsFile).exists();
            assertThat(outputDirectoryThatIsFile).isRegularFile();
        }

        @Test
        void non_existing_directory(@TempDir final Path tempDir) {
            // Arrange
            // An @TempDir-annotated param is created automatically, so we use a subdirectory which is not created.
            final Path outputDirectory = tempDir.resolve(Paths.get("puml"));

            // Act
            final Optional<Failure> output = director.ensureOutputDirectoryExists(outputDirectory);

            // Assert
            assertThat(output).isNotPresent();
            assertThat(outputDirectory).exists();
            assertThat(outputDirectory).isDirectory();
        }
    }
}
