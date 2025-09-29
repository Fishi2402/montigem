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
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.lang.sd4development.SD4DevelopmentMill;
import de.monticore.lang.sd4development._visitor.SD4DevelopmentVisitor2;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.lang.sdbasis._visitor.SDBasisVisitor2;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.visitor.LoggingSDVisitor;
import umlp.generator.domain.visitor.SDVisitor;
import umlp.generator.domain.visitor.model.ActivityBar;
import umlp.generator.domain.visitor.model.SDTest;
import umlp.generator.domain.visitor.model.interaction.ConstructorCallInteraction;
import umlp.generator.domain.visitor.model.interaction.Interaction;
import umlp.generator.domain.visitor.model.interaction.InteractionVariantMatcher;
import umlp.generator.domain.visitor.model.interaction.MethodCallInteraction;
import umlp.generator.domain.visitor.model.ocl.LetStatement;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;
import umlp.generator.domain.visitor.model.ocl.OCLStatementVariantMatcher;
import umlp.generator.domain.visitor.state.FinishedState;
import umlp.generator.domain.visitor.state.VisitorState;
import umlp.generator.domain.visitor.state.VisitorStateVariantMatcher;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SD2TestDecorator {

  private final GlobalExtensionManagement glex;

  public SD2TestDecorator(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  public void transform(ASTSDArtifact sdAst,
                        ASTCDDefinition domainCd,
                        ASTCDCompilationUnit decoratedCD) {
    var name = sdAst.getSequenceDiagram().getName();
    var targetPackage = getPackage(name.toLowerCase(), decoratedCD);
    targetPackage.addCDElement(createTest(sdAst, domainCd));
  }

  private ASTCDElement createTest(ASTSDArtifact model, ASTCDDefinition domainCd) {
    logModel(model); //TODO currently here for debug purposes

    var diagramName = model.getSequenceDiagram().getName();
    var domainName = domainCd.getName();
    var domainPackageName = domainName.toLowerCase();

    var testClass = CD4CodeBasisMill
        .cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(diagramName)
        .setCDExtendUsage(CDExtendUsageFacade
                              .getInstance()
                              .createCDExtendUsage(domainName + "SQLDBTestSetup"))
        .build();

    var cd4c = CD4C.getInstance();

    //constructor
    cd4c.addConstructor(testClass, "umlp.domain.sd2test.Constructor", diagramName);

    //override initBackendManager
    cd4c.addMethod(testClass, "umlp.domain.sd2test.InitBackendManager", domainName);

    //TODO figure out which objects have been declared in a corresponding object diagram

    //apply a visitor to the model
    var visitor = new SDVisitor();
    visit(model, visitor);

    var sdTest = visitor.currentState().matchVariant(new VisitorStateVariantMatcher<SDTest>() {
      @Override
      public SDTest whenFinished(FinishedState finished) {
        return finished.extractTest();
      }

      @Override
      public SDTest otherwise(VisitorState state) {
        var message = "SDVisitor didn't end in FinishedState, but instead in " + state;
        Log.error(message);
        throw new IllegalStateException(message);
      }
    });

    Log.info(sdTest.toString(), "SD2TestDecorator"); //TODO here for debug purposes

    //copy import statements of the diagram
    for (var importStatement : sdTest.imports()) {
      var signature = createJavaSignature(importStatement);
      cd4c.addImport(testClass, signature);
    }

    //TODO create (final?) attributes for objects and respective init methods
    //TODO create simple statements for pre interaction ocl statements
    //TODO create load- and check statements for every trigger
    //TODO create attributes for ocl let expressions

    //create methods for objects declared in the test
    var objectNames = new ArrayList<String>();
    for (var sdObject : sdTest.sdObjects()) {
      var name = sdObject.name();
      objectNames.add(name);
      cd4c.addMethod(testClass,
                     "umlp.domain.sd2test.CreateObject",
                     name,
                     sdObject.qualifiedTypeName(),
                     domainName);
    }

    var statements = List.<String>of();
    var testMethod = cd4c.addMethod(testClass, "umlp.domain.sd2test.Test", objectNames, statements);
    glex.replaceTemplate("cd2java.Annotations", testMethod, new StringHookPoint("@Test"));

    //other imports
    cd4c.addImport(testClass, Joiners.DOT.join(domainPackageName, "test_infrastructure", "*"));
    cd4c.addImport(testClass, Joiners.DOT.join(PathUtil.getTestInfrastructurePath(), "*"));
    cd4c.addImport(testClass, "org.junit.jupiter.api.Test");

    return testClass;
  }

  private Collection<Variable> collectDeclaredVariables(Collection<Interaction> interactions) {
    //variable declarations may occur in constructor calls and OCL variable assignments
    //OCL can occur after every interaction and activity bar
    //further interactions may occur within an activity bar

    return interactions.stream().flatMap(interaction -> {
      var oclStream = interaction
          .oclStatements()
          .stream()
          .flatMap(oclStatement -> getVariable(oclStatement).stream());
      var interactionSpecificStream =
          interaction.matchVariant(new InteractionVariantMatcher<Stream<Variable>>() {
        @Override
        public Stream<Variable> whenMethodCall(MethodCallInteraction methodCall) {
          return toVariableStream(methodCall.activityBar().stream());
        }

        @Override
        public Stream<Variable> whenConstructorCall(ConstructorCallInteraction constructorCall) {
          var objectStream = Stream.of(new Variable(constructorCall.objectName(),
                                                    constructorCall.declarationTypeName()));
          var activityBarStream = toVariableStream(constructorCall.activityBar().stream());
          return Stream.concat(objectStream, activityBarStream);
        }

        @Override
        public Stream<Variable> otherwise(Interaction interaction) {
          return Stream.empty();
        }
      });
      return Stream.concat(oclStream, interactionSpecificStream);
    }).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private Stream<Variable> toVariableStream(Stream<ActivityBar> activityBarStream) {
    return activityBarStream.flatMap(activityBar -> {
      var oclStream = activityBar
          .oclStatements()
          .stream()
          .flatMap(oclStatement -> getVariable(oclStatement).stream());
      var interactionStream = collectDeclaredVariables(activityBar.body()).stream();
      return Stream.concat(oclStream, interactionStream);
    });
  }

  private static Optional<Variable> getVariable(OCLStatement oclStatement) {
    return oclStatement.matchVariant(new OCLStatementVariantMatcher<>() {
      @Override
      public Optional<Variable> whenLetStatement(LetStatement letStatement) {
        return Optional.of(new Variable(letStatement.name(), letStatement.qualifiedTypeName()));
      }

      @Override
      public Optional<Variable> otherwise(OCLStatement statement) {
        return Optional.empty();
      }
    });
  }

  private static String createJavaSignature(ASTMCImportStatement importStatement) {
    var parts = new ArrayList<>(importStatement.getMCQualifiedName().getPartsList());
    if (importStatement.isStar()) {
      parts.add("*");
    }

    /* In an umlp diagram, the package name starts with an upper case letter, but the java generator
     * creates fully lowercase package names
     * -> lowercase the pre-last part of an import
     * */
    var partsSize = parts.size();
    if (partsSize > 1) {
      var preLastIndex = partsSize - 2;
      parts.set(preLastIndex, parts.get(preLastIndex).toLowerCase());
    }

    return Names.constructQualifiedName(parts);
  }

  private static void logModel(ASTSDArtifact model) {
    var visitor = new LoggingSDVisitor();
    visit(model, visitor);
    visitor.visits().forEach(visit -> Log.info(visit, "SD2TestDecorator.Visit"));
  }

  private static <T extends SDBasisVisitor2 & SD4DevelopmentVisitor2 & MCBasicTypesVisitor2> void visit(
      ASTSDArtifact model,
      T visitor) {
    var traverser = SD4DevelopmentMill.traverser();
    traverser.add4SDBasis(visitor);
    traverser.add4SD4Development(visitor);
    traverser.add4MCBasicTypes(visitor);
    model.accept(traverser);
  }

  private ASTCDPackage getPackage(String packageName, ASTCDCompilationUnit decoratedCD) {
    var p = CD4CodeMill
        .cDPackageBuilder()
        .setMCQualifiedName(MCTypeFacade.getInstance().createQualifiedName(packageName))
        .build();
    decoratedCD.getCDDefinition().addCDElement(p);
    return p;
  }

  private static final class Variable {
    private final String name;
    private final String type;

    public Variable(String name, String type) {
      this.name = name;
      this.type = type;
    }

    public String name() {
      return name;
    }

    public String type() {
      return type;
    }
  }
}
