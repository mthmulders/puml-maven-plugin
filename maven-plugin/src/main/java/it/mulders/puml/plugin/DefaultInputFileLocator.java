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

import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * {@inheritDoc}
 */
@Named
@Singleton
public class DefaultInputFileLocator implements InputFileLocator {
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Path> determineFilesForProcessing(final FileSet fileSet) throws MojoExecutionException {
        final File basedir = new File(fileSet.getDirectory());
        final String includes = String.join(",", fileSet.getIncludes());
        final String excludes = String.join(",", fileSet.getExcludes());

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
}
