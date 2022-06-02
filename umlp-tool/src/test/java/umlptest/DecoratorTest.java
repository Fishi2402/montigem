package umlptest;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeTrafo4Defaults;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import umlp.UMLPMill;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp.generator.UMLP2CDTrafo;
import umlp.generator.decorator.AbstractDecorator;

abstract public class DecoratorTest extends AbstractUMLPTest {

  protected ASTCDCompilationUnit decoratedAST;

  @BeforeAll
  public static void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
    UMLPMill.reset();
    UMLPMill.init();
  }

  @BeforeEach
  public void setup() {
    ASTUMLPCompilationUnit inputAst = parse("de/monticore/A.umlp");
    // Translate AST of UMLP Model into initial CD-AST
    ASTCDCompilationUnit cdAST = UMLP2CDTrafo.run(inputAst);

    // execute some basic CD-AST trafos (e.g., put everything in packages)
    (new CD4CodeAfterParseTrafo()).transform(cdAST);
    UMLP2CDTrafo.createCD4CodeSymTab(cdAST);
    (new CD4CodeTrafo4Defaults()).transform(cdAST);
    CDAssociationCreateFieldsFromNavigableRoles cDAssociationCreateFieldsFromNavigableRoles = new CDAssociationCreateFieldsFromNavigableRoles();
    cDAssociationCreateFieldsFromNavigableRoles.setTraverser(CD4CodeMill.traverser());
    cDAssociationCreateFieldsFromNavigableRoles.transform(cdAST);
    UMLP2CDTrafo.createCD4CodeSymTab(cdAST);

    // Create new output CompilationUnit
    decoratedAST = UMLP2CDTrafo.createCompilationUnit(cdAST);

    // Call decorator
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    CD4C.init(getGenSetup());
    AbstractDecorator decorator = getDecorator(glex);
    decorator.transform(cdAST, decoratedAST);
  }

  abstract AbstractDecorator getDecorator(GlobalExtensionManagement glex);

  protected boolean existsMethod(ASTCDType type, String methodName) {
    return type.getCDMethodList().stream()
            .filter(m -> methodName.equals(m.getName())).count()>0;
  }
}
