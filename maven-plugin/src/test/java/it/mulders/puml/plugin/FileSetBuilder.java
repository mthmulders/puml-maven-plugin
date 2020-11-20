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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public class FileSetBuilder {
    private final FileSet fileSet = new FileSet();

    public FileSetBuilder baseDirectory(final Path baseDirectory) {
        this.fileSet.setDirectory(baseDirectory.toString());
        return this;
    }

    public FileSetBuilder includes(final Collection<String> includes) {
        this.fileSet.setIncludes(new ArrayList<>(includes));
        return this;
    }

    public FileSetBuilder excludes(final Collection<String> excludes) {
        this.fileSet.setExcludes(new ArrayList<>(excludes));
        return this;
    }

    public FileSet build() {
        return this.fileSet;
    }
}
