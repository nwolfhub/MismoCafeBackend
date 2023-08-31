package org.nwolfhub;

import org.nwolfhub.easycli.model.Level;
import org.nwolfhub.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DockerIntegration {
    public final static String DOCKER_IMAGE = "amazoncorretto:20";

    public static void init() {
        try {
            Main.cli.printAtLevel(Level.Info, "Pulling java");
            Runtime.getRuntime().exec("docker pull " + DOCKER_IMAGE);
            Main.cli.printAtLevel(Level.Info, "Pulled java");
        } catch (IOException e) {
            Main.cli.printAtLevel(Level.Panic, "Failed to pull docker image: " + e);
        }
    }

    public static String createImage(File javaFile, String owner) {
        String name = Utils.generateString(50).toLowerCase();
        Main.cli.printAtLevel(Level.Info, "Creating new image: " + name);
        if(owner.contains("owner")) throw new IllegalArgumentException("Username cannot contain word owner");
        File dockerDir = new File("docker" + name);
        dockerDir.mkdir();
        try {
            Files.copy(javaFile.getAbsoluteFile().toPath(), Path.of(dockerDir.getAbsolutePath() + "/" + javaFile.getName()));
            javaFile.delete();
            File dockerfile = new File(dockerDir, "Dockerfile");
            dockerfile.createNewFile();
            try (FileOutputStream out = new FileOutputStream(dockerfile)) {
                String content = "FROM " + DOCKER_IMAGE + "\nADD " + javaFile.getName() + " /\nCMD [\"java\", \"-jar\", \"" + javaFile.getName() + "\"]";
                out.write(content.getBytes(StandardCharsets.UTF_8));
            }
            Runtime.getRuntime().exec("docker build -t " + name + " " + dockerDir).waitFor();
            return name;
        } catch (IOException | InterruptedException e) {
            Main.cli.printAtLevel(Level.Error, "Failed to exec: " + e);
            return null;
        }
    }

    public static Process runContainer(String name) {
        try {
            Process process = Runtime.getRuntime().exec("docker container run --name " + "mismo" + name + " " + name);
            return process;
        } catch (IOException e) {
            return null;
        }
    }

    public static void removeImage(String name) {
        try {
            Runtime.getRuntime().exec("docker image rm " + name);
        } catch (IOException e) {
            Main.cli.printAtLevel(Level.Error, "Could not remove docker image:", e);
        }
    }
}
