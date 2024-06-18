/* (c) https://github.com/MontiCore/monticore */
package gradle.specific.pojo;

import de.monticore.gradle.internal.isolation.CachedIsolation;
import de.se_rwth.commons.logging.Log;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CD2POJOTask extends DefaultTask {

  protected static final CachedIsolation.WithClassPath isolator = new CachedIsolation.WithClassPath();

  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract DirectoryProperty getInput();

  @OutputDirectory
  public abstract DirectoryProperty getOutput();

  public CD2POJOTask() {
    super();
  }

  @TaskAction
  public void run() {
    // TODO: Isolate TaskAction, so that no side-effects (... static variables / Logger / Mill) occur
    // Also important for parallelization

    Set<File> inputFiles = getInput().getAsFileTree().getFiles().stream()
        .filter(File::isFile)
        .filter(f -> f.getName().endsWith(".cd"))
        .collect(Collectors.toUnmodifiableSet());

    for (File inputFile : inputFiles) {
      List<String> args = new ArrayList<>();
      {
        args.add("--input");
        args.add(inputFile.getAbsolutePath());
      }
      {
        args.add("--output");
        args.add(getOutput().getAsFile().get().getAbsolutePath());
      }

      args.add("-c2mc");

      Log.trace(args.toString(), this.getName());

      de.monticore.CDGeneratorTool.main(args.toArray(new String[0]));
    }
  }
}
