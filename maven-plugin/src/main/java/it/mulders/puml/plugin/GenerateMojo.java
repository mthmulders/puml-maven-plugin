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
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Mojo(name = "generate")
public class GenerateMojo extends AbstractMojo {
    private final PlantUmlFactory factory;

    @Parameter(required=true, property="plantuml.sourceFiles")
    private FileSet sourceFiles;

    @Inject
    public GenerateMojo(final PlantUmlFactory factory) {
        this.factory = factory;
    }

    @Override
    public void execute() throws MojoExecutionException {
        verifyParameters();

        final List<Path> filesForProcessing = determineFilesForProcessing();

        filesForProcessing.forEach(file -> {
            getLog().info("Processing file " + file.toAbsolutePath().toString());
        });

        findPlantUmlFacade();
    }

    private void verifyParameters() throws MojoExecutionException {
        final Path directory = Paths.get(this.sourceFiles.getDirectory());

        if (!Files.isDirectory(directory)) {
            getLog().warn("Specified source directory is not a directory");
            return;
        }

        try {
            if (Files.list(directory).count() == 0) {
                getLog().warn("Specified source directory is empty");
                return;
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Could not scan source directory for files", e);
        }
    }

    private List<Path> determineFilesForProcessing() throws MojoExecutionException {
        final File basedir = new File(this.sourceFiles.getDirectory());
        final String includes = String.join(",", this.sourceFiles.getIncludes());
        final String excludes = String.join(",", this.sourceFiles.getExcludes());

        try {
            final List<File> files = FileUtils.getFiles(basedir, includes, excludes);
            return files.stream()
                    .map(File::toURI)
                    .map(Paths::get)
                    .collect(toList());
        } catch (IOException e) {
            throw new MojoExecutionException("Could not determine files to process", e);
        }
    }

    private PlantUmlFacade findPlantUmlFacade() throws MojoExecutionException {
        return factory.findPlantUmlImplementation()
                .orElseThrow(() -> new MojoExecutionException("No PlantUML adapter found. Add one to your classpath. This plugin will not work without it."));
    }

    public void setSourceFiles(final FileSet sourceFiles) {
        this.sourceFiles = sourceFiles;
    }
}