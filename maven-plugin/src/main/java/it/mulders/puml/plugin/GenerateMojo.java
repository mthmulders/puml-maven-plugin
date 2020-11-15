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

import it.mulders.puml.api.PlantUmlFacade;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import javax.inject.Inject;

@Mojo(name = "generate")
public class GenerateMojo extends AbstractMojo {
    private final PlantUmlFactory factory;

    @Inject
    public GenerateMojo(final PlantUmlFactory factory) {
        this.factory = factory;
    }

    @Override
    public void execute() throws MojoExecutionException {
        findPlantUmlFacade();
    }

    private PlantUmlFacade findPlantUmlFacade() throws MojoExecutionException {
        return factory.findPlantUmlImplementation()
                .orElseThrow(() -> new MojoExecutionException("No PlantUML adapter found. Add one to your classpath. This plugin will not work without it."));
    }
}