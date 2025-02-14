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

import static java.util.function.Predicate.not;

import it.mulders.puml.api.PlantUmlOutput;
import it.mulders.puml.api.PlantUmlOutput.Failure;
import it.mulders.puml.api.PlantUmlOutput.Success;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Aggregates the PlantUML output of each processed file and converts that into one {@link PlantUmlOutput} value.
 */
public class ItemOutputCollector implements Collector<ItemOutput, List<ItemOutput>, PlantUmlOutput> {
    @Override
    public Supplier<List<ItemOutput>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<ItemOutput>, ItemOutput> accumulator() {
        return List::add;
    }

    @Override
    public BinaryOperator<List<ItemOutput>> combiner() {
        return (a, b) -> {
            final List<ItemOutput> result = new ArrayList<>();
            result.addAll(a);
            result.addAll(b);
            return result;
        };
    }

    @Override
    public Function<List<ItemOutput>, PlantUmlOutput> finisher() {
        return (outputs) -> {
            if (outputs.stream().allMatch(ItemOutput::success)) {
                return new Success(outputs.size());
            }

            final String failedItems = outputs.stream()
                    .filter(not(ItemOutput::success))
                    .map(ItemOutput::input)
                    .map(Path::toString)
                    .collect(Collectors.joining(System.lineSeparator()));

            return new Failure("One or more diagrams could not be generated. Failed items: " + failedItems);
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
