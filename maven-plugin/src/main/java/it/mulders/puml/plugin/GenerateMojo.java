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
import it.mulders.puml.api.PlantUmlInput;
import it.mulders.puml.api.PlantUmlOptions;
import it.mulders.puml.api.PlantUmlOutput;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

@Mojo(name = "generate")
@Execute
@RequiredArgsConstructor( onConstructor = @__( { @Inject }))
@Setter
@Slf4j
public class GenerateMojo extends AbstractMojo {
    private final InputFileLocator inputFileLocator;
    private final PlantUmlFactory plantUmlFactory;

    @Parameter(required=true, property="plantuml.sourceFiles")
    private FileSet sourceFiles;

    @Override
    public void execute() throws MojoExecutionException {
        if (!verifyParameters()) {
            return;
        }

        final Collection<Path> filesForProcessing = inputFileLocator.determineFilesForProcessing(this.sourceFiles);

        final PlantUmlFacade plantUml = findPlantUmlFacade();

        final PlantUmlInput input = PlantUmlInput.builder()
                .filesForProcessing(filesForProcessing)
                .build();
        final PlantUmlOptions options = PlantUmlOptions.builder()
                .build();

        final PlantUmlOutput output = plantUml.process(input, options);
        if (output.isFailure()) {
            throw new MojoExecutionException(((PlantUmlOutput.Failure) output).getMessage());
        }
    }

    private boolean verifyParameters() throws MojoExecutionException {
        final Path directory = Paths.get(this.sourceFiles.getDirectory());

        if (!Files.isDirectory(directory)) {
            log.warn("Specified source directory is not a directory");
            return false;
        }

        return true;
    }

    private PlantUmlFacade findPlantUmlFacade() throws MojoExecutionException {
        return plantUmlFactory.findPlantUmlImplementation()
                .orElseThrow(() -> new MojoExecutionException("No PlantUML adapter found. Add one to your classpath. This plugin will not work without it."));
    }
}