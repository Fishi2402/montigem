package umlptest.decorator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeTrafo4Defaults;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import umlp.UMLPMill;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp.generator.domain.UMLP2CDTrafo;
import umlp.generator.domain.decorator.AbstractDecorator;
import umlptest.AbstractUMLPTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract public class DecoratorTest extends AbstractUMLPTest {

  protected ASTCDCompilationUnit decoratedAST;

  protected GeneratorSetup genSetup = getGenSetup();

  @BeforeAll
  public static void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void setup() {
    UMLPMill.reset();
    UMLPMill.init();
    BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope());

    ASTUMLPCompilationUnit inputAst = parse("de/monticore/A.umlp");
    // Translate AST of UMLP Model into initial CD-AST
    ASTCDCompilationUnit cdAST = UMLP2CDTrafo.run(inputAst);

    // execute some basic CD-AST trafos (e.g., put everything in packages)
    (new CD4CodeAfterParseTrafo()).transform(cdAST);
    UMLP2CDTrafo.createCD4CodeSymTab(cdAST);
    (new CD4CodeTrafo4Defaults()).transform(cdAST);
    UMLP2CDTrafo.createCD4CodeSymTab(cdAST);

    // Create new output CompilationUnit
    decoratedAST = UMLP2CDTrafo.createCompilationUnit(cdAST);

    // Call decorator
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    genSetup.setGlex(glex);
    CD4C.init(genSetup);
    CD4C.getInstance().setEmptyBodyTemplate("cd2java.EmptyBody");
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    AbstractDecorator decorator = getDecorator(glex);
    decorator.transform(cdAST, decoratedAST);
  }

  protected abstract AbstractDecorator getDecorator(GlobalExtensionManagement glex);

  protected void existsMethod(ASTCDType type, String methodName) {
    assertTrue(type.getCDMethodList().stream()
            .filter(m -> methodName.equals(m.getName())).count()>0, "method " + methodName + " exists");
  }

  protected void existsAttribute(ASTCDType type, String attributeName) {
    assertTrue(type.getCDAttributeList().stream()
            .filter(m -> attributeName.equals(m.getName())).count()>0, "attribute " + attributeName + " exists");
  }

  protected void hasSuperClass(ASTCDClass ast, String superClassName) {
    assertTrue(ast.isPresentCDExtendUsage() && ast.getCDExtendUsage().getSuperclassList().stream()
            .filter(m -> superClassName.equals(m.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())))).count()>0,
            "superclass " + superClassName + " exists");
  }

  protected void hasSuperInterface(ASTCDClass ast, String superInterfaceName) {
    assertTrue(ast.isPresentCDInterfaceUsage() && ast.getCDInterfaceUsage().getInterfaceList().stream()
                    .filter(m -> superInterfaceName.equals(m.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())))).count()>0,
            "superinterface " + superInterfaceName + " exists");
  }

  protected void hasSuperInterface(ASTCDInterface ast, String superInterfaceName) {
    assertTrue(ast.isPresentCDExtendUsage() && ast.getCDExtendUsage().getSuperclassList().stream()
                    .filter(m -> superInterfaceName.equals(m.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())))).count()>0,
            "superinterface " + superInterfaceName + " exists");
  }

  protected ASTCDClass getClassByName(String name) {
    Optional<ASTCDClass> type = decoratedAST.getCDDefinition().getCDClassesList().stream()
            .filter(c -> name.equals(c.getName())).findFirst();
    assertTrue(type.isPresent());
    return type.get();
  }

  protected ASTCDInterface getInterfaceByName(String name) {
    Optional<ASTCDInterface> type = decoratedAST.getCDDefinition().getCDInterfacesList().stream()
            .filter(c -> name.equals(c.getName())).findFirst();
    assertTrue(type.isPresent());
    return type.get();
  }
  protected void generateAndCheckCode(ASTCDType type) {
    // Check existing package
    assertFalse(decoratedAST.getCDDefinition().getCDPackagesList().isEmpty());

    // Generate Code
    StringBuilder sb;
    GeneratorEngine generatorEngine = new GeneratorEngine(genSetup);
    if (type instanceof ASTCDClass) {
      sb = generatorEngine.generate(CD2JavaTemplates.CLASS, type, decoratedAST.getCDDefinition().getCDPackagesList().get(0));
    } else if (type instanceof ASTCDInterface) {
      sb = generatorEngine.generate(CD2JavaTemplates.INTERFACE, type, decoratedAST.getCDDefinition().getCDPackagesList().get(0));
    } else {
      sb = new StringBuilder();
    }

    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
