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
import it.mulders.puml.api.PlantUmlInput;
import it.mulders.puml.api.PlantUmlOptions;
import it.mulders.puml.api.PlantUmlOutput;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GenerateMojoTest implements WithAssertions {
    private final InputFileLocator inputFileLocator = mock(InputFileLocator.class);
    private final PlantUmlFacade plantUml = mock(PlantUmlFacade.class);
    private final PlantUmlFactory plantUmlFactory = mock(PlantUmlFactory.class);

    private final GenerateMojo mojo = new GenerateMojo(inputFileLocator, plantUmlFactory);

    @BeforeEach
    void mock_PlantUML_implementation() {
        when(plantUmlFactory.findPlantUmlImplementation()).thenReturn(Optional.of(plantUml));
        when(plantUml.process(any(), any())).thenReturn(new PlantUmlOutput.Success(0));
    }

    @Test
    void should_fail_without_PlantUMLFactory_implementation() {
        // Arrange
        final FileSet fileSet = new FileSetBuilder().baseDirectory(Paths.get(".")).build();
        mojo.setSourceFiles(fileSet);
        mojo.setFormat("svg");
        when(plantUmlFactory.findPlantUmlImplementation()).thenReturn(Optional.empty());

        // Act
        assertThatThrownBy(mojo::execute)

        // Assert
            .isInstanceOf( MojoExecutionException.class)
            .hasMessageContaining("No PlantUML adapter found");
    }

    @Test
    void should_invoke_PlantUMLFacade() throws MojoExecutionException {
        // Arrange
        final FileSet fileSet = new FileSetBuilder().baseDirectory(Paths.get(".")).build();
        mojo.setOutputDirectory(new File("target"));
        mojo.setSourceFiles(fileSet);
        mojo.setFormat("svg");
        mojo.setStripPath(new File("."));

        // Act
        mojo.execute();

        // Assert
        verify(plantUml).process(any(), any());
    }

    @Test
    void should_pass_files_to_process_to_PlantUML() throws MojoExecutionException {
        // Arrange
        final Collection<Path> files = singletonList(Paths.get( "./example.puml"));
        when(inputFileLocator.determineFilesForProcessing(any())).thenReturn(files);
        mojo.setSourceFiles(new FileSetBuilder().baseDirectory(Paths.get(".")).build());
        mojo.setOutputDirectory(new File("target"));
        mojo.setFormat("svg");
        mojo.setStripPath(new File("."));

        // Act
        mojo.execute();

        // Assert
        final ArgumentCaptor<PlantUmlInput> inputCaptor = ArgumentCaptor.forClass(PlantUmlInput.class);
        verify(plantUml).process(inputCaptor.capture(), any());

        final PlantUmlInput input = inputCaptor.getValue();
        assertThat(input.filesForProcessing()).isEqualTo(files);
    }

    @Test
    void should_pass_output_directory_to_PlantUML() throws MojoExecutionException {
        // Arrange
        final File outputDirectory = new File("target");
        mojo.setOutputDirectory(outputDirectory);
        mojo.setSourceFiles(new FileSetBuilder().baseDirectory(Paths.get(".")).build());
        mojo.setFormat("svg");
        mojo.setStripPath(new File("."));

        // Act
        mojo.execute();

        // Assert
        final ArgumentCaptor<PlantUmlInput> inputCaptor = ArgumentCaptor.forClass(PlantUmlInput.class);
        verify(plantUml).process(inputCaptor.capture(), any());

        final PlantUmlInput input = inputCaptor.getValue();
        assertThat(input.outputDirectory().toAbsolutePath()).isEqualTo(Paths.get(outputDirectory.toURI()));
    }

    @Test
    void should_not_invoke_PlantUML_when_source_directory_is_invalid() throws MojoExecutionException {
        // Arrange
        final FileSet fileSet = new FileSetBuilder()
                .baseDirectory(Paths.get("non-existing"))
                .build();
        mojo.setSourceFiles(fileSet);
        mojo.setOutputDirectory(new File("target"));
        mojo.setFormat("svg");

        // Act
        mojo.execute();

        // Assert
        verify(plantUml, never()).process(any(), any());
    }

    @Test
    void should_not_invoke_PlantUML_when_outputFormat_is_invalid() throws MojoExecutionException {
        // Arrange
        final FileSet fileSet = new FileSetBuilder().baseDirectory(Paths.get(".")).build();
        mojo.setSourceFiles(fileSet);
        mojo.setOutputDirectory(new File("target"));
        mojo.setFormat("foo");

        // Act
        mojo.execute();

        // Assert
        verify(plantUml, never()).process(any(), any());
    }

    @Test
    void should_pass_pragmas_to_PlantUML() throws MojoExecutionException {
        // Arrange
        final List<String> pragmas = List.of("pragma1", "pragma2");
        mojo.setPragmas(pragmas);
        mojo.setSourceFiles(new FileSetBuilder().baseDirectory(Paths.get(".")).build());
        mojo.setOutputDirectory(new File("target"));
        mojo.setFormat("svg");
        mojo.setStripPath(new File("."));

        // Act
        mojo.execute();

        // Assert
        final ArgumentCaptor<PlantUmlOptions> optionsCaptor = ArgumentCaptor.forClass(PlantUmlOptions.class);
        verify(plantUml).process(any(), optionsCaptor.capture());

        final PlantUmlOptions options = optionsCaptor.getValue();
        assertThat(options.pragmas()).isEqualTo(pragmas);
    }
}
