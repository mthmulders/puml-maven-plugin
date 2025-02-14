package it.mulders.puml.tests;

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

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@MavenJupiterExtension
public class BasicUsageIT {
    @MavenTest
    @MavenGoal("generate-resources")
    void should_generate_svg_diagram(final MavenExecutionResult result) throws IOException {
        assertThat(result).isSuccessful();

        final Path baseDir = result.getMavenProjectResult().getTargetProjectDirectory();
        final Path outputDirectory = baseDir.resolve(Paths.get("target", "plantuml"));
        assertThat(outputDirectory).exists().isDirectory();

        final Path outputFile = outputDirectory.resolve(Paths.get("docs", "example.svg"));

        final List<String> log = Files.readAllLines(result.getMavenLog().getStdout());
        assertThat(log).anySatisfy(line -> assertThat(line).contains("Using PlantUML version"));
        assertThat(Files.walk(outputDirectory)).contains(outputFile);
    }

    @MavenTest
    @MavenGoal("generate-resources")
    void should_generate_png_diagram(final MavenExecutionResult result) throws IOException {
        assertThat(result).isSuccessful();

        final Path baseDir = result.getMavenProjectResult().getTargetProjectDirectory();
        final Path outputDirectory = baseDir.resolve(Paths.get("target", "plantuml"));
        assertThat(outputDirectory).exists().isDirectory();

        final Path outputFile = outputDirectory.resolve(Paths.get("docs", "example.png"));

        final List<String> log = Files.readAllLines(result.getMavenLog().getStdout());
        assertThat(log).anySatisfy(line -> assertThat(line).contains("Using PlantUML version"));
        assertThat(Files.walk(outputDirectory)).contains(outputFile);
    }
}
