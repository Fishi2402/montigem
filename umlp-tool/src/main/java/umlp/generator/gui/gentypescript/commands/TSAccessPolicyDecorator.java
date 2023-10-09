package umlp.generator.gui.gentypescript.commands;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.AccessPolicyDecorator;

import java.util.Collection;
import java.util.List;

public class TSAccessPolicyDecorator extends AccessPolicyDecorator {
  public TSAccessPolicyDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getCheckPermissionTemplate() {
    return "umlp.gui2ts.accesspolicy.TSCheckPermission";
  }

}
