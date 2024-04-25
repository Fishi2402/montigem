package gradle.specific;

import com.google.cloud.tools.jib.api.CacheDirectoryCreationException;
import com.google.cloud.tools.jib.api.Containerizer;
import com.google.cloud.tools.jib.api.DockerDaemonImage;
import com.google.cloud.tools.jib.api.InvalidImageReferenceException;
import com.google.cloud.tools.jib.api.Jib;
import com.google.cloud.tools.jib.api.JibContainer;
import com.google.cloud.tools.jib.api.JibContainerBuilder;
import com.google.cloud.tools.jib.api.LogEvent;
import com.google.cloud.tools.jib.api.RegistryException;
import com.google.cloud.tools.jib.api.RegistryImage;
import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public abstract class UMLPFrontendImageTask extends DefaultTask {

  @InputFile
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract RegularFileProperty getCaddyfile();

  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract ConfigurableFileCollection getServedFiles();

  @Input
  public abstract Property<String> getFullImageName();

  @Input
  public abstract Property<Boolean> getUseRegistry();

  @Input
  @Optional
  public abstract Property<String> getRegistryUser();

  @Input
  @Optional
  public abstract Property<String> getRegistryPassword();

  public UMLPFrontendImageTask() {
    getCaddyfile().convention(getProject().getLayout().file(getProject().provider(() -> getProject().getBuildDir().toPath().resolve("frontendGen").resolve("Caddyfile").toFile())));
    getServedFiles().from(
        getProject().provider(() ->
            getProject().fileTree(getProject().getBuildDir().toPath().resolve("frontendGen").resolve("dist").resolve("client")).getFiles().stream().map(File::toPath).collect(Collectors.toList())
        )
    );
  }

  @TaskAction
  public void run() throws InvalidImageReferenceException, IOException, CacheDirectoryCreationException, ExecutionException, InterruptedException, RegistryException {
    Logger logger = getProject().getLogger();

    JibContainerBuilder b = getBuilder();
    Containerizer containerizer;
    if(getUseRegistry().get()){
      RegistryImage registryImage = RegistryImage.named(getFullImageName().get());
      if(getRegistryUser().isPresent() && getRegistryPassword().isPresent()){
        registryImage.addCredential(getRegistryUser().get(), getRegistryPassword().get());
      }
      containerizer = Containerizer.to(registryImage);
    }else{
      containerizer = Containerizer.to(DockerDaemonImage.named(getFullImageName().get()));
    }
    containerizer.addEventHandler(LogEvent.class, le -> {
      switch (le.getLevel()){
        case ERROR:
          logger.error(le.getMessage());
          break;
        case WARN:
          logger.warn(le.getMessage());
          break;
        case LIFECYCLE:
          logger.lifecycle(le.getMessage());
          break;
        case PROGRESS:
          logger.debug(le.getMessage());
          break;
        case INFO:
          logger.info(le.getMessage());
          break;
        case DEBUG:
          logger.debug(le.getMessage());
          break;
      }
    });
    JibContainer image = b.containerize(containerizer);
    logger.info("Build image " + image.getTargetImage());
  }

  @Internal
  protected JibContainerBuilder getBuilder() throws IOException, InvalidImageReferenceException {
    return Jib.from("caddy:2.7-alpine")
        .addLayer(List.of(getCaddyfile().get().getAsFile().toPath()), AbsoluteUnixPath.get("/etc/caddy/"))
        .addLayer(getServedFiles().getFiles().stream().map(File::toPath).collect(Collectors.toList()), AbsoluteUnixPath.get("/srv"));
  }
}
