package umlp.generator.domain.decorator;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.Joiners;
import umlp.generator.util.PathUtil;

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
    var domainName = domainCd.getName();
    var domainPackageName = domainName.toLowerCase();

    var testClass = CD4CodeBasisMill.cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(name)
        .setCDExtendUsage(CDExtendUsageFacade.getInstance().createCDExtendUsage(domainName + "SQLDBTestSetup"))
        .build();

    var cd4c = CD4C.getInstance();

    //constructor
    cd4c.addConstructor(testClass, "umlp.domain.sd2test.Constructor", name);

    //override initBackendManager
    cd4c.addMethod(testClass, "umlp.domain.sd2test.InitBackendManager", domainName);

    //TODO create test method

    //imports
    cd4c.addImport(testClass, Joiners.DOT.join(domainPackageName, "*"));
    cd4c.addImport(testClass, Joiners.DOT.join(domainPackageName, "test_infrastructure", "*"));
    cd4c.addImport(testClass, Joiners.DOT.join(PathUtil.getTestInfrastructurePath(), "*"));

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
