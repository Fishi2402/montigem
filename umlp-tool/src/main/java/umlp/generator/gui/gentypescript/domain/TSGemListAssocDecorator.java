/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.GemListAssocDecorator;

public class TSGemListAssocDecorator extends GemListAssocDecorator {

  public TSGemListAssocDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  protected String getConstructorEmptyTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSConstructorEmpty";
  }

  protected String getConstructorTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSConstructor";
  }

  protected String getInternalRemoveTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSInternalRemove";
  }

  protected String getInternalSetTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSInternalSet";
  }

  protected String getInternalAddTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSInternalAdd";
  }

  protected String getGetOwnerTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSGetOwner";
  }

  protected String getLoadObjectsTemplate() {
    return "umlp.gui2ts.gemlist.TSLoadObjects";
  }

}
