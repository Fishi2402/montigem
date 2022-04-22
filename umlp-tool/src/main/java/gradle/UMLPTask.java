package gradle;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Incubating;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileType;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.*;
import org.gradle.work.ChangeType;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class UMLPTask extends DefaultTask {

    @Incremental
    @org.gradle.api.tasks.Optional
    @InputDirectory
    abstract DirectoryProperty getInputDir();

    @org.gradle.api.tasks.Optional
    @InputFile
    @Incremental
    @Incubating // Use `inputDir` instead!
    abstract RegularFileProperty getInputFile();

    @InputDirectory
    @org.gradle.api.tasks.Optional
    @Incremental
    abstract DirectoryProperty getHwcDir();

    @InputFile
    @org.gradle.api.tasks.Optional
    abstract RegularFileProperty getScript();

    @Input
    @org.gradle.api.tasks.Optional
    abstract ListProperty<String> getMoreArgs();

    @OutputDirectory
    abstract DirectoryProperty getOutputDir();

    @OutputDirectory
    abstract DirectoryProperty getReportDir();

    @Inject
    abstract public WorkerExecutor getWorkerExecutor();

    private WorkQueue workQueue;

    public UMLPTask(){
      super();
      this.getReportDir().convention(
            getProject().getLayout().getBuildDirectory().dir("umlp_reports/task_"+this.getName())
        );
    }

  /**
   *
   * @param filePath Path of InputFile
   * @return Arguments that can be used to start CLI-Tool
   */
  protected List<String> createArgList(Path filePath){
      assert(filePath.toFile().isFile());
      assert(isInputFile(filePath.toFile()));

      List<String> result = new ArrayList<>();
      result.add("-i");
      result.add(filePath.toString());

      result.add("-o");
      result.add(this.getOutputDir().getAsFile().get().getPath());

      result.add("-r");
      result.add(this.getReportDir().getAsFile().get().getPath());

      if(this.getHwcDir().isPresent()){
        result.add("-hwc");
        result.add(this.getHwcDir().getAsFile().get().getPath());
      }

      if(this.getMoreArgs().isPresent()){
        result.addAll(this.getMoreArgs().get());
      }

      return result;
    }

  /**
   *
   * @param lastRun Data from previous run, used to check if inputs have changed
   * @param inputChanges List of file-changes from gradle
   * @return if the tool should run again, or if the previous output can be reused
   */
    protected boolean isUpToDate(IncGenData lastRun, InputChanges inputChanges){
        // TODO: BETTER!!
        return false;
    }

    protected void deletePreviousOutput(File f, IncGenData lastRun){
      Log.warn("Deleting generated artifacts from previous run of " + f.getName());
      lastRun.deleteGeneratedFiles();

      // Deleting Old Reports
        try {
            FileUtils.deleteDirectory(this.getReportDirOfFile(f).getAsFile());
        } catch (IOException e) {
            Log.warn("Error deleting old reports. " +
                "This could cause errors in upcoming incremental builds." +
                "Affected directory: " + this.getReportDirOfFile(f));
        }
    }

  /**
   * @param f File, which is used as tool-input
   * @return Sub-Directory in <ReportDir>, where reports for input file should be stored
   */
  protected Directory getReportDirOfFile(File f){
        String pathName = f.getName();
        if(getInputDir().isPresent()) {
            Path dirPath = getInputDir().get().getAsFile().toPath();
            Path filePath = f.toPath();
            pathName = dirPath.relativize(filePath).toString();
        }
        return getReportDir().dir(pathName.replace(".","__")).get();
    }

  /**
   * @param inputFile File, which is used as tool-input
   * @return Generated 'IncGenGradleCheck' file from previous run. File might not always exist.
   */
    protected File getIncGenFile(File inputFile){
      return getReportDirOfFile(inputFile).dir(
          inputFile.getName().toLowerCase().replace(".","__")
        ).file("IncGenGradleCheck.txt").getAsFile();
    }

    protected void runOnSingleInputFile(File f, InputChanges inputChanges){
        assert f.exists();
        assert f.isFile();
        assert isInputFile(f);

      IncGenData lastRun = new IncGenData(getIncGenFile(f));

        if(!isUpToDate(lastRun, inputChanges)) {
            Log.warn(f.getName() + " is *NOT* UP-TO-DATE, starting generation process");

            deletePreviousOutput(f, lastRun);

            workQueue.submit(UMLPAction.class, param -> {
                param.getArgs().set(createArgList(f.toPath()));
            });
        } else {
            Log.warn(f.getName() + " is UP-TO-DATE, no action required");
        }
    }

    protected void checkParameters() throws IllegalArgumentException{
        if(!getMoreArgs().isPresent()){
            return;
        }
        List<String> arg = this.getMoreArgs().get();
        if(arg.contains("-i") || arg.contains("-o") || arg.contains("-hwc")){
            throw new IllegalArgumentException("'moreArgs' must not contain '-i' / '-o' or '-hwc' flag. Use UMLPTask parameter instead");
        }

        if(getScript().isPresent()){
            throw new IllegalArgumentException("<script> not yet supported");
        }

        if(getInputFile().isPresent() && !isInputFile(this.getInputFile().getAsFile().get())){
            throw new IllegalArgumentException("<inputFile> must be a umlp-file");
        }
    }

    protected boolean isInputFile(File f){
      return f.getName().endsWith(".umlp");
    }

    @TaskAction
    public void run(InputChanges inputChanges) throws IOException {
      Log.warn("Start UMLPTask" + this.getName());

      checkParameters();
        workQueue = getWorkerExecutor().classLoaderIsolation();

        Log.warn("Started UMLPTask " + this.getName());

        // Remove Artifacts from deleted inputs
        inputChanges.getFileChanges(this.getInputDir()).forEach(ch -> {
            if(ch.getChangeType() == ChangeType.REMOVED && isInputFile(ch.getFile()) && ch.getFileType() == FileType.FILE){
                this.deletePreviousOutput(ch.getFile(), new IncGenData(getIncGenFile(ch.getFile())));
            }
        });


        // Generate from InputDir
        if(getInputDir().isPresent()) {
            assert(getInputDir().getAsFile().get().isDirectory());
            getInputDir().getAsFileTree().getFiles().stream()
                    .filter(this::isInputFile)
                    .forEach(x->runOnSingleInputFile(x,inputChanges));
        }

        // Generate from InputFile
        if(this.getInputFile().isPresent()){
            runOnSingleInputFile(this.getInputFile().get().getAsFile(),inputChanges);
        }
    }
}