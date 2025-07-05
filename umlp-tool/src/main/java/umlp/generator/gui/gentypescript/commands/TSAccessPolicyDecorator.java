package umlp.generator.gui.gentypescript.commands;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import umlp.generator.domain.decorator.AccessPolicyDecorator;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class TSAccessPolicyDecorator extends AccessPolicyDecorator {
  protected final Path outPath;
  protected final MCPath hwPath;

  public TSAccessPolicyDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    super(glex);
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  @Override
  protected String getCheckPermissionTemplate() {
    return "umlp.gui2ts.accesspolicy.TSCheckPermission";
  }

}
