package umlp.generator.gui.gentypescript.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class GUIImportHelperMock extends GUIImportHelper {

  public GUIImportHelperMock() {
    super(null, null, null);
  }

  public GUIImportHelperMock(GlobalExtensionManagement glex, ASTCDClass clazz, String importerPackage) {
    super(glex, clazz, importerPackage);
  }

  @Override
  public void addImport(String qType) {
  }

  @Override
  public void addImportString(String importStr) {
  }

}
