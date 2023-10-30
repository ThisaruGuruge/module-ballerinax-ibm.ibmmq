# Ballerina `ibm.ibmmq` Library

[![Build](https://github.com/ballerina-platform/module-ballerinax-ibm.ibmmq/actions/workflows/build-timestamped-master.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerinax-ibm.ibmmq/actions/workflows/build-timestamped-master.yml)
[![codecov](https://codecov.io/gh/ballerina-platform/module-ballerinax-ibm.ibmmq/branch/main/graph/badge.svg)](https://codecov.io/gh/ballerina-platform/module-ballerinax-ibm.ibmmq)
[![Trivy](https://github.com/ballerina-platform/module-ballerinax-ibm.ibmmq/actions/workflows/trivy-scan.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerinax-ibm.ibmmq/actions/workflows/trivy-scan.yml)
[![GraalVM Check](https://github.com/ballerina-platform/module-ballerinax-ibm.ibmmq/actions/workflows/build-with-bal-test-graalvm.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerinax-ibm.ibmmq/actions/workflows/build-with-bal-test-graalvm.yml)
[![GitHub Last Commit](https://img.shields.io/github/last-commit/ballerina-platform/module-ballerinax-ibm.ibmmq.svg)](https://github.com/ballerina-platform/module-ballerinax-ibm.ibmmq/commits/main)

The `ballerinax/ibm.ibmmq` library provides an API to connect to an IBM MQ server using Ballerina.

This library is created with minimal deviation from the IBM MQ java client API to make it easy for the developers who are used to working with the IBM MQ java client API.

## Issues and projects 

Issues and Projects tabs are disabled for this repository as this is part of the Ballerina Standard Library. To report bugs, request new features, start new discussions, view project boards, etc., go to the [Ballerina Standard Library parent repository](https://github.com/ballerina-platform/ballerina-standard-library). 

This repository only contains the source code for the library.

## Build from the source

### Set up the prerequisites

* Download and install Java SE Development Kit (JDK) version 17 (from one of the following locations).

   * [Oracle](https://www.oracle.com/java/technologies/downloads/)

   * [OpenJDK](https://adoptium.net/)

        > **Note:** Set the JAVA_HOME environment variable to the path name of the directory into which you installed JDK.

2. Download and install [Docker](https://www.docker.com/). This is required to run the tests.

### Build the source

Execute the commands below to build from the source.

1. To build the library:
   ```    
   ./gradlew clean build
   ```

2. To run the tests:
   ```
   ./gradlew clean test
   ```
3. To build the library without the tests:
   ```
   ./gradlew clean build -x test
   ```
4. To debug library implementation:
   ```
   ./gradlew clean build -Pdebug=<port>
   ```
5. To debug the library with Ballerina language:
   ```
   ./gradlew clean build -PbalJavaDebug=<port>
   ```
6. Publish ZIP artifact to the local `.m2` repository:
   ```
   ./gradlew clean build publishToMavenLocal
   ```
7. Publish the generated artifacts to the local Ballerina central repository:
   ```
   ./gradlew clean build -PpublishToLocalCentral=true
   ```
8. Publish the generated artifacts to the Ballerina central repository:
   ```
   ./gradlew clean build -PpublishToCentral=true
   ```

## Contribute to Ballerina

As an open source project, Ballerina welcomes contributions from the community. 

For more information, go to the [contribution guidelines](https://github.com/ballerina-platform/ballerina-lang/blob/master/CONTRIBUTING.md).

## Code of conduct

All the contributors are encouraged to read the [Ballerina Code of Conduct](https://ballerina.io/code-of-conduct).

## Useful links

* For more information go to the [`ibm.ibmmq` library](https://lib.ballerina.io/ballerinax/ibm.ibmmq/latest).
* For example demonstrations of the usage, go to [Ballerina By Examples](https://ballerina.io/learn/by-example/).
* Chat live with us via our [Discord server](https://discord.gg/ballerinalang).
* Post all technical questions on Stack Overflow with the [#ballerina](https://stackoverflow.com/questions/tagged/ballerina) tag.
