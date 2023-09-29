package gradle.specific;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.IgnoreEmptyDirectories;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

abstract public class SerializeGUISymbolsTask extends DefaultTask {

  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract DirectoryProperty getInput();

  @InputDirectory
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract DirectoryProperty getSymbolPath();

  @OutputDirectory
  public abstract DirectoryProperty getOutput();

  public SerializeGUISymbolsTask() {
    super();
  }

  @TaskAction
  public void run() {
    // TODO: Isolate TaskAction, so that no side-effects (... static variables / Logger / Mill) occur
    // Also important for parallelization

    Set<File> inputFiles = getInput().getAsFileTree().getFiles().stream()
        .filter(File::isFile)
        .filter(f -> f.getName().endsWith(".gui"))
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
      de.monticore.guidsl.GUIDSLTool.main(args.toArray(new String[0]));
    }
  }
}
