package umlp.generator.domain.decorator.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DomainTypeFilterDecorator extends AbstractDecorator {
  public static String SUFFIX = "Filter";

  public DomainTypeFilterDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c -> astcdPackage.addCDElement(withOrigin(createClass(origCD, c, astcdPackage))));
  }

  protected ASTCDElement createClass(ASTCDCompilationUnit origCD, ASTCDClass c, ASTCDPackage pack) {
    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage("DomainTypeFilter"))
        .setName(c.getName()+SUFFIX)
        .build();

    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("DomainTypeFilter"));

    getCD4C().addMethod(ast, "umlp.commands.domainTypeFilter.Test", c.getName(), pack.getName());
    return ast;
  }
}
