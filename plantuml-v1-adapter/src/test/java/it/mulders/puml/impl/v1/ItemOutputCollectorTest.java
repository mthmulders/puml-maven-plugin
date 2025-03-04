package it.mulders.puml.impl.v1;

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

import it.mulders.puml.api.PlantUmlOutput;
import it.mulders.puml.api.PlantUmlOutput.Success;
import java.nio.file.Paths;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ItemOutputCollectorTest implements WithAssertions {
    @Test
    void with_no_inputs_should_return_success() {
        // Arrange
        final Stream<ItemOutput> inputs = Stream.of();

        // Act
        final PlantUmlOutput output = inputs.collect(new ItemOutputCollector());

        // Assert
        assertThat(output.isSuccess()).isTrue();
        assertThat(((Success) output).processedCount()).isEqualTo(0);
    }

    @Test
    void should_work_with_parallel_stream() {
        // Arrange
        final Stream<ItemOutput> inputs = IntStream.rangeClosed(0, 10)
                .parallel()
                .mapToObj(i -> ItemOutput.builder().success(true).build());

        // Act
        final PlantUmlOutput output = inputs.collect(new ItemOutputCollector());

        // Assert
        assertThat(output.isSuccess()).isTrue();
        assertThat(((Success) output).processedCount()).isEqualTo(11);
    }

    @Test
    void with_no_failed_inputs_should_return_success() {
        // Arrange
        final Stream<ItemOutput> inputs = Stream.of(
                ItemOutput.builder().success(true).build(),
                ItemOutput.builder().success(true).build());

        // Act
        final PlantUmlOutput output = inputs.collect(new ItemOutputCollector());

        // Assert
        assertThat(output.isSuccess()).isTrue();
        assertThat(((Success) output).processedCount()).isEqualTo(2);
    }

    @Test
    void with_failed_input_should_return_failure() {
        // Arrange
        final Stream<ItemOutput> inputs = Stream.of(
                ItemOutput.builder().success(true).input(Paths.get("good.puml")).build(),
                ItemOutput.builder()
                        .success(false)
                        .input(Paths.get("wrong.puml"))
                        .build());

        // Act
        final PlantUmlOutput output = inputs.collect(new ItemOutputCollector());

        // Assert
        assertThat(output.isFailure()).isTrue();
        assertThat(((PlantUmlOutput.Failure) output).message()).contains("wrong.puml");
        assertThat(((PlantUmlOutput.Failure) output).message()).doesNotContain("good.puml");
    }
}
