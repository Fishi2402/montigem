/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.Joiners;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ObserverDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TSObserverMixinDecorator extends TSObserverDecorator {

  public static final String SUFFIX = "Mixin";

  public TSObserverMixinDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.generationDetails = new ArrayList<>();

    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    this.astcdPackage = astcdPackage.getName();
    for (ASTCDClass c : getNonDerivedDomainClasses(origCD)) {
      ASTCDInterface i = createObserverInterface(c);
      interface2Mixin(i);
      astcdPackage.addCDElement(withOrigin(i));
    }

    for (ASTCDInterface c : getNonDerivedDomainInterfaces(origCD)) {
      ASTCDInterface i = createObserverInterface(c);
      interface2Mixin(i);
      astcdPackage.addCDElement(withOrigin(i));
    }
  }

  protected void interface2Mixin(ASTCDInterface i) {
    i.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder()
        .setSuperclassList(List.of(MCTypeFacade.getInstance()
            .createQualifiedType(i.getName()))).build());
    i.setName(i.getName() + SUFFIX);
    glex.replaceTemplate("umlp.gui2ts.TSInterfaceDeclaration",
        i, new TemplateHookPoint("umlp.gui2ts.TSInterface2Mixin"));
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    super.addImports(domainType, cdType);

    String obs = domainType.getName() + ObserverDecorator.SUFFIX;
    getCD4C().addImport(cdType, astcdPackage + "." + obs);
  }

}
