package it.mulders.puml.tests;

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
public class PragmaUsageIT {
    @MavenTest
    @MavenGoal("generate-resources")
    void should_generate_single_pragma(final MavenExecutionResult result) throws IOException {
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
    void should_generate_multiple_pragmas(final MavenExecutionResult result) throws IOException {
        assertThat(result).isSuccessful();

        final Path baseDir = result.getMavenProjectResult().getTargetProjectDirectory();
        final Path outputDirectory = baseDir.resolve(Paths.get("target", "plantuml"));
        assertThat(outputDirectory).exists().isDirectory();

        final Path outputFile1 = outputDirectory.resolve(Paths.get("docs", "example1.svg"));
        final Path outputFile2 = outputDirectory.resolve(Paths.get("docs", "example2.svg"));

        final List<String> log = Files.readAllLines(result.getMavenLog().getStdout());
        assertThat(log).anySatisfy(line -> assertThat(line).contains("Using PlantUML version"));
        assertThat(Files.walk(outputDirectory)).contains(outputFile1, outputFile2);
    }
}
