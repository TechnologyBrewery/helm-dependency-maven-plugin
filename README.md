# Helm Dependency Maven Plugin

The Helm Dependency Maven Plugin is used to deploy Helm Chart chart dependencies to an alternate repository.

## Requirments

* Java 8
* Maven

## Building from Source

In the Helm Dependency Maven Plugin's root directory, execute `mvn clean install` to build and install the plugin. 

## Goals

* upload-dependencies - Uploads all of a Helm Chart's chart dependencies to an alternative repository. 
This also includes any sub-charts. 

Example: `mvn com.boozallen.aissemble:helm-dependency-maven-plugin:[version-number]:upload-dependencies -Did=[repositoryID] -Dtype=[NEXUS | ARTIFACTORY | CHARTMUSEUM] -Durl=[url]`

id: The repository ID used to access credentials defined in the Maven settings.xml file. 
type: Type of repository the dependencies will be deployed to.
url: URL to the repository