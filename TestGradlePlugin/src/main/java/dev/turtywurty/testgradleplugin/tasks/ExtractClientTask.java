package dev.turtywurty.testgradleplugin.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@CacheableTask
public abstract class ExtractClientTask extends DefaultTask {
    @Input
    public abstract Property<String> getVersion();

    @OutputDirectory
    @Optional
    public abstract DirectoryProperty getOutputDir();

    @TaskAction
    public void extractJar() {
        System.out.println("Extracting jar!");

        Path versionPath = getOutputDir()
                .getOrElse(getProject()
                        .getLayout()
                        .getBuildDirectory()
                        .dir("minecraft")
                        .get())
                .getAsFile()
                .toPath()
                .resolve(getVersion().get());
        if (Files.notExists(versionPath))
            throw new IllegalStateException("Version directory does not exist!");

        Path jarPath = versionPath.resolve("client.jar");
        if (Files.notExists(jarPath))
            throw new IllegalStateException("Client jar does not exist!");

        Path outputDir = versionPath.resolve("client");
        try {
            Files.createDirectories(outputDir);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to create output directory!", exception);
        }

        // extract jar
        try (var jar = new JarFile(jarPath.toFile())) {
            int extractedFiles = 0, extractedDirs = 0;
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                Path destination = outputDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(destination);
                    extractedDirs++;
                } else {
                    Files.createDirectories(destination.getParent());
                    Files.copy(jar.getInputStream(entry), destination);
                    extractedFiles++;
                }
            }

            System.out.printf("Extracted %d files and %d directories!%n", extractedFiles, extractedDirs);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to extract jar!", exception);
        }
    }
}
