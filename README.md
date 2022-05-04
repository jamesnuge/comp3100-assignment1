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

Note: To run the buildServer script you will need `gcc`, `make` and `libxml2`. The best option is to use the pre-compiled binaries from the [ds-sim](https://github.com/distsys-MQ/ds-sim)

### Building

To build an uber-jar for use on other machines/environments simply run `gradle build` and copy the jar from 'build/lib'

## Available Algorithms

Currently there are 3 algorithms available in this client implementation:
- Largest Round Robin (LRR)
- Best Fit (BF)
- Worst Fit (WF)

### Largest Round Robin

Largest round robin is a simple algorithm that will schedule all the jobs among the group of machines with the largest number of cores. 
It assigns a job to each server sequentially, then repeats the process when more jobs come in than there are machines

### Best Fit

The Best Fit algorithm tries to find the server that has the lowest number of cores required for the job that currently has no active jobs.
If there are no servers that have no jobs either active or waiting, the algorithm assigns the job to the first machine with the lowest number of cores

### Worst fit

The worst fit algorithm is the opposite of the best fit algorithm, as the name implies.
It will attempt to assign the job to a capable and non-busy machine with the largest number of cores.
If there are no machines that match that description it will assign it to the first of the largest (number of cores) server.