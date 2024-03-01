package org.technologybrewery.helmdependency;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.maven.plugin.MojoExecutionException;

import io.kokuwa.maven.helm.pojo.HelmExecutable;

public class HelmDependencyUtils {

    private static String HELM_PACKAGE = "helm";

    /**
     * 
     * From the given chart directory, reads the charts template document and determines all dependent Docker images associated
     * with chart. 
     * @param chartDirectory - Path to where the chart.yaml is located
     * @return A list of dependent Docker images
     * @throws MojoExecutionException
     * @throws InterruptedException
     */
    public static List<String> getDependentDockerImages(HelmExecutable helmExecutable, String chartDirectory) throws MojoExecutionException, InterruptedException {
		File[] chartTarballs = new File(chartDirectory).listFiles((dir, filename) -> {
			return filename.endsWith(".tgz");
		});
		List<String> dependentImages = new ArrayList<>();
		for(File chartTarball : chartTarballs) {
			String output = helmExecutable
							.arguments("template", chartTarball.getAbsolutePath())
							.execute("Could not get docker image dependencies from chart's template");
			Scanner scanner = new Scanner(output);
			while(scanner.hasNextLine()) {
				String input = scanner.nextLine();
                String imageKeyword = "image:";
				if(input.contains(imageKeyword)) {
                    int startIndex = input.indexOf(imageKeyword) + imageKeyword.length();
                    String imageName = input.substring(startIndex).replaceAll("\"", "").trim();
					dependentImages.add(imageName);
				}
			}
			scanner.close();
		}
        return dependentImages;
    }

    public static boolean isHelmPackage(String packageType) {
        return packageType.equals(HELM_PACKAGE);
    }
}
