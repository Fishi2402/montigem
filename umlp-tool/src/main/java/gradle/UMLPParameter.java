package gradle;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.work.Incremental;
import org.gradle.workers.WorkParameters;

public interface UMLPParameter extends WorkParameters {
    RegularFileProperty getInputFile();

    DirectoryProperty getHwcDir();

    RegularFileProperty getScript();

    DirectoryProperty getOutputDir();
}
