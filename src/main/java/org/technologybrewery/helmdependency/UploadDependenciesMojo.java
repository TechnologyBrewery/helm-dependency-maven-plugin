package org.technologybrewery.helmdependency;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import io.kokuwa.maven.helm.UploadMojo;
import io.kokuwa.maven.helm.pojo.HelmRepository;
import io.kokuwa.maven.helm.pojo.RepoType;

@Mojo(name = "upload-dependencies")
public class UploadDependenciesMojo extends UploadMojo {

    @Parameter(property = "url", required = true)
    private String url;

    @Parameter(property = "id", required = true)
    private String id;

    @Parameter(property = "type", required = true)
    private RepoType type;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    private static String HELM_PACKAGE = "helm";

    @Override
    public void execute() throws MojoExecutionException {
        if (!project.getPackaging().equals(HELM_PACKAGE)) {
            return;
        }
        uploadRepoStable = createHelmRepository(id, url, type);
        uploadRepoSnapshot = createHelmRepository(id, url, type);
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
