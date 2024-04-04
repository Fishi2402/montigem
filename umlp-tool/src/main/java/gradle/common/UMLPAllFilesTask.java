/* (c) https://github.com/MontiCore/monticore */
package gradle.common;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.common.MCAllFilesTask;

import java.util.function.Consumer;

abstract public class UMLPAllFilesTask extends MCAllFilesTask {

  public UMLPAllFilesTask(String type) {
    super(type, null);
  }

  @Override
  protected Consumer<String[]> getRunMethod() {
    return UMLPToolInvoker::run;
  }

  @Override
  protected Class<? extends AToolAction> getToolAction() {
    return UMLPAction.class;
  }
}
