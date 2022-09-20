package umlp.generator;

import de.monticore.cd.facade.*;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;

import java.util.Locale;
import java.util.Optional;

// TODO refactor this class
public abstract class AbstractDecorator {

  public AbstractDecorator(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  public static final String DEFAULT_PACKAGE = "";
  
  public static final String COMMAND_PACKAGE = DEFAULT_PACKAGE+"commands";
  
  public static final String CLIENT_PACKAGE = DEFAULT_PACKAGE+"client";

  protected final GlobalExtensionManagement glex;

  protected final CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  
  protected TypeCheck typeCheck;

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
  
  public TypeCheck getTypeCheck() {
    return typeCheck;
  }
  
  public void setTypeCheck(TypeCheck typeCheck) {
    this.typeCheck = typeCheck;
  }
  
  protected ASTCDPackage getPackage(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD, String packageName) {
    String completeName = getPackageName(origCD, packageName);
    Optional<ASTCDPackage> astCDPackage = decoratedCD.getCDDefinition().getCDPackagesList().stream()
            .filter(p -> p.getName().equals(completeName)).findFirst();
    if (astCDPackage.isPresent()) {
      return astCDPackage.get();
    } else {
      ASTCDPackage p = CD4CodeMill.cDPackageBuilder()
              .setMCQualifiedName(getMCTypeFacade().createQualifiedName(completeName)).build();
      decoratedCD.getCDDefinition().addCDElement(p);
      return p;
    }
  }

  protected String getPackageName(ASTCDCompilationUnit origCD, String subPackage) {
    String origPackage = Names.constructQualifiedName(origCD.getCDPackageList(), origCD.getCDDefinition().getName());
    return (subPackage.isEmpty()?origPackage:Joiners.DOT.join(origPackage, subPackage)).toLowerCase();
  }

  abstract public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD);

}
