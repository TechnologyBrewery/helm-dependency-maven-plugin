package org.technologybrewery.helmdependency;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.technologybrewery.orphedomos.util.exec.DockerCommandExecutor;

import io.kokuwa.maven.helm.UploadMojo;

@Mojo(name = "pull-docker-dependencies")
public class PullDockerDependenciesMojo extends UploadMojo {

    @Parameter(property = "id", required = true)
    private String id;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        if(!HelmDependencyUtils.isHelmPackage(project.getPackaging())) {
            return;
        }

        useLocalHelmBinary = true;
		autoDetectLocalHelmBinary = true;
        for(Path chartDirectory : getChartDirectories()) {
            try {
                pullDockerImages(chartDirectory.toString());
            } catch (Exception e) {
                getLog().info(e.getMessage());
            }
        }
    }

    private void pullDockerImages(String chartDirectory) throws MojoExecutionException, InterruptedException {
        String chartTarballs = Paths.get(chartDirectory, "target", "helm", "repo").toString();
        List<String> imageNames = HelmDependencyUtils.getDependentDockerImages(helm(), chartTarballs);
		DockerCommandExecutor executor = new DockerCommandExecutor(project.getBasedir());
		for(String imageName : imageNames) {
            try {
                executor.executeAndLogOutput(Arrays.asList("pull", imageName));
            } catch (Exception e) {
                getLog().info(e.getMessage());
            }
		}
	}    
}
