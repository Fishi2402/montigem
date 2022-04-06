package gradle;

import de.se_rwth.commons.logging.Log;
import org.gradle.api.DefaultTask;
import org.gradle.api.Incubating;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.Optional;
import org.gradle.work.ChangeType;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class UMLPTask extends DefaultTask {

    @Incremental
    @Optional
    @InputDirectory
    abstract DirectoryProperty getInputDir();

    @Optional
    @InputFile
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

    @Inject
    abstract public WorkerExecutor getWorkerExecutor();

    private WorkQueue workQueue;



    protected boolean isUpToDate(File f, InputChanges inputChanges){
        AtomicBoolean result = new AtomicBoolean(true);  // File is up-to-date, unless a change is found

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
        // TODO
    }

    protected void runOnSingleInputFile(File f,InputChanges inputChanges){
        assert f.exists();
        assert f.isFile();
        assert isUMLPFile(f);

        if(!isUpToDate(f, inputChanges)) {
            Log.warn(f.getName() + " is *NOT* UP-TO-DATE, starting generation process");

            deletePreviousOutput(f);
            workQueue.submit(UMLPAction.class, param -> {
                param.getInputFile().set(f);
                param.getOutputDir().set(this.getOutputDir());
                param.getHwcDir().set(this.getHwcDir());
                param.getMoreArgs().set(this.getMoreArgs());
            });
        } else {
            Log.warn(f.getName() + " is UP-TO-DATE, no action required");
        }
    }

    protected void checkMoreArgs() throws IllegalArgumentException{
        if(!getMoreArgs().isPresent()){
            return;
        }
        List<String> arg = this.getMoreArgs().get();
        if(arg.contains("-i") || arg.contains("-o") || arg.contains("-hwc")){
            throw new IllegalArgumentException("'moreArgs' must not contain '-i' / '-o' or '-hwc' flag. Use UMLPTask parameter instead");
        }
    }

    protected boolean isUMLPFile(File f){
      return f.getName().endsWith(".umlp") && f.isFile();
    }

    @TaskAction
    public void run(InputChanges inputChanges) throws IOException {
        checkMoreArgs();
        workQueue = getWorkerExecutor().classLoaderIsolation();

        Log.warn("Start UMLPTask");

        // Remove Artifacts from deleted inputs
        inputChanges.getFileChanges(this.getInputDir()).forEach(ch -> {
            if(ch.getChangeType() == ChangeType.REMOVED && isUMLPFile(ch.getFile())){
                this.deletePreviousOutput(ch.getFile());
            }
        });

        if(getInputDir().isPresent()) {
            getInputDir().getAsFileTree().getFiles().stream()
                    .filter(this::isUMLPFile)
                    .forEach(x->runOnSingleInputFile(x,inputChanges));
        }
        if(this.getInputFile().isPresent()){
            runOnSingleInputFile(this.getInputFile().get().getAsFile(),inputChanges);
        }
    }
}