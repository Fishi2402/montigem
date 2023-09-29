package gradle.specific;

import de.se_rwth.commons.logging.Log;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

abstract public class SerializeCDSymbolsTask extends DefaultTask {

  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract DirectoryProperty getInput();

  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract DirectoryProperty getSymbolPath();

  @OutputDirectory
  public abstract DirectoryProperty getOutput();

  public SerializeCDSymbolsTask() {
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
        args.add("-i");
        args.add(inputFile.getAbsolutePath());
      }
      {
        args.add("-path");
        args.add(getSymbolPath().get().getAsFile().getAbsolutePath());
      }
      {
        args.add("-s");
        String relPath = getInput().get().getAsFile().toPath().relativize(inputFile.toPath()).toString();
        args.add(getOutput().file(relPath + "sym").get().getAsFile().getAbsolutePath());
      }

      Log.trace(args.toString(), this.getName());

      de.monticore.cd4code.CD4CodeTool.main(args.toArray(new String[0]));
    }
  }
}
