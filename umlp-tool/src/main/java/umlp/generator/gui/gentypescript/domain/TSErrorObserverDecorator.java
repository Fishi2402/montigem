/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.ErrorObserverDecorator;

public class TSErrorObserverDecorator extends ErrorObserverDecorator {

  protected String astcdPackage;

  public TSErrorObserverDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    getCD4C().addImport(cdType, this.astcdPackage + "." + domainType.getName());
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    super.transform(origCD, decoratedCD);
  }

  @Override
  protected String getNotifyInvalidTemplate() {
    return "umlp.gui2ts.errorobserver.TSNotifyInvalid";
  }
}
