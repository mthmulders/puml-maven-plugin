<!---
   Copyright 2020 Maarten Mulders

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->
# PUML Maven Plugin

> A Maven Plugin for generating diagrams from PlantUML sources 

![Run tests](https://github.com/mthmulders/puml-maven-plugin/workflows/Run%20tests/badge.svg)
[![SonarCloud quality gate](https://sonarcloud.io/api/project_badges/measure?project=mthmulders_puml-maven-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=mthmulders_puml-maven-plugin)
[![SonarCloud vulnerability count](https://sonarcloud.io/api/project_badges/measure?project=mthmulders_puml-maven-plugin&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=mthmulders_puml-maven-plugin)
[![SonarCloud technical debt](https://sonarcloud.io/api/project_badges/measure?project=mthmulders_puml-maven-plugin&metric=sqale_index)](https://sonarcloud.io/dashboard?id=mthmulders_puml-maven-plugin)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=mthmulders/puml-maven-plugin)](https://dependabot.com)
[![Mutation testing badge](https://img.shields.io/endpoint?style=plastic&url=https%3A%2F%2Fbadge-api.stryker-mutator.io%2Fgithub.com%2Fmthmulders%2Fpuml-maven-plugin%2Fmain)](https://dashboard.stryker-mutator.io/reports/github.com/mthmulders/puml-maven-plugin/main)
[![Maven Central](https://img.shields.io/maven-central/v/it.mulders.puml-maven-plugin/puml-maven-plugin.svg?color=brightgreen&label=Maven%20Central)](https://search.maven.org/artifact/it.mulders.puml-maven-plugin/puml-maven-plugin)
[![](https://img.shields.io/github/license/mthmulders/puml-maven-plugin.svg)](./LICENSE)

## Usage
Add the plugin to your POM.

```xml
<plugin>
    <groupId>it.mulders.puml</groupId>
    <artifactId>plantuml-maven-plugin</artifactId>
    <version>0.1</version>
    <dependencies>
        <!-- described below -->
    </dependencies>
    <configuration>
        <!-- described below -->
    </configuration>
</plugin>
```

The plugin needs two additional dependencies:
1. An [adapter](#plantuml-adapters) for PlantUML
1. PlantUML itself

For example:

```xml
    <dependencies>
        <dependency>
            <groupId>it.mulders.puml</groupId>
            <artifactId>plantuml-v1-adapter</artifactId>
            <version>@project.version@</version>
        </dependency>
        <dependency>
            <groupId>net.sourceforge.plantuml</groupId>
            <artifactId>plantuml</artifactId>
            <version>1.2020.9</version>
        </dependency>
    </dependencies>
```

Finally, you can configure the plugin using the standard Maven plugin mechanism.
Details are below.

## Goals
This plugin provides one goal.

### `generate` &mdash; Generates diagrams from a set of input files.
This goal takes a set of input files and generates a diagram for each of them.
You can configure the goal using these parameters:

| **Input** | The set of input files as a [FileSet](https://maven.apache.org/shared/file-management/fileset.html) |
| --- | --- |
| Name | `sourceFiles` |
| Property | `plantuml.sourceFiles` | 
| Required? | yes |
| Default value | - |

| **Output** | Where to place generated diagrams. |
| --- | --- |
| Name | `outputDirectory` |
| Property | `plantuml.outputDirectory` | 
| Required? | no |
| Default value | `${project.build.directory}/plantuml` |

| **Path stripping** | A path to strip from each input file before building the output file name | 
| --- | --- |
| Name | `stripPath` |
| Property | `plantuml.stripPath` | 
| Required? | yes |
| Default value | - |

| **Output format** | Format the diagrams should be generated in. | 
| --- | --- |
| Name | `format` |
| Property | `plantuml.format` | 
| Required? | yes |
| Default value | - |
| Values | `SVG` |


## Path stripping
The plugin will remove the `stripPath` parameter from each input file name and then create the output file name from the remainder of the input file name.

To understand what this means, consider this example.
Imagine an input file **src/main/docs/diagrams/architecture/overview.puml** and the default output directory, **${project.build.directory}/plantuml**.
Without path stripping, the output file would be **${project.build.directory}/plantuml/src/main/docs/diagrams/architecture/overview.puml**.
Path stripping lets you strip a part of the input file name, so the output location will be shorter.

In the above example, a value of **src/main/docs** for the `stripPath` parameter would result in the following output file: **${project.build.directory}/plantuml/diagrams/architecture/overview.puml**.

## PlantUML Adapters
To accommodate for [changing versions of PlantUML](https://plantuml.com/versioning-scheme), this plugin requires you to add an adapter.
It is the adapters job to abstract the actual invocation of PlantUML.
This should keep the plugin API stable regardless of which version of PlantUML you want to use.

A plugin does *not* pull in the actual version of PlantUML.
This gives you the flexibility to specify the exact version of PlantUML you want to use in your project.