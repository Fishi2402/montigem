package umlp.generator.domain.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.types.MCTypeFacade;

public class SD2TestDecorator {

  private final GlobalExtensionManagement glex;

  public SD2TestDecorator(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  public void transform(ASTSDArtifact sdAst, ASTCDDefinition domainCd, ASTCDCompilationUnit decoratedCD) {
    var name = sdAst.getSequenceDiagram().getName();
    var targetPackage = getPackage(name.toLowerCase(), decoratedCD);
    targetPackage.addCDElement(createTest(sdAst, domainCd));
  }

  private ASTCDElement createTest(ASTSDArtifact model, ASTCDDefinition domainCd) {
    var name = model.getSequenceDiagram().getName();

    var testClass = CD4CodeBasisMill.cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(name)
        .build();

    //TODO we need some info about the domain CD here, like name and package; How do we get that?

    //TODO implement
    return testClass;
  }

  private ASTCDPackage getPackage(String packageName, ASTCDCompilationUnit decoratedCD) {
    var p = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(MCTypeFacade.getInstance().createQualifiedName(packageName))
        .build();
    decoratedCD.getCDDefinition().addCDElement(p);
    return p;
  }
}
