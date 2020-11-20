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
import java.util.Collection;
import java.util.Collections;

/**
 * Specifies the input that PlantUML should process.
 */
public class PlantUmlInput {
    private final Collection<Path> filesForProcessing;

    private PlantUmlInput(final Collection<Path> filesForProcessing) {
        this.filesForProcessing = filesForProcessing;
    }

    public Collection<Path> getFilesForProcessing() {
        return Collections.unmodifiableCollection(this.filesForProcessing);
    }

    public static PlantUmlInputBuilder builder() {
        return new PlantUmlInputBuilder();
    }

    public static class PlantUmlInputBuilder {
        private Collection<Path> filesForProcessing = Collections.emptyList();

        public PlantUmlInputBuilder filesForProcessing(final Collection<Path> filesForProcessing) {
            this.filesForProcessing = filesForProcessing;
            return this;
        }

        public PlantUmlInput build() {
            return new PlantUmlInput(
                    this.filesForProcessing
            );
        }
    }
}
