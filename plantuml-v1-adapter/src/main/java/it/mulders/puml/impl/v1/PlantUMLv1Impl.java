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

import it.mulders.puml.api.PlantUmlFacade;
import it.mulders.puml.api.PlantUmlInput;
import it.mulders.puml.api.PlantUmlOptions;
import it.mulders.puml.api.PlantUmlOutput;
import it.mulders.puml.api.PlantUmlOutput.Failure;
import it.mulders.puml.api.PlantUmlOutput.Success;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.version.Version;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Implementation of the {@link PlantUmlFacade} based on PlantUML v1.x.y.
 */
@Slf4j
public class PlantUMLv1Impl implements PlantUmlFacade {
    /**
     * {@inheritDoc}
     */
    @Override
    public PlantUmlOutput process(final PlantUmlInput input, final PlantUmlOptions options) {
        final Collection<Path> filesForProcessing = input.getFilesForProcessing();

        if (filesForProcessing == null || filesForProcessing.isEmpty()) {
            log.info("No input files to process");
            return new Success(0);
        }

        log.info("Using PlantUML version {}", Version.versionString());

        final Path outputDirectory = input.getOutputDirectory();
        final Map<Path, Path> inputToOutputMapping = filesForProcessing.stream()
                .collect(toMap(identity(), path -> computeOutputPath( path, outputDirectory, options)));

        return inputToOutputMapping.values().stream()
                // Validate the output directory for each file that we must process
                .map(outputPath -> ensureOutputDirectoryExists(outputPath.getParent()))
                // Filter items where the output directory could not be created
                .filter(Optional::isPresent)
                // Get the first failure
                .map(Optional::get)
                .findAny()
                // ... otherwise, start processing files.
                .orElseGet(() ->
                    inputToOutputMapping.entrySet().stream()
                            .map(entry ->  processDiagram(entry.getKey(), entry.getValue(), options))
                            .collect(new ItemOutputCollector())
                );
    }

    // Visible for testing
    Path computeOutputPath(final Path inputPath, final Path outputDirectory, final PlantUmlOptions options) {
        final Path workingDirectory = Paths.get("").toAbsolutePath();
        final Path stripPath = options.getStripPath();
        final Path relativeInputPath = workingDirectory.resolve(stripPath).relativize(inputPath.toAbsolutePath());
        final Path relativeOutputPath = options.getFormat().convertFilename(relativeInputPath);

        final Path outputPath = outputDirectory.resolve(relativeOutputPath);
        log.debug("Computed output path {} for input {}", outputPath, inputPath);
        return outputPath;
    }

    // Visible for testing
    Optional<PlantUmlOutput> ensureOutputDirectoryExists(final Path outputDirectory) {
        try {
            Files.createDirectories(outputDirectory);
        } catch (FileAlreadyExistsException faee) {
            return Optional.of(new Failure("Specified output directory exists but is a file, not a directory"));
        } catch (IOException e) {
            return Optional.of(new Failure(e));
        }
        return Optional.empty();
    }

    private ItemOutput processDiagram(final Path inputPath, final Path outputPath, final PlantUmlOptions options) {
        log.info("Processing file {}", inputPath.toAbsolutePath());
        final ItemOutput.ItemOutputBuilder result = ItemOutput.builder().input(inputPath);

        final String input;

        try {
            log.debug("Reading input from {}", inputPath.toString());
            input = new String(Files.readAllBytes(inputPath));
        } catch (IOException e) {
            log.error("Could not read input file {}", inputPath.toString(), e);
            return result.success(false).build();
        }

        try (final OutputStream fos = new FileOutputStream(outputPath.toFile());
             final OutputStream bos = new BufferedOutputStream(fos)) {
            log.debug("Processing diagram");
            return processDiagram(input, bos, options);
        } catch (IOException e) {
            log.error( "Could not write to output file {}", outputPath.toString(), e);
            return result.success(false).build();
        }
    }

    ItemOutput processDiagram(final String input, final OutputStream output, final PlantUmlOptions options) throws IOException {
        final ItemOutput.ItemOutputBuilder result = ItemOutput.builder();
        final SourceStringReader reader = new SourceStringReader(input);

        reader.outputImage(output, fileFormatOption(options));

        return result.success(true).build();
    }

    FileFormatOption fileFormatOption(final PlantUmlOptions options) {
        switch (options.getFormat()) {
            case SVG:
                return new FileFormatOption(FileFormat.SVG, false);
            default:
                log.error("This adapter does not support format {}", options.getFormat());
                throw new IllegalArgumentException("Format " + options.getFormat() + " not supported");
        }
    }
}
