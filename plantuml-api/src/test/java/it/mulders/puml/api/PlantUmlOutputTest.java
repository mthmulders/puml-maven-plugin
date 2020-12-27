package it.mulders.puml.api;

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
            assertThat(result.getMessage()).isEqualTo("Error occurred");
        }

        @Test
        void getMessage_from_exception() {
            assertThat(new Failure(new IllegalArgumentException("Wrong argument")).getMessage()).isEqualTo("Wrong argument");
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
            assertThat(result.getProcessedCount()).isEqualTo(1);
        }
    }
}