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
import lombok.extern.slf4j.Slf4j;

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

        filesForProcessing.forEach(file -> {
            log.info("Processing file {}", file.toAbsolutePath());
        });

        return new PlantUmlOutput.Success();
    }
}
