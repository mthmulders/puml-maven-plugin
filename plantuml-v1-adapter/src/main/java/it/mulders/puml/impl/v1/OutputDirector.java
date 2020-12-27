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
import it.mulders.puml.api.PlantUmlOutput;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Class for working with output directories and folders.
 */
@Slf4j
public class OutputDirector {
    /**
     * Ensures a folder for placing the generated diagrams exists.
     * @param outputDirectory The desired folder.
     * @return An {@link PlantUmlOutput.Failure} when the folder couldn't be created.
     */
    public Optional<PlantUmlOutput.Failure> ensureOutputDirectoryExists(final Path outputDirectory) {
        try {
            Files.createDirectories(outputDirectory);
        } catch (FileAlreadyExistsException faee) {
            return Optional.of(new PlantUmlOutput.Failure("Specified output directory exists but is a file, not a directory"));
        } catch (IOException e) {
            return Optional.of(new PlantUmlOutput.Failure(e));
        }
        return Optional.empty();
    }


    // Visible for testing
    public Path computeOutputPath(final Path inputPath, final Path outputDirectory, final PlantUmlOptions options) {
        final Path workingDirectory = Paths.get("").toAbsolutePath();
        final Path stripPath = options.getStripPath();
        final Path relativeInputPath = workingDirectory.resolve(stripPath).relativize(inputPath.toAbsolutePath());
        final Path relativeOutputPath = options.getFormat().convertFilename(relativeInputPath);

        final Path outputPath = outputDirectory.resolve(relativeOutputPath);
        log.debug("Computed output path {} for input {}", outputPath, inputPath);
        return outputPath;
    }

}
