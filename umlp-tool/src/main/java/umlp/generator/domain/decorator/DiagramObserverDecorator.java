/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DiagramObserverDecorator extends AbstractDecorator {

  public static final String SUFFIX = "Observer";

  public DiagramObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    ASTCDInterface inter = createInterface(origCD);
    astcdPackage.addCDElement(withOrigin(inter));
    astcdPackage.addCDElement(withOrigin(createEmptyImpl(origCD, inter)));
  }

  protected ASTCDInterface createInterface(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName() + SUFFIX;

    ASTCDInterface ast = UMLPMill.cDInterfaceBuilder()
            .setModifier(PUBLIC.build())
            .setName(name).build();

    // Methods
    origCD.getCDDefinition().getCDClassesList().stream()
            .filter(c -> !c.getModifier().isDerived())
            .filter(c -> !c.getModifier().isAbstract())
            .forEach(c -> {
              getCD4C().addMethod(ast, "umlp.diagram.NotifyAdd", c.getName());
              getCD4C().addMethod(ast, "umlp.diagram.NotifyDelete", c.getName());
              getCD4C().addMethod(ast, "umlp.diagram.MaybeNotifyAdd", c.getName());
              getCD4C().addMethod(ast, "umlp.diagram.MaybeNotifyDelete", c.getName());
            });

    getCD4C().addMethod(ast, "umlp.common_observer.GetScopeGlobal");
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));

    return ast;
  }

  /**
   * An empty implementation of the interface.
   * Useful in Typescript, since ts interfaces do not support default methods
   */
  protected ASTCDClass createEmptyImpl(ASTCDCompilationUnit origCD, ASTCDInterface i){
    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(i.getName()))
        .setName("Empty" + i.getName()).build();

    for (ASTCDMethod astcdMethod : i.getCDMethodList()) {
      if(astcdMethod.getName().startsWith("maybe") || astcdMethod.getName().equals("getScope")) {
        continue;
      }
      ASTCDMethod element = astcdMethod.deepClone();
      element.setIsDefault(false);
      element.getModifier().setAbstract(false);
      element.setModifier(PUBLIC.build());
      glex.replaceTemplate(GUI2ComponentTemplates.EMPTY_BODY, element, new StringHookPoint(""));
      ast.addCDMember(element);
    }

    return ast;
  }
}
