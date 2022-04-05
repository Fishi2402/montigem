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

    protected void runOnSingleInputFile(File f){
        Log.warn("Running on file " + f.getName());
        List<String> args = new ArrayList<>();
        args.add("-i");
        args.add(f.getPath());

        args.add("-o");
        args.add(getOutputDir().getAsFile().get().getPath());

        if(getHwcDir().isPresent()){
            args.add("-hwc");
            args.add(getHwcDir().getAsFileTree().getAsPath());
        }

        String[] arrayArgs = args.toArray(new String[0]);

        Log.warn(arrayArgs[0]);
        Log.warn(arrayArgs[1]);
        Log.warn(Arrays.toString(arrayArgs));

        UMLPTool.main(arrayArgs);
    }
    @TaskAction
    public void run(InputChanges inputChanges) throws IOException {
        Log.warn("Start UMLPTask");
        if(getInputDir().isPresent()) {
            getInputDir().getAsFileTree().getFiles().stream()
                    .filter(f -> f.getName().endsWith(".umlp"))
                    .forEach(this::runOnSingleInputFile);
        }
        if(getInputFile().isPresent()){
            runOnSingleInputFile(getInputFile().get().getAsFile());
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