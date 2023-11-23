package dev.turtywurty.testgradleplugin.tasks;

import com.google.gson.JsonObject;
import dev.turtywurty.testgradleplugin.TestGradlePlugin;
import dev.turtywurty.testgradleplugin.piston.version.Download;
import dev.turtywurty.testgradleplugin.piston.version.VersionPackage;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@CacheableTask
public abstract class DownloadServerTask extends DefaultTask {
    @Input
    public abstract Property<String> getVersion();

    @OutputDirectory
    @Optional
    public abstract DirectoryProperty getOutputDir();

    @TaskAction
    public void downloadServer() {
        Path versionPath = getOutputDir()
                .orElse(getProject()
                        .getLayout()
                        .getBuildDirectory()
                        .dir("minecraft")
                        .get())
                .get()
                .getAsFile()
                .toPath()
                .resolve(getVersion().get());

        VersionPackage versionPackage = readVersionPackage(versionPath);
        System.out.println("Version package path: " + versionPath.resolve("version.json"));

        Download serverDownload = versionPackage.downloads().server();
        System.out.println("Server download: " + serverDownload.url());

        Path jarPath = serverDownload.downloadToPath(versionPath);
        System.out.println("Server jar downloaded to: " + jarPath);

        System.out.println("Done!");
    }

    private VersionPackage readVersionPackage(Path versionPath) {
        Path versionJsonPath = versionPath.resolve("version.json").toAbsolutePath();
        VersionPackage versionPackage;
        try {
            String jsonStr = Files.readString(versionJsonPath);
            JsonObject json = TestGradlePlugin.GSON.fromJson(jsonStr, JsonObject.class);
            versionPackage = VersionPackage.fromJson(json);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to download client!", exception);
        }

        return versionPackage;
    }
}