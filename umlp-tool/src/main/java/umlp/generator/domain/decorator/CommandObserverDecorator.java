/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class CommandObserverDecorator extends CommandSendingObserverDecorator {

  public static final String SUFFIX = "CommandObserver";

  public CommandObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getSuffix() {
    return SUFFIX;
  }

  @Override
  protected String setAttributeTmplt() {
    return "umlp.commandobserver.NotifySetAttribute";
  }

  @Override
  protected String removeAttributeTmplt() {
    return "umlp.commandobserver.NotifyRemoveAttribute";
  }

  @Override
  protected String addAttributeTmplt() {
    return "umlp.commandobserver.NotifyAddAttribute";
  }

  @Override
  protected String scopeTmplt() {
    return "umlp.commandobserver.GetScope";
  }
}
