/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.Joiners;
import umlp.generator.domain.decorator.ObserverDecorator;

import java.util.ArrayList;

public class TSObserverDecorator extends ObserverDecorator {

  protected String astcdPackage;

  public TSObserverDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.generationDetails = new ArrayList<>();

    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    this.astcdPackage = astcdPackage.getName();
    for (ASTCDClass c : getNonDerivedDomainClasses(origCD)) {
      ASTCDInterface i = createInterface(c);
      /*
      ASTCDInterface mixin = createInterface(c);
      glex.replaceTemplate("umlp.gui2ts.TSInterfaceDeclaration",
          mixin, new TemplateHookPoint("umlp.gui2ts.TSInterface2Mixin"));
      /**/
      astcdPackage.addCDElement(withOrigin(i));
    }

    for (ASTCDInterface c : getNonDerivedDomainInterfaces(origCD)) {
      ASTCDInterface i = createInterface(c);
      /*
      ASTCDInterface mixin = createInterface(c);
      glex.replaceTemplate("umlp.gui2ts.TSInterfaceDeclaration",
          mixin, new TemplateHookPoint("umlp.gui2ts.TSInterface2Mixin"));
      /**/
      astcdPackage.addCDElement(withOrigin(i));
    }
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    super.addImports(domainType, cdType);

    if (domainType.isPresentSymbol()) {
      CDTypeSymbol symbol = domainType.getSymbol();
      getCD4C().addImport(cdType,
          Joiners.DOT.join(symbol.getPackageName().toLowerCase(), symbol.getName()));
    }
  }

}
