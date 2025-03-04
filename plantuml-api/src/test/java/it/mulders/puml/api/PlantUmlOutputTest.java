package it.mulders.puml.api;

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

import it.mulders.puml.api.PlantUmlOutput.Failure;
import it.mulders.puml.api.PlantUmlOutput.Success;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlantUmlOutputTest implements WithAssertions {
    @Nested
    class FailureTest {
        private final Failure result = new Failure("Error occurred");

        @Test
        void is_failure() {
            assertThat(result.isFailure()).isTrue();
        }

        @Test
        void is_no_success() {
            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        void getMessage() {
            assertThat(result.message()).isEqualTo("Error occurred");
        }

        @Test
        void getMessage_from_exception() {
            assertThat(new Failure(new IllegalArgumentException("Wrong argument")).message())
                    .isEqualTo("Wrong argument");
        }
    }

    @Nested
    class SuccessTest {
        private final Success result = new Success(1);

        @Test
        void is_success() {
            assertThat(result.isFailure()).isFalse();
        }

        @Test
        void is_no_failure() {
            assertThat(result.isSuccess()).isTrue();
        }

        @Test
        void getProcessedCount() {
            assertThat(result.processedCount()).isEqualTo(1);
        }
    }
}
