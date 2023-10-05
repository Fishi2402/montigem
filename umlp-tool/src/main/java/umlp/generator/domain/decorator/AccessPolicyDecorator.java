/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class AccessPolicyDecorator extends AbstractDecorator {

  public static final String SUFFIX = "AccessPolicy";

  public AccessPolicyDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
        .forEach(c -> astcdPackage.addCDElement(withOrigin(createClass(origCD, c))));
    origCD.getCDDefinition().getCDInterfacesList().stream().filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c -> astcdPackage.addCDElement(withOrigin(createClass(origCD, c))));
  }

  protected ASTCDElement createClass(ASTCDCompilationUnit origCD, ASTCDType domainClass) {
    String className = domainClass.getName();

    ASTCDClass policyClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(className + SUFFIX)
        .build();
    createCheckPolicy(policyClass);
    getCD4C().addImport(policyClass, PathUtil.getBackendRTECommandPath("Command"));
    return policyClass;
  }

  protected ASTCDMethodSignature createCheckPolicy(ASTCDType policyClass) {
    return getCD4C().addMethod(policyClass, getCheckPermissionTemplate());
  }

  protected String getCheckPermissionTemplate() {
    return "umlp.accesspolicy.CheckPermission";
  }
}
