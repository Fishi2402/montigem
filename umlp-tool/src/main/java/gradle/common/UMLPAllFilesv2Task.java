/* (c) https://github.com/MontiCore/monticore */
package gradle.common;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.common.MCAllFilesTask;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.gradle.api.file.Directory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.InputChanges;
import umlp.UMLPToolOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

abstract public class UMLPAllFilesv2Task extends MCAllFilesTask {

  public UMLPAllFilesv2Task(String type) {
    super(type, null);
  }

  @Override
  protected Consumer<String[]> getRunMethod() {
    return UMLPToolInvoker::run;
  }

  @Override
  protected Class<? extends AToolAction> getToolAction() {
    return UMLPActionv2.class;
  }
}
