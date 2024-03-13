/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.ast.Comment;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;

/**
 * The derived-Impl class should be extended using the TOP mechanism
 * and implement the various getters
 */
public class DerivedClassImplDecorator extends AbstractDecorator {

  public DerivedClassImplDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    getDerivedDomainClasses(origCD).forEach(c -> astcdPackage.addCDElement(withOrigin(createClass(c))));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass) {
    String classname = domainClass.getName() + "Impl";

    ASTCDClass ast = UMLPMill.cDClassBuilder().setModifier(PUBLIC_ABSTRACT.build())
      .setName(classname).build();

    ast.add_PreComment(new Comment("/* Implement the methods via the TOP mechanism */"));

    if (!ast.isPresentCDExtendUsage())
      ast.setCDExtendUsage(UMLPMill.cDExtendUsageBuilder().build());

    ast.getSuperclassList().add(getMCTypeFacade().createQualifiedType(domainClass.getName()));

    addImports(domainClass, ast);

    return ast;
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
  }
}
