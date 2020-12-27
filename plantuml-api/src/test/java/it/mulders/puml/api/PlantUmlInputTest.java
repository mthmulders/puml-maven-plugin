package it.mulders.puml.api;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

class PlantUmlInputTest implements WithAssertions {
    private static final Collection<Path> filesForProcessing = Arrays.asList(
            Paths.get("file1.puml"),
            Paths.get("file2.puml")
    );
    private static final Path outputDirectory = Paths.get(".");

    private final PlantUmlInput result = PlantUmlInput.builder()
            .filesForProcessing(filesForProcessing)
            .outputDirectory(outputDirectory)
            .build();

    @Test
    void filesForProcessing() {
        assertThat(result.getFilesForProcessing()).isEqualTo(filesForProcessing);
    }

    @Test
    void outputDirectory() {
        assertThat(result.getOutputDirectory()).isEqualTo(outputDirectory);
    }
}