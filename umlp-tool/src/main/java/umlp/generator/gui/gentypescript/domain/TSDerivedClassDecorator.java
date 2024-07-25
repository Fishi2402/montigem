/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.DerivedClassDecorator;
import umlp.generator.util.UMLPSymbolTables;

//Derived classes are no longer supported
@Deprecated
public class TSDerivedClassDecorator extends DerivedClassDecorator {

  protected String astcdPackage;
  protected ASTCDCompilationUnit origCD;

  public TSDerivedClassDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    this.origCD = origCD;
    super.transform(origCD, decoratedCD);
  }

  @Override
  protected String getGetTemplate() {
    return "umlp.gui2ts.derivedclass.TSGet";
  }

  @Override
  protected String getSizeTemplate() {
    return "umlp.gui2ts.derivedclass.TSSize";
  }

  @Override
  protected String getCalcTemplate() {
    return "umlp.gui2ts.derivedclass.TSCalc";
  }

  @Override
  protected String getCalcSizeTemplate() {
    return "umlp.gui2ts.derivedclass.TSCalcSize";
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    if (!domainType.getSuperclassList().isEmpty()) {
      ASTCDCompilationUnit cdOfSuper = UMLPSymbolTables.getDiagramFor(domainType.getSuperclassList().get(0));
      getCD4C().addImport(cdType, getPackageName(cdOfSuper, DEFAULT_PACKAGE) + "." + domainType.getSuperclassList().get(0).printType());
    }
    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      if (!c.getName().equals(domainType.getName())) {
        getCD4C().addImport(cdType, this.astcdPackage + "." + c.getName());
      }
    }
    for (ASTCDEnum c : this.origCD.getCDDefinition().getCDEnumsList()) {
      if (!c.getName().equals(domainType.getName())) {
        getCD4C().addImport(cdType, this.astcdPackage + "." + c.getName());
      }
    }
    for (ASTCDInterface c : this.origCD.getCDDefinition().getCDInterfacesList()) {
      if (!c.getName().equals(domainType.getName())) {
        ASTCDCompilationUnit cdOfSuper = UMLPSymbolTables.getDiagramFor(c);
        getCD4C().addImport(cdType, getPackageName(cdOfSuper, DEFAULT_PACKAGE) + "." + c.getName());
      }
    }
  }

}
