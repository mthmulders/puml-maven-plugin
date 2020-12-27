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

import it.mulders.puml.api.PlantUmlOptions.Format;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.file.Path;
import java.nio.file.Paths;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlantUmlOptionsTest implements WithAssertions {
    @EnumSource( Format.class)
    @ParameterizedTest
    void convert_filename(final Format format) {
        // Act
        final Path result = format.convertFilename(Paths.get("example.puml"));

        // Assert
        assertThat(result.toString()).endsWith(format.getExtension());
    }

    private static final Format format = Format.SVG;
    private static final Path stripPath = Paths.get("src/main/docs");

    private final PlantUmlOptions result = PlantUmlOptions.builder()
            .format(format)
            .stripPath(stripPath)
            .build();

    @Test
    void format() {
        assertThat(result.getFormat()).isEqualTo(format);
    }

    @Test
    void stripPath() {
        assertThat(result.getStripPath()).isEqualTo(stripPath);
    }
}