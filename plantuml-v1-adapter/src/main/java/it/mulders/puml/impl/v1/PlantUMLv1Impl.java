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

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import it.mulders.puml.api.PlantUmlFacade;
import it.mulders.puml.api.PlantUmlInput;
import it.mulders.puml.api.PlantUmlOptions;
import it.mulders.puml.api.PlantUmlOutput;
import it.mulders.puml.api.PlantUmlOutput.Success;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link PlantUmlFacade} based on PlantUML v1.x.y.
 */
public class PlantUMLv1Impl implements PlantUmlFacade {
    private static final Logger log = LoggerFactory.getLogger(PlantUMLv1Impl.class);
    private static final String HEADLESS = "java.awt.headless";

    private final OutputDirector outputDirector;

    public PlantUMLv1Impl() {
        this(new OutputDirector());
    }

    PlantUMLv1Impl(final OutputDirector outputDirector) {
        this.outputDirector = outputDirector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlantUmlOutput process(final PlantUmlInput input, final PlantUmlOptions options) {
        final Collection<Path> filesForProcessing = input.filesForProcessing();

        if (filesForProcessing == null || filesForProcessing.isEmpty()) {
            log.info("No input files to process");
            return new Success(0);
        }

        log.info("Using PlantUML version {}", Version.versionString());

        final Path outputDirectory = input.outputDirectory();
        final Map<Path, Path> inputToOutputMapping = filesForProcessing.stream()
                .collect(toMap(identity(), path -> outputDirector.computeOutputPath(path, outputDirectory, options)));

        return inputToOutputMapping.values().stream()
                // Validate the output directory for each file that we must process
                .map(outputPath -> outputDirector.ensureOutputDirectoryExists(outputPath.getParent()))
                // Filter items where the output directory could not be created
                .filter(Optional::isPresent)
                // Get the first failure
                .map(Optional::get)
                .map(PlantUmlOutput.class::cast)
                .findAny()
                // ... otherwise, start processing files.
                .orElseGet(() -> inputToOutputMapping.entrySet().stream()
                        .map(entry -> processDiagram(entry.getKey(), entry.getValue(), options))
                        .collect(new ItemOutputCollector()));
    }

    private ItemOutput processDiagram(final Path inputPath, final Path outputPath, final PlantUmlOptions options) {
        log.info("Processing file {}", inputPath.toAbsolutePath());
        final ItemOutput.ItemOutputBuilder result = ItemOutput.builder().input(inputPath);

        final String input;

        try {
            log.debug("Reading input from {}", inputPath);
            input = new String(Files.readAllBytes(inputPath));
        } catch (IOException e) {
            log.error("Could not read input file {}", inputPath, e);
            return result.success(false).build();
        }

        try (final OutputStream fos = new FileOutputStream(outputPath.toFile());
                final OutputStream bos = new BufferedOutputStream(fos)) {
            log.debug("Processing diagram");
            return processDiagram(input, bos, options);
        } catch (IOException e) {
            log.error("Could not write to output file {}", outputPath, e);
            return result.success(false).build();
        }
    }

    ItemOutput processDiagram(final String input, final OutputStream output, final PlantUmlOptions options)
            throws IOException {
        final ItemOutput.ItemOutputBuilder result = ItemOutput.builder();
        final List<String> config = mapPragmasToConfig(options.pragmas());
        final SourceStringReader reader = new SourceStringReader(Defines.createEmpty(), input, config);

        final String headless = System.getProperty(HEADLESS);

        try {
            System.setProperty(HEADLESS, "true");
            reader.outputImage(output, fileFormatOption(options));
        } finally {
            // Restore old value
            if (headless != null) {
                System.setProperty(HEADLESS, headless);
            }
        }

        return result.success(true).build();
    }

    List<String> mapPragmasToConfig(final List<String> pragmas) {
        if (pragmas == null || pragmas.isEmpty()) {
            return Collections.emptyList();
        }
        return pragmas.stream()
                .map(this::mapPragmaToConfig)
                .filter(Objects::nonNull)
                .map(p -> "!pragma " + p)
                .toList();
    }

    String mapPragmaToConfig(final String pragma) {
        if (pragma == null || pragma.isEmpty() || pragma.trim().isEmpty()) {
            return null;
        }
        if (pragma.contains("=")) {
            final String part1 = pragma.substring(0, pragma.indexOf("="));
            final String part2 = pragma.substring(pragma.indexOf("=") + 1);
            return part1 + " " + part2;
        }
        return pragma;
    }

    FileFormatOption fileFormatOption(final PlantUmlOptions options) {
        return switch (options.format()) {
            case PNG -> new FileFormatOption(FileFormat.PNG, false);
            case SVG -> new FileFormatOption(FileFormat.SVG, false);
        };
    }
}
