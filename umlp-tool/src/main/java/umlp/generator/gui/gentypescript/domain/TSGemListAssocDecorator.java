/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.GemListAssocDecorator;

public class TSGemListAssocDecorator extends GemListAssocDecorator {

  public TSGemListAssocDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  protected String getConstructorEmptyTemplate() {
    return "umlp.gui2ts.gemlist.assocs.ConstructorEmpty";
  }

  protected String getConstructorTemplate() {
    return "umlp.gui2ts.gemlist.assocs.Constructor";
  }

  protected String getInternalRemoveTemplate() {
    return "umlp.gui2ts.gemlist.assocs.InternalRemove";
  }

  protected String getInternalSetTemplate() {
    return "umlp.gui2ts.gemlist.assocs.InternalSet";
  }

  protected String getInternalAddTemplate() {
    return "umlp.gui2ts.gemlist.assocs.InternalAdd";
  }

  protected String getGetOwnerTemplate() {
    return "umlp.gui2ts.gemlist.assocs.GetOwner";
  }

  protected String getLoadObjectsTemplate() {
    return "umlp.gui2ts.gemlist.LoadObjects";
  }

}
