package umlp.generator.decorator;

import de.monticore.cd.facade.*;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.MCTypeFacade;

import java.util.Optional;

// TODO refactor this class
public abstract class AbstractDecorator {

  public AbstractDecorator(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  public static final String DEFAULT_PACKAGE = "umlp";
  
  public static final String COMMAND_PACKAGE = DEFAULT_PACKAGE+".commands";
  
  public static final String CLIENT_PACKAGE = DEFAULT_PACKAGE+".client";

  protected final GlobalExtensionManagement glex;

  protected final CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter());

  protected GlobalExtensionManagement getGlex() {
    return glex;
  }

  protected MCTypeFacade getMCTypeFacade() {
    return MCTypeFacade.getInstance();
  }

  protected CDAttributeFacade getCDAttributeFacade() {
    return CDAttributeFacade.getInstance();
  }

  protected CDConstructorFacade getCDConstructorFacade() {
    return CDConstructorFacade.getInstance();
  }

  protected CDMethodFacade getCDMethodFacade() {
    return CDMethodFacade.getInstance();
  }

  protected CDParameterFacade getCDParameterFacade() {
    return CDParameterFacade.getInstance();
  }

  protected CDExtendUsageFacade getExtendUsageFacade() {
    return CDExtendUsageFacade.getInstance();
  }

  protected CDInterfaceUsageFacade getInterfaceUsageFacade() {
    return CDInterfaceUsageFacade.getInstance();
  }

  protected CDStereotypeFacade getStereotypeFacade() {
    return CDStereotypeFacade.getInstance();
  }

  protected CD4C getCD4C() {
    return CD4C.getInstance();
  }

  protected CD4CodeFullPrettyPrinter getCDPrinter (){
    return printer;
  }

  protected ASTCDPackage getPackage(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD, String packageName) {
    Optional<ASTCDPackage> astCDPackage = decoratedCD.getCDDefinition().getCDPackagesList().stream()
            .filter(p -> p.getName().equals(packageName)).findFirst();
    if (astCDPackage.isPresent()) {
      return astCDPackage.get();
    } else {
      ASTCDPackage p = CD4CodeMill.cDPackageBuilder()
              .setMCQualifiedName(getMCTypeFacade().createQualifiedName(packageName)).build();
      decoratedCD.getCDDefinition().addCDElement(p);
      return p;
    }
  }

  abstract public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD);

}
