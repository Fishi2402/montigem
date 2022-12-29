/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DiagramObserverDecorator extends AbstractDecorator {

  public static final String SUFFIX = "Observer";

  public DiagramObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(createInterface(origCD));
  }

  protected ASTCDInterface createInterface(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName() + SUFFIX;

    ASTCDInterface ast = UMLPMill.cDInterfaceBuilder()
            .setModifier(PUBLIC.build())
            .setName(name).build();

    // Methods
    origCD.getCDDefinition().getCDClassesList().stream()
            .filter(c -> !c.getModifier().isDerived())
            .forEach(c -> {
              getCD4C().addMethod(ast, "umlp.diagram.NotifyAdd", c.getName());
              getCD4C().addMethod(ast, "umlp.diagram.NotifyDelete", c.getName());
            });

    return ast;
  }
}
