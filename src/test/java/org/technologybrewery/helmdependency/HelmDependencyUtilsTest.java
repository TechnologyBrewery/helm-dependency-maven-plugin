package org.technologybrewery.helmdependency;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class HelmDependencyUtilsTest extends UploadDependenciesMojo {

    private List<String> imageNames;
    private String chartDirectory = Paths.get("src", "test", "resources", "chart").toString();

    @Given("a packaged Helm Chart {word}")
    public void a_packaged_helm_chart(String chartName) {
        File chartTarball = new File(Paths.get(chartDirectory, chartName).toString());
        assertTrue(chartTarball.exists());
    }
    @When("the Docker dependencies action is triggered")
    public void the_docker_dependencies_action_is_triggered() throws MojoExecutionException, InterruptedException {
        useLocalHelmBinary = true;
        autoDetectLocalHelmBinary = true;
        imageNames = HelmDependencyUtils.getDependentDockerImages(helm(), chartDirectory);
        
    }
    @Then("Docker dependency names {word} are received")
    public void all_docker_dependencies_are_received(String names) {
        List<String> compareNames = Arrays.asList(names.split(","));
        imageNames.containsAll(compareNames);
    }

}
