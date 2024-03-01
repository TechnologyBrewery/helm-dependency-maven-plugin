# Helm Dependency Maven Plugin

The Helm Dependency Maven Plugin is used to deploy Helm Chart's chart and Docker dependencies to an alternate repository.

## Requirments

* Java 8
* Maven
* Helm

## Building from Source

In the Helm Dependency Maven Plugin's root directory, execute `mvn clean install` to build and install the plugin. 

## Goals

* upload-dependencies - Uploads all of a Helm Chart's chart and docker dependencies to an alternative repository. 
This also includes any sub-charts. 

Example: `mvn com.boozallen.aissemble:helm-dependency-maven-plugin:[version-number]:upload-dependencies -Did=[repositoryID] -Dtype=[NEXUS | ARTIFACTORY | CHARTMUSEUM] -DchartsUrl=[url] -DdockerUrl=[url]`

id: The repository ID used to access credentials defined in the Maven settings.xml file. 
type: Type of repository the dependencies will be deployed to.
chartsUrl: URL to the Helm Chart repository
dockerUrl: URL to the Docker repository

* pull-docker-dependencies - Pulls all of a Helm Chart's docker dependencies and installs them into the Docker daemon.

Example: `mvn org.technologybrewery.helmdependency:helm-dependency-maven-plugin:[version-number]:pull-docker-dependencies -Did=[repositoryID]`

id: The repository ID used to access credentials defined in the Maven settings.xml file. 
