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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Options to pass to PlantUML.
 */
public record PlantUmlOptions(Format format, Path stripPath, List<String> pragmas) {

    public enum Format {
        PNG("png"),
        SVG("svg");

        public final String extension;

        Format(String extension) {
            this.extension = extension;
        }

        public Path convertFilename(final Path input) {
            final int lastDot = input.toString().lastIndexOf('.');
            final String baseName = input.toString().substring(0, lastDot);
            final String filename = String.format("%s.%s", baseName, extension);
            return Paths.get(filename);
        }

        public static Optional<Format> fromExtension(final String input) {
            return Arrays.stream(Format.values())
                    .filter(f -> f.extension.equalsIgnoreCase(input))
                    .findAny();
        }
    }
}
