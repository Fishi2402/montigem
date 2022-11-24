package gradle;

import com.google.common.collect.Sets;
import de.se_rwth.commons.logging.Log;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class UMLPOutputReportCheck extends DefaultTask {
  @InputDirectory
  abstract DirectoryProperty getInputDir();

  @InputDirectory // Nothing is written, only read!
  abstract DirectoryProperty getOutputDir();

  @InputDirectory // Nothing is written, only read!
  abstract DirectoryProperty getReportDir();

  @TaskAction
  public void run() {
    UMLPTask dummyUMLPTask = this.getProject().getTasks().create("dummyTestTask_usedForOutputReportFrom" + this.getName(), UMLPTask.class);
    dummyUMLPTask.getReportDir().set(this.getReportDir());
    dummyUMLPTask.getInputDir().set(this.getInputDir());
    dummyUMLPTask.getOutputDir().set(this.getOutputDir());

    Set<File> reportedGenFiles = getInputDir().getAsFileTree().getFiles().stream()
        .filter(dummyUMLPTask::isInputFile)
        .peek(f -> Log.info("IncGenFile is in: " + dummyUMLPTask.getIncGenFile(f), this.getClass().getName()))
        .peek(f -> {if (!dummyUMLPTask.getIncGenFile(f).exists()){
          Log.error("Didn't find report from previous run! This leads to errors in incremental build\n" +
              "Is <Reporting> enabled in UMLPTask? Is the <IncGenGradleReporter> active?\n\n" +
              "\tInput File: " + f.getPath() + "\n" +
              "\tExpected Report in: " + dummyUMLPTask.getIncGenFile(f).getPath() + "\n");
        }} )
        .map(f ->
            new IncGenData(dummyUMLPTask.getIncGenFile(f), dummyUMLPTask.getProject().getProjectDir().toPath().toAbsolutePath().toFile())
        )
        .map(incGen -> incGen.generatedFiles)
        .flatMap(Set::stream)
        .collect(Collectors.toSet());
    Set<File> realGenFiles = getOutputDir().getAsFileTree().getFiles();
    Set<File> notReportedGenFiles = Sets.difference(realGenFiles, reportedGenFiles);

    if(!notReportedGenFiles.isEmpty()){
      Log.error("There are generated files, which are not reported! \n"
      +"This breaks the incremental build of the UMLPTask\n"
      + "Report generated files using <Reporting.reportFileCreation(...)> \n"
      + "\n"
      + "The following files are not reported:\n\t" +
          notReportedGenFiles.stream().map(File::getPath).collect(Collectors.joining("\n\t")));
    }
  }
}
