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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.mulders.puml.api.PlantUmlInput;
import it.mulders.puml.api.PlantUmlOptions;
import it.mulders.puml.api.PlantUmlOutput;
import it.mulders.puml.api.PlantUmlOutput.Failure;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import net.sourceforge.plantuml.FileFormatOption;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlantUMLv1ImplTest implements WithAssertions {
    private static final PlantUmlOptions OPTIONS =
            new PlantUmlOptions(PlantUmlOptions.Format.SVG, Paths.get("src", "test", "resources"), null);
    private static final Path EXISTING = Paths.get("src", "test", "resources", "existing.puml");

    private final PlantUMLv1Impl impl = new PlantUMLv1Impl();

    @Test
    void should_return_success_output(@TempDir final Path tempDir) {
        // Arrange
        final PlantUmlInput input = new PlantUmlInput(singletonList(EXISTING), tempDir);

        // Act
        final PlantUmlOutput output = impl.process(input, OPTIONS);

        // Assert
        assertThat(output.isSuccess()).isTrue();
    }

    @Test
    void should_not_fail_with_null_input_files(@TempDir final Path tempDir) {
        // Arrange
        final PlantUmlInput input = new PlantUmlInput(null, tempDir);

        // Act
        final PlantUmlOutput output = impl.process(input, OPTIONS);

        // Assert
        assertThat(output.isSuccess()).isTrue();
    }

    @Test
    void should_not_fail_with_zero_input_files(@TempDir final Path tempDir) {
        // Arrange
        final PlantUmlInput input = new PlantUmlInput(emptyList(), tempDir);

        // Act
        final PlantUmlOutput output = impl.process(input, OPTIONS);

        // Assert
        assertThat(output.isSuccess()).isTrue();
    }

    @Test
    void should_fail_when_output_directory_is_existing_file(@TempDir final Path tempDir) throws IOException {
        // Arrange
        final Path outputDirectory = Files.createFile(tempDir.resolve(Paths.get("puml")));
        final PlantUmlInput input = new PlantUmlInput(singletonList(EXISTING), outputDirectory);

        // Act
        final PlantUmlOutput output = impl.process(input, OPTIONS);

        // Assert
        assertThat(output.isFailure()).isTrue();
    }

    @Test
    void should_create_output_directories_and_file(@TempDir final Path tempDir) {
        // Arrange
        // An @TempDir-annotated param is created automatically, so we use a subdirectory which is not created.
        final Path outputDirectory = tempDir.resolve(Paths.get("puml"));
        final PlantUmlInput input = new PlantUmlInput(singletonList(EXISTING), outputDirectory);

        // Act
        impl.process(input, OPTIONS);

        // Assert
        assertThat(outputDirectory).isDirectory().exists();
        assertThat(outputDirectory.resolve(Paths.get("existing.svg")))
                .isRegularFile()
                .exists();
    }

    @Test
    void should_fail_with_non_existing_file(@TempDir final Path tempDir) {
        // Arrange
        // An @TempDir-annotated param is created automatically, so we use a subdirectory which is not created.
        final Path outputDirectory = tempDir.resolve(Paths.get("puml"));
        final PlantUmlInput input = new PlantUmlInput(singletonList(Paths.get("non-existing.puml")), outputDirectory);

        // Act
        final PlantUmlOutput result = impl.process(input, OPTIONS);

        // Assert
        assertThat(result.isFailure()).isTrue();
        final Failure failure = (Failure) result;
        assertThat(failure.message()).contains("non-existing.puml");
    }

    @Test
    void should_fail_when_output_directory_not_created(@TempDir final Path tempDir) {
        // Arrange
        // An @TempDir-annotated param is created automatically, so we use a subdirectory which is not created.
        final PlantUmlInput input = new PlantUmlInput(singletonList(Paths.get("non-existing.puml")), tempDir);
        final OutputDirector outputDirector = mock(OutputDirector.class);
        when(outputDirector.computeOutputPath(any(), any(), any())).thenReturn(Paths.get("."));
        when(outputDirector.ensureOutputDirectoryExists(any())).thenReturn(Optional.of(new Failure("Alas...")));

        // Act
        final PlantUmlOutput result = new PlantUMLv1Impl(outputDirector).process(input, OPTIONS);

        // Assert
        assertThat(result.isFailure()).isTrue();
        final Failure failure = (Failure) result;
        assertThat(failure.message()).contains("Alas...");
    }

    @EnumSource(PlantUmlOptions.Format.class)
    @ParameterizedTest
    void fileFormatOption(final PlantUmlOptions.Format format) {
        // Act
        final FileFormatOption option = impl.fileFormatOption(new PlantUmlOptions(format, null, null));

        // Assert
        assertThat(option).isNotNull();
    }

    @Test
    void process_diagram_should_write_diagram_to_output_stream() throws IOException {
        // Arrange
        final String input =
                """
                @startuml
                class PlantUMLv1Impl {
                }
                @enduml
                """;
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        // Act
        impl.processDiagram(input, output, OPTIONS);

        // Assert
        assertThat(output.toString())
                .contains("<svg xmlns=\"http://www.w3.org/2000/svg\"")
                .contains("</svg>");
    }

    @Test
    void should_handle_null_pragmas() throws IOException {
        // Arrange
        final String input =
                """
                @startuml
                class PlantUMLv1Impl {
                }
                @enduml
                """;
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PlantUmlOptions options = new PlantUmlOptions(PlantUmlOptions.Format.SVG, null, null);

        // Act
        impl.processDiagram(input, output, options);

        // Assert
        assertThat(output.toString())
                .contains("<svg xmlns=\"http://www.w3.org/2000/svg\"")
                .contains("</svg>");
    }

    @Test
    void should_handle_single_pragma() throws IOException {
        // Arrange
        final String input =
                """
                @startuml
                class PlantUMLv1Impl {
                }
                @enduml
                """;
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PlantUmlOptions options =
                new PlantUmlOptions(PlantUmlOptions.Format.SVG, null, singletonList("layout=smetana"));

        // Act
        impl.processDiagram(input, output, options);

        // Assert
        assertThat(output.toString())
                .contains("<svg xmlns=\"http://www.w3.org/2000/svg\"")
                .contains("</svg>");
    }

    @Test
    void should_convert_pragma_with_equals_sign() {
        // Act
        final String result = impl.mapPragmaToConfig("layout=smetana");

        // Assert
        assertThat(result).isEqualTo("layout smetana");

    }

    @Test
    void should_convert_pragma_without_equals_sign() {
        // Act
        final String result = impl.mapPragmaToConfig("layout smetana");

        // Assert
        assertThat(result).isEqualTo("layout smetana");
    }

    @Test
    void should_convert_empty_pragma() {
        // Act, Assert
        assertThat(impl.mapPragmaToConfig(" ")).isNull();
        assertThat(impl.mapPragmaToConfig("\n")).isNull();
        assertThat(impl.mapPragmaToConfig("")).isNull();
    }

    @Test
    void should_convert_null_pragma() {
        // Act
        final String result = impl.mapPragmaToConfig(null);

        // Assert
        assertThat(result).isNull();
    }
}
