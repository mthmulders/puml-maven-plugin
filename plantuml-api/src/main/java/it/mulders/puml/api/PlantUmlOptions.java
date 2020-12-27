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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

/**
 * Options to pass to PlantUML.
 */
@Builder
@Getter
public class PlantUmlOptions {
    @AllArgsConstructor
    @Getter
    public enum Format {
        PNG("png"),
        SVG("svg");

        private final String extension;

        public Path convertFilename(final Path input) {
            final String filename = input.toString().replaceAll("puml$", extension);
            return Paths.get(filename);
        }

        public static Optional<Format> fromExtension(final String input) {
            return Arrays.stream(Format.values())
                    .filter(f -> f.getExtension().equalsIgnoreCase(input))
                    .findAny();
        }
    }

    private final Format format;
    private final Path stripPath;
}
