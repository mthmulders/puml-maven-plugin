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

import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@DisplayNameGeneration( DisplayNameGenerator.ReplaceUnderscores.class)
class DefaultInputFileLocatorTest implements WithAssertions {
    private static final Path BASE_DIRECTORY = Paths.get("src", "test", "resources", "locating").toAbsolutePath();

    private final InputFileLocator locator = new DefaultInputFileLocator();

    @Test
    void should_find_all_files_in_directory() throws MojoExecutionException {
        // Arrange
        final FileSet fileSet = new FileSetBuilder()
                .baseDirectory(BASE_DIRECTORY)
                .includes(singletonList("*"))
                .build();

        // Act
        final Collection<Path> result = locator.determineFilesForProcessing(fileSet);

        // Assert
        final List<String> paths = result.stream()
                .map(BASE_DIRECTORY::relativize)
                .map(Path::toString)
                .collect(toList());
        assertThat(paths).containsOnly("a.txt", "b.txt", "c.dot");
    }
    
    @Test
    void should_apply_exclude_patterns_in_directory() throws MojoExecutionException {
        // Arrange
        final FileSet fileSet = new FileSetBuilder()
                .baseDirectory(BASE_DIRECTORY)
                .includes(singletonList("*"))
                .excludes(singletonList("*.dot"))
                .build();

        // Act
        final Collection<Path> result = locator.determineFilesForProcessing(fileSet);

        // Assert
        final List<String> paths = result.stream()
                .map(BASE_DIRECTORY::relativize)
                .map(Path::toString)
                .collect(toList());
        assertThat(paths).containsOnly("a.txt", "b.txt");
    }
}