package sovd_gateway_module;

import de.monticore.cd.facade.CDTypeFacade;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

public class SvodGatewayDecorator extends AbstractDecorator {
  public SvodGatewayDecorator(GlobalExtensionManagement glex){
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    System.out.println("Hello world for decorator! Got cd \n" + UMLPMill.prettyPrint(origCD, true));

    ASTCDPackage pack = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    pack.addCDElement(withOrigin(createClass()));
  }

  private ASTCDElement createClass() {
    return CDTypeFacade.getInstance().createCDClass("Test");
  }
}
