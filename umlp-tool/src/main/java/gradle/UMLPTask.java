package gradle;

import de.se_rwth.commons.logging.Log;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileType;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;
import org.gradle.work.ChangeType;
import org.gradle.work.FileChange;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;
import umlp.UMLPTool;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class UMLPTask extends DefaultTask {

    @Incremental
    @Optional
    @InputDirectory
    abstract DirectoryProperty getInputDir();

    @Optional
    @InputFile
    abstract RegularFileProperty getInputFile();

    @InputDirectory
    @Optional
    @Incremental
    abstract DirectoryProperty getHwcDir();

    @InputFile
    @Optional
    abstract RegularFileProperty getScript();

    @OutputDirectory
    abstract DirectoryProperty getOutputDir();

    @Inject
    abstract public WorkerExecutor getWorkerExecutor();

    private WorkQueue workQueue;

    protected void runOnSingleInputFile(File f){
        workQueue.submit(UMLPAction.class, param -> {
            param.getInputFile().set(f);
            param.getOutputDir().set(this.getOutputDir());
            param.getHwcDir().set(this.getHwcDir());
        });
    }

    @TaskAction
    public void run(InputChanges inputChanges) throws IOException {
        workQueue = getWorkerExecutor().classLoaderIsolation();

        Log.warn("Start UMLPTask");
        if(getInputDir().isPresent()) {
            getInputDir().getAsFileTree().getFiles().stream()
                    .filter(f -> f.getName().endsWith(".umlp"))
                    .forEach(this::runOnSingleInputFile);
        }
        if(this.getInputFile().isPresent()){
            runOnSingleInputFile(this.getInputFile().get().getAsFile());
        }


   /*         inputChanges.getFileChanges(getInputDir()).forEach(ch -> {
                if (ch.getFileType() == FileType.DIRECTORY) return;

                String fileName = ch.getFile().getName().substring(0, ch.getFile().getName().lastIndexOf('.'));
                String fileEnding = ch.getFile().getName().substring(ch.getFile().getName().lastIndexOf('.'));
                if (!fileEnding.equals(".umlp")) return;

                Provider<Directory> outDir = getOutputDir().dir(fileName);

                // TODO: Delete old Files

                // Spawn generate task
                // TODO: Run in Parallel!
                if (ch.getChangeType() == ChangeType.ADDED || ch.getChangeType() == ChangeType.MODIFIED) {
                    runOnSingleInputFile(ch.getFile());
                }
            }); */
        }
}