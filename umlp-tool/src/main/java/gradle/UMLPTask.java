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
import umlp.UMLPTool;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public abstract class UMLPTask extends DefaultTask {

    @Incremental
    @InputDirectory
    abstract DirectoryProperty getInputDir();

    @InputDirectory
    @Incremental
    abstract DirectoryProperty getHwcDir();

    @InputFile
    @Optional
    abstract RegularFileProperty getScript();

    @OutputDirectory
    abstract DirectoryProperty getOutputDir();

    protected void runOnSingleInputFile(File f){
        Log.warn("Running on file " + f.getName());
        String[] args = {"-i", f.getPath(),
                "-o", getOutputDir().getAsFile().get().getPath(),
                "-hwc", getHwcDir().getAsFile().get().getPath()
        };
        Log.warn(Arrays.toString(args));
        UMLPTool.main(args);
    }
    @TaskAction
    public void run(InputChanges inputChanges) throws IOException {
        Log.warn("Start UMLPTask");
        getInputDir().getAsFileTree().getFiles().stream()
                .filter(f -> f.getName().endsWith(".umlp"))
                .forEach(this::runOnSingleInputFile);

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