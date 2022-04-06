package gradle;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.work.FileChange;
import org.gradle.work.InputChanges;
import org.gradle.workers.WorkParameters;


public interface UMLPParameter extends WorkParameters {
    RegularFileProperty getInputFile();

    DirectoryProperty getHwcDir();

    RegularFileProperty getScript();

    DirectoryProperty getOutputDir();

    ListProperty<String> getMoreArgs();
}
