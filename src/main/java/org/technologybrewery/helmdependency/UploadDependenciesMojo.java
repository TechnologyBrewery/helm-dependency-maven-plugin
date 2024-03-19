package org.technologybrewery.helmdependency;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.technologybrewery.orphedomos.util.exec.DockerCommandExecutor;
import org.technologybrewery.orphedomos.util.exec.DockerCredentialExecutor;

import io.kokuwa.maven.helm.UploadMojo;
import io.kokuwa.maven.helm.pojo.HelmRepository;
import io.kokuwa.maven.helm.pojo.RepoType;

@Mojo(name = "upload-dependencies")
public class UploadDependenciesMojo extends UploadMojo {

    @Parameter(property = "chartsUrl", required = true)
    private String chartsUrl;

    @Parameter(property = "dockerUrl", required = true)
    private String dockerUrl;

    @Parameter(property = "id", required = true)
    private String id;

    @Parameter(property = "type", required = true)
    private RepoType type;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        if (!HelmDependencyUtils.isHelmPackage(project.getPackaging())) {
            return;
        }
        uploadRepoStable = createHelmRepository(id, chartsUrl, type);
        uploadRepoSnapshot = createHelmRepository(id, chartsUrl, type);
		useLocalHelmBinary = true;
		autoDetectLocalHelmBinary = true;
        DockerCredentialExecutor credentials = new DockerCredentialExecutor(settings, id, dockerUrl, project.getBasedir(), true, false);
        credentials.login();
        for (Path chartDirectory : getChartDirectories()) {
            outputDirectory = new File(Paths.get(chartDirectory.toString(), "charts").toString());
            if (!outputDirectory.exists()) {
                getLog().warn(String.format("Helm chart at %s either does " + 
                    "not contain dependencies or is out of date", chartDirectory.toString()));;
                continue;
            }
            try {
                super.execute();
            } catch (Exception e) {
                getLog().info(String.format("%s:%s", e.getMessage(), e.getCause()));
            }
            try {
                deployDockerDependentDockerImages(chartDirectory.toString());
            } catch (Exception e) {
                getLog().info(String.format("%s:%s", e.getMessage(), e.getCause()));
            }
        }
        credentials.logout();
    }

    private void deployDockerDependentDockerImages(String chartDirectory) throws InterruptedException, MojoExecutionException {
        String chartTarballs = Paths.get(chartDirectory, "target", "helm", "repo").toString();
        HashSet<String> dockerImages = new HashSet<>(HelmDependencyUtils.getDependentDockerImages(helm(), chartTarballs));
        DockerCommandExecutor executor = new DockerCommandExecutor(project.getBasedir());
        for(String dockerImage : dockerImages) {
            String newTag = String.format("%s%s", dockerUrl, dockerImage);
            try {
                executor.executeAndLogOutput(Arrays.asList("tag", dockerImage, newTag));
                executor.executeAndLogOutput(Arrays.asList("push", newTag));
            }
            catch (Exception e) {
                getLog().error(e.getMessage());
            }
        }
    }

    private HelmRepository createHelmRepository(String id, String url, RepoType type) {
        HelmRepository repo = new HelmRepository();
        repo.setName(id);
        repo.setUrl(url);
        repo.setType(type);
        return repo;
    }
}
