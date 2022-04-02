## About The Project

This project is the implementation of a scheduling client to be used with the server from the [ds-sim](https://github.com/distsys-MQ/ds-sim) project

## Getting Started

The following instructions should allow you to run the client in your local environment

### Prerequisites
- [Java 8](https://www.oracle.com/java/technologies/java8.html)
- [Gradle 4.7.x](https://docs.gradle.org/7.4/userguide/userguide.html)

### Installation and running the client

1. Clone the project
2. Run the 'buildServer' shell script, or clone the pre-compiled ds-server and move it to the project root
3. Run the ds-server with the '-n' flag
4. Run the command `gradle build`
5. Run the command `gradle run`

You can pass the algorithm acronym to the client using the '--args' option.
The ds-server must be run in the same working directory as the client, as it client relies on reading a file based on the root path
For ease of use, it is recommended that users install [SDKMAN](https://sdkman.io) and create a local environment so that you don't muddy your pre-existing java installations

### Building

To build an uber-jar for use on other machines/environments simply run `gradle build` and copy the jar from 'build/lib'
