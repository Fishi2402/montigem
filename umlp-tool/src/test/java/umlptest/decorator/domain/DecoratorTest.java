/* (c) https://github.com/MontiCore/monticore */
package umlptest.decorator.domain;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeTrafo4Defaults;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.UMLSetDefaultsTrafo;
import umlptest.AbstractUMLPTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract public class DecoratorTest extends AbstractUMLPTest {

  protected ASTCDCompilationUnit decoratedAST;

  protected GeneratorSetup genSetup = getGenSetup();

  protected MCPath hwPath = new MCPath();

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

    ASTCDCompilationUnit cdAST = parse("de/monticore/A.umlp");

    // execute some basic CD-AST trafos (e.g., put everything in packages)
    (new CD4CodeAfterParseTrafo()).transform(cdAST);

    // Create scopes and symbols
    umlp._symboltable.IUMLPArtifactScope symTab = UMLPMill.scopesGenitorDelegator().createFromAST(cdAST);
    cdAST.accept(new CD4CodeSymbolTableCompleter(cdAST).getTraverser()); // run symbol table completer

    // Give default name to anonymous associations
    new UMLSetDefaultsTrafo(CD4CodeMill.traverser()).transform(cdAST);

    (new CD4CodeTrafo4Defaults()).transform(cdAST);

    // Create new output CompilationUnit
    decoratedAST = createCompilationUnit(cdAST);

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

  protected ASTCDCompilationUnit createCompilationUnit(ASTCDCompilationUnit cd) {
    // create an empty CD definition
    ASTCDDefinition cdDef = CD4CodeMill.cDDefinitionBuilder()
            .setName(cd.getCDDefinition().getName())
            .setModifier(CDModifier.PUBLIC.build())
            .build();

    // ... and add it to a new CD compilation unit
    ASTCDCompilationUnit newCD = CD4CodeMill.cDCompilationUnitBuilder()
            .setCDDefinition(cdDef)
            .build();

    // set the package
    if (cd.isPresentMCPackageDeclaration()) {
      newCD.setMCPackageDeclaration(cd.getMCPackageDeclaration());
    }

    return newCD;
  }

  protected void existsMethod(ASTCDType type, String methodName) {
    assertTrue(type.getCDMethodList().stream()
            .filter(m -> methodName.equals(m.getName())).count()>0, "method " + methodName + " exists");
  }

  protected void existsAttribute(ASTCDType type, String attributeName) {
    assertTrue(type.getCDAttributeList().stream()
            .filter(m -> attributeName.equals(m.getName())).count()>0, "attribute " + attributeName + " exists");
  }

  protected void hasSuperClass(ASTCDClass ast, String superClassName) {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter();
    assertTrue(ast.isPresentCDExtendUsage() && ast.getCDExtendUsage().getSuperclassList().stream()
            .filter(m -> superClassName.equals(pp.prettyprint(m))).count()>0,
            "superclass " + superClassName + " exists");
  }

  protected void hasSuperInterface(ASTCDClass ast, String superInterfaceName) {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter();
    assertTrue(ast.isPresentCDInterfaceUsage() && ast.getCDInterfaceUsage().getInterfaceList().stream()
                    .filter(m -> superInterfaceName.equals(pp.prettyprint(m))).count()>0,
            "superinterface " + superInterfaceName + " exists");
  }

  protected void hasSuperInterface(ASTCDInterface ast, String superInterfaceName) {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter();
    assertTrue(ast.isPresentCDExtendUsage() && ast.getCDExtendUsage().getSuperclassList().stream()
                    .filter(m -> superInterfaceName.equals(pp.prettyprint(m))).count()>0,
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
