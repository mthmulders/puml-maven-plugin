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

import static java.util.stream.Collectors.joining;

import it.mulders.puml.api.PlantUmlFacade;
import it.mulders.puml.api.PlantUmlInput;
import it.mulders.puml.api.PlantUmlOptions;
import it.mulders.puml.api.PlantUmlOptions.Format;
import it.mulders.puml.api.PlantUmlOutput;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates diagrams from a set of input files.
 */
@Mojo(name = "generate")
@Execute
public class GenerateMojo extends AbstractMojo {
    private static final Logger log = LoggerFactory.getLogger(GenerateMojo.class);

    private final InputFileLocator inputFileLocator;
    private final PlantUmlFactory plantUmlFactory;

    @Inject
    public GenerateMojo(final InputFileLocator inputFileLocator, final PlantUmlFactory plantUmlFactory) {
        this.inputFileLocator = inputFileLocator;
        this.plantUmlFactory = plantUmlFactory;
    }

    /**
     * Specify which files to process.
     */
    @Parameter(required = true, property = "plantuml.sourceFiles")
    private FileSet sourceFiles;

    // Would be great if we could use java.nio.file.Path here.
    // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=563526
    // and https://bugs.eclipse.org/bugs/show_bug.cgi?id=563525.
    /**
     * Specify where to place generated diagrams.
     */
    @Parameter(
            required = true,
            property = "plantuml.outputDirectory",
            defaultValue = "${project.build.directory}/plantuml")
    private File outputDirectory;

    // Would be great if we could use java.nio.file.Path here.
    // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=563526
    // and https://bugs.eclipse.org/bugs/show_bug.cgi?id=563525.
    /**
     * Specifies a directory to strip from each file in {@link #sourceFiles} before building the output file name.
     */
    @Parameter(required = true, property = "plantuml.stripPath")
    private File stripPath;

    /**
     * Specify which format the diagrams should be generated in. Options:
     * <ul>
     *     <li>SVG</li>
     * </ul>
     */
    @Parameter(required = true, property = "plantuml.format")
    private String format;

    /**
     * Specify pragmas to be passed to PlantUML.
     */
    @Parameter(property = "plantuml.pragmas")
    private List<String> pragmas;

    @Override
    public void execute() throws MojoExecutionException {
        if (!verifyParameters()) {
            return;
        }

        final Collection<Path> filesForProcessing = inputFileLocator.determineFilesForProcessing(this.sourceFiles);
        if (filesForProcessing.isEmpty()) {
            log.info("No files to process.");
            log.info("In directory {}, no files match includes {} and excludes {}",
                    this.sourceFiles.getDirectory(), this.sourceFiles.getIncludes(), this.sourceFiles.getExcludes());
            return;
        }

        final PlantUmlFacade plantUml = findPlantUmlFacade();

        final PlantUmlInput input = new PlantUmlInput(filesForProcessing, Paths.get(outputDirectory.toURI()));
        final PlantUmlOptions options =
                new PlantUmlOptions(determineOutputFormat(), Paths.get(stripPath.toURI()), pragmas);

        final PlantUmlOutput output = plantUml.process(input, options);
        if (output.isFailure()) {
            throw new MojoExecutionException(((PlantUmlOutput.Failure) output).message());
        }
    }

    private Format determineOutputFormat() throws MojoExecutionException {
        return Format.fromExtension(this.format)
                .orElseThrow(() -> new MojoExecutionException("Unknown output format " + this.format));
    }

    private boolean verifyParameters() {
        final Path directory = Paths.get(this.sourceFiles.getDirectory());

        if (!Files.isDirectory(directory)) {
            log.warn("Specified source directory is not a directory");
            return false;
        }

        final boolean validFormat = Format.fromExtension(this.format).isPresent();
        if (!validFormat) {
            final String formats =
                    Arrays.stream(Format.values()).map(Format::name).collect(joining(", "));
            log.error("Specified output format is not valid; supported options are {}", formats);
            return false;
        }

        return true;
    }

    private PlantUmlFacade findPlantUmlFacade() throws MojoExecutionException {
        return plantUmlFactory
                .findPlantUmlImplementation()
                .orElseThrow(() -> new MojoExecutionException(
                        "No PlantUML adapter found. Add one to your classpath. This plugin will not work without it."));
    }

    public void setSourceFiles(FileSet sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setStripPath(File stripPath) {
        this.stripPath = stripPath;
    }

    public void setPragmas(List<String> pragmas) {
        this.pragmas = pragmas;
    }
}
