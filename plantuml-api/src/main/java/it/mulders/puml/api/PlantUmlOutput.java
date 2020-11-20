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

/**
 * Output of invoking PlantUML.
 */
public interface PlantUmlOutput {
    class Success implements PlantUmlOutput {
    }

    class Failure implements PlantUmlOutput{
        private final String message;

        public Failure(final String message) {
            this.message = message;
        }

        public Failure(final Throwable cause) {
            this.message = cause.getMessage();
        }

        public String getMessage() {
            return this.message;
        }
    }

    default boolean isSuccess() {
        return this instanceof Success;
    }

    default boolean isFailure() {
        return this instanceof Failure;
    }
}
