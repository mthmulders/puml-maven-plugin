package it.mulders.puml.plugin;

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
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

@Mojo(name = "generate")
@Execute
public class GenerateMojo extends AbstractMojo {
    private static final Logger logger = LoggerFactory.getLogger( GenerateMojo.class );

    private final InputFileLocator inputFileLocator;
    private final PlantUmlFactory plantUmlFactory;

    @Parameter(required=true, property="plantuml.sourceFiles")
    private FileSet sourceFiles;

    @Inject
    public GenerateMojo(final InputFileLocator inputFileLocator, final PlantUmlFactory plantUmlFactory) {
        this.inputFileLocator = inputFileLocator;
        this.plantUmlFactory = plantUmlFactory;
    }

    @Override
    public void execute() throws MojoExecutionException {
        verifyParameters();

        final Collection<Path> filesForProcessing = inputFileLocator.determineFilesForProcessing(this.sourceFiles);

        filesForProcessing.forEach(file -> {
            logger.info("Processing file {}", file.toAbsolutePath().toString());
        });

        findPlantUmlFacade();
    }

    private void verifyParameters() throws MojoExecutionException {
        final Path directory = Paths.get(this.sourceFiles.getDirectory());

        if (!Files.isDirectory(directory)) {
            logger.warn("Specified source directory is not a directory");
            return;
        }

        try {
            if (Files.list(directory).count() == 0) {
                logger.warn("Specified source directory is empty");
                return;
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Could not scan source directory for files", e);
        }
    }

    private PlantUmlFacade findPlantUmlFacade() throws MojoExecutionException {
        return plantUmlFactory.findPlantUmlImplementation()
                .orElseThrow(() -> new MojoExecutionException("No PlantUML adapter found. Add one to your classpath. This plugin will not work without it."));
    }

    public void setSourceFiles(final FileSet sourceFiles) {
        this.sourceFiles = sourceFiles;
    }
}