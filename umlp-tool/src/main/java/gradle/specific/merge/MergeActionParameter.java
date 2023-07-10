/* (c) https://github.com/MontiCore/monticore */
package gradle.specific.merge;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.workers.WorkParameters;

import java.nio.file.Path;


public interface MergeActionParameter extends WorkParameters {
  ConfigurableFileCollection getInputs();

  RegularFileProperty getOutputFile();
}
