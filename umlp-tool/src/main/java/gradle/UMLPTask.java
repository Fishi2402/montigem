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
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class UMLPTask extends DefaultTask {

    @Incremental
    @Optional
    @InputDirectory
    abstract DirectoryProperty getInputDir();

    @Optional
    @InputFile
    @Incremental
    @Incubating // Use `inputDir` instead!
    abstract RegularFileProperty getInputFile();

    @InputDirectory
    @Optional
    @Incremental
    abstract DirectoryProperty getHwcDir();

    @InputFile
    @Optional
    abstract RegularFileProperty getScript();

    @Input
    @Optional
    abstract ListProperty<String> getMoreArgs();


    @OutputDirectory
    abstract DirectoryProperty getOutputDir();

    @OutputDirectory
    abstract DirectoryProperty getReportDir();

    @Inject
    abstract public WorkerExecutor getWorkerExecutor();

    private WorkQueue workQueue;



    public UMLPTask(){
    this.getReportDir().convention(
            getProject().getLayout().getBuildDirectory().dir("umlp_reports/task_"+this.getName())
        );
    }

    protected List<String> createArgList(File f){
      List<String> result = new ArrayList<>();
      result.add("-i");
      result.add(f.getPath());

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

    protected boolean isUpToDate(File f, InputChanges inputChanges){
        AtomicBoolean result = new AtomicBoolean(true);  // File is up-to-date, unless a change is found

        // inputFile is not part of inputChanges
        // TODO: Better Solution
        try {
            if (getInputFile().isPresent() && getInputFile().getAsFile().get().getCanonicalPath().equals(f.getCanonicalPath())) {
                result.set(false);
            }
        } catch (IOException e){
            result.set(false);
        }

        // Check for changes in <inputDir>
        inputChanges.getFileChanges(getInputDir()).forEach(ch -> {
            try {
                if (ch.getFile().getCanonicalPath().equals(f.getCanonicalPath())) {
                    result.set(false);
                }
            } catch (IOException e){
                result.set(false);  // If an error occurs, it is not up-to-date
            }
        });
        // TODO: Use IncGenResult
        return result.get();
    }

    protected void deletePreviousOutput(File f){
        // TODO do not parse IncGenFile both in up-to-date check and delete
      try {
        IncGenData lastRun = new IncGenData(getIncGenFile(f));
        Log.warn("Deleting generated artifacts from previous run of " + f.getName());
        lastRun.getGeneratedFiles().forEach( toDelete -> {
          Log.warn("\t Delete " + toDelete.getPath() );
          toDelete.delete();
        });
      } catch (IOException e) {}

      // Deleting Old Reports
        try {
            FileUtils.deleteDirectory(this.getReportDirOfFile(f).getAsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Directory getReportDirOfFile(File f){
        String pathName = f.getName();
        if(getInputDir().isPresent()) {
            Path dirPath = getInputDir().get().getAsFile().toPath();
            Path filePath = f.toPath();
            pathName = dirPath.relativize(filePath).toString();
        }
        return getReportDir().dir(pathName.replace(".","__")).get();
    }

    protected File getIncGenFile(File inputFile){
      return getReportDirOfFile(inputFile).dir(
          inputFile.getName().toLowerCase().replace(".","__")
        ).file("IncGenGradleCheck.txt").getAsFile();
    }

    protected void runOnSingleInputFile(File f,InputChanges inputChanges){
        assert f.exists();
        assert f.isFile();
        assert isUMLPFile(f);

        if(!isUpToDate(f, inputChanges)) {
            Log.warn(f.getName() + " is *NOT* UP-TO-DATE, starting generation process");

            deletePreviousOutput(f);

            workQueue.submit(UMLPAction.class, param -> {
                param.getArgs().set(createArgList(f));
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

        if(getInputFile().isPresent() && !isUMLPFile(this.getInputFile().getAsFile().get())){
            throw new IllegalArgumentException("<inputFile> must be a umlp-file");
        }
    }

    protected boolean isUMLPFile(File f){
      return f.getName().endsWith(".umlp");
    }

    @TaskAction
    public void run(InputChanges inputChanges) throws IOException {
        checkParameters();
        workQueue = getWorkerExecutor().classLoaderIsolation();

        Log.warn("Start UMLPTask");

        // Remove Artifacts from deleted inputs
        inputChanges.getFileChanges(this.getInputDir()).forEach(ch -> {
            if(ch.getChangeType() == ChangeType.REMOVED && isUMLPFile(ch.getFile()) && ch.getFileType() == FileType.FILE){
                this.deletePreviousOutput(ch.getFile());
            }
        });


        // Generate from InputDir
        if(getInputDir().isPresent()) {
            assert(getInputDir().getAsFile().get().isDirectory());
            getInputDir().getAsFileTree().getFiles().stream()
                    .filter(this::isUMLPFile)
                    .forEach(x->runOnSingleInputFile(x,inputChanges));
        }

        // Generate from InputFile
        if(this.getInputFile().isPresent()){
            runOnSingleInputFile(this.getInputFile().get().getAsFile(),inputChanges);
        }
    }
}