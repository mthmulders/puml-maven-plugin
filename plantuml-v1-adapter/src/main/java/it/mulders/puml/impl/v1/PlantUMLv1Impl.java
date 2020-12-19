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
import net.sourceforge.plantuml.version.Version;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

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
            return new Success();
        }

        filesForProcessing.forEach(file -> log.info("Processing file {}", file.toAbsolutePath()));

        log.info("Using PlantUML version {}", Version.versionString());

        try {
            if (Files.exists(input.getOutputDirectory())) {
                if (!Files.isDirectory(input.getOutputDirectory())) {
                    return new Failure("Specified output directory exists but is a file rather than a directory");
                }
            } else {
                Files.createDirectories( input.getOutputDirectory() );
            }
        } catch (IOException e) {
            return new Failure(e);
        }

        return new Success();
    }
}
