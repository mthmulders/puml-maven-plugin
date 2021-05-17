package it.mulders.puml.tests;

/*
 * Copyright 2021 Maarten Mulders
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

import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.assertj.core.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
public class Issue23IT {
    @MavenTest
    @MavenGoal("process-resources")
    void should_generate_png_file(final MavenExecutionResult result) throws IOException {
        assertThat(result).isSuccessful();

        final Path baseDir = Paths.get(result.getMavenProjectResult().getTargetProjectDirectory().toURI());
        final Path outputDirectory = baseDir.resolve(Paths.get("target", "site"));
        Assertions.assertThat(outputDirectory)
                .exists()
                .isDirectory();

        final Path outputFile = outputDirectory.resolve(Paths.get("class-diagram.png"));
        Assertions.assertThat(Files.walk(outputDirectory)).contains(outputFile);
    }
}
