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

import java.nio.file.Path;
import java.util.Collection;

/**
 * Locates the files that match a particular {@link FileSet}.
 */
public interface InputFileLocator {
    /**
     * Given a {@link FileSet}, find all files that match.
     * @param fileSet A fileset
     * @return A collection with the paths to matching files.
     * @throws MojoExecutionException In case of errors scanning the filesystem.
     */
    Collection<Path> determineFilesForProcessing(final FileSet fileSet) throws MojoExecutionException;
}
