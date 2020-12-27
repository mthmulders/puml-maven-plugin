package it.mulders.puml.api;

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

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

class PlantUmlInputTest implements WithAssertions {
    private static final Collection<Path> filesForProcessing = Arrays.asList(
            Paths.get("file1.puml"),
            Paths.get("file2.puml")
    );
    private static final Path outputDirectory = Paths.get(".");

    private final PlantUmlInput result = PlantUmlInput.builder()
            .filesForProcessing(filesForProcessing)
            .outputDirectory(outputDirectory)
            .build();

    @Test
    void filesForProcessing() {
        assertThat(result.getFilesForProcessing()).isEqualTo(filesForProcessing);
    }

    @Test
    void outputDirectory() {
        assertThat(result.getOutputDirectory()).isEqualTo(outputDirectory);
    }
}