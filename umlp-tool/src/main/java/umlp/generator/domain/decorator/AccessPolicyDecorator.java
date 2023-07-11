/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

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

  }

  protected ASTCDElement createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass) {
    String className = domainClass.getName();

    ASTCDClass policyClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(className + SUFFIX)
        .addCDMember(createCheckPolicy(domainClass)).build();
    return policyClass;
  }

  protected ASTCDMethodSignature createCheckPolicy(ASTCDClass domainClass) {
    return getCD4C().addMethod(domainClass, "umlp.accesspolicy.CheckPermission");
  }


}
