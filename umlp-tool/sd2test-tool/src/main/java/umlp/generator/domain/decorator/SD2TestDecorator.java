package umlp.generator.domain.decorator;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCDClass;
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
import umlp.generator.domain.visitor.model.MatchingStrategy;
import umlp.generator.domain.visitor.model.SDObject;
import umlp.generator.domain.visitor.model.SDTest;
import umlp.generator.domain.visitor.model.Trigger;
import umlp.generator.domain.visitor.model.interaction.Actor;
import umlp.generator.domain.visitor.model.interaction.ConstructorCallInteraction;
import umlp.generator.domain.visitor.model.interaction.ExhaustiveInteractionVariantMatcher;
import umlp.generator.domain.visitor.model.interaction.Interaction;
import umlp.generator.domain.visitor.model.interaction.InteractionVariantMatcher;
import umlp.generator.domain.visitor.model.interaction.MethodCallInteraction;
import umlp.generator.domain.visitor.model.interaction.ReturnInteraction;
import umlp.generator.domain.visitor.model.interaction.ThrowInteraction;
import umlp.generator.domain.visitor.model.ocl.AssertStatement;
import umlp.generator.domain.visitor.model.ocl.ExhaustiveOCLStatementVariantMatcher;
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
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SD2TestDecorator {

  private static final String PROCESSOR_VARIABLE_NAME = "processor";
  private static final Collector<CharSequence, ?, String> STATEMENT_COLLECTOR = Collectors.joining(
      ";\n      ");

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

    //TODO this here:
    /* Things to generate:
     * Imports
     * Attributes
     * Create-Methods
     * Test Method:
     *   processor-Name
     *   object instantiation
     *   pre-interaction ocl
     *   processor-activation
     *   for each trigger do:
     *     - expectations
     *     - trigger call
     *   done
     * */

    //add imports
    addImports(cd4c, testClass, sdTest);

    var sdObjects = sdTest.sdObjects();
    var attributes = getVariables(sdTest);

    //add attributes
    for (var attribute : attributes) {
      cd4c.addAttribute(testClass, String.format("%s %s", attribute.type(), attribute.name()));
    }

    //add create methods
    for (var sdObject : sdObjects) {
      cd4c.addMethod(testClass,
                     "umlp.domain.sd2test.CreateObject",
                     sdObject.name(),
                     sdObject.qualifiedTypeName(),
                     domainName);
    }

    //processor name
    var defaultMatchingStrategy = sdTest.matchingStrategy().orElseGet(MatchingStrategy::getDefault);
    var processorName = determineProcessorVariableName(attributes);

    var objectInitializationStatements = createObjectInitStatements(sdObjects,
                                                                    defaultMatchingStrategy,
                                                                    processorName);
    var preInteractionStatements = sdTest
        .preInteractionOCLStatements()
        .stream()
        .flatMap(statement -> createSimpleOCLStatements(statement,
                                                        processorName,
                                                        defaultMatchingStrategy))
        .collect(Collectors.toList());
    var triggerStatements = sdTest
        .trigger()
        .stream()
        .flatMap(trigger -> createTriggerStatements(trigger,
                                                    processorName,
                                                    defaultMatchingStrategy))
        .collect(Collectors.toList());

    //test method
    var testMethod = cd4c.addMethod(testClass,
                                    "umlp.domain.sd2test.Test",
                                    processorName,
                                    stringifyMatchingStrategy(defaultMatchingStrategy),
                                    objectInitializationStatements,
                                    preInteractionStatements,
                                    triggerStatements);
    glex.addAfterTemplate("cd2java.Annotations", testMethod, new StringHookPoint("@Test"));
    glex.addAfterTemplate("cd2java.Annotations",
                          testMethod,
                          new StringHookPoint("@SuppressWarnings({\"SimplifiableAssertion\", " +
                                              "\"OptionalOfNullableMisuse\"})"));

    //other imports
    cd4c.addImport(testClass, Joiners.DOT.join(domainPackageName, "test_infrastructure", "*"));
    cd4c.addImport(testClass, Joiners.DOT.join(PathUtil.getTestInfrastructurePath(), "*"));
    cd4c.addImport(testClass, "org.junit.jupiter.api.Test");
    cd4c.addImport(testClass, "umlp.test_rte.monitor.InteractionProcessor");
    cd4c.addImport(testClass, "umlp.test_rte.monitor.model.MatchingStrategy");
    cd4c.addImport(testClass, "umlp.test_rte.monitor.MethodMonitor");
    cd4c.addImport(testClass, "org.junit.jupiter.api.Assertions");
    cd4c.addImport(testClass, "umlp.test_rte.monitor.model.ocl.OCLCondition");
    cd4c.addImport(testClass, "umlp.test_rte.monitor.model.ocl.OCLVariableDeclaration");

    return testClass;
  }

  private Stream<String> createTriggerStatements(Trigger trigger,
                                                 String processorName,
                                                 MatchingStrategy defaultMatchingStrategy) {
    var triggerInteraction = trigger.triggerInteraction();

    //TODO add checks for complete triggers and non-static resulting calls when reading the model!

    var triggerInteractionStatementStream = triggerInteraction.isStatic() ?
        triggerInteraction
            .oclStatements()
            .stream()
            .flatMap(statement -> createSimpleOCLStatements(statement,
                                                            processorName,
                                                            defaultMatchingStrategy)) :
        createExpectStatements(triggerInteraction, processorName, defaultMatchingStrategy);

    var resultingInteractionStatementStream = trigger
        .resultingInteractions()
        .stream()
        .flatMap(interaction -> createExpectStatements(interaction,
                                                       processorName,
                                                       defaultMatchingStrategy));
    var expectStatementStream = Stream.concat(triggerInteractionStatementStream,
                                              resultingInteractionStatementStream);

    var triggerCall = String.format("%s.%s(%s)",
                                    triggerInteraction.target().name(),
                                    triggerInteraction.methodName(),
                                    join(triggerInteraction.argumentExpressions().orElseThrow()));
    var triggerStatement = String.format(
        "try { %s; } catch (Throwable ignore) {}\n  %s.assertExpectedInteractionsHaveOccurred()",
        triggerCall,
        processorName);

    return Stream.concat(expectStatementStream, Stream.of(triggerStatement, ""));
  }

  private String join(List<String> parameterExpressions) {
    return String.join(", ", parameterExpressions);
  }

  private Stream<String> createExpectStatements(Interaction interaction,
                                                String processorName,
                                                MatchingStrategy defaultMatchingStrategy) {
    return interaction.matchVariant(new ExhaustiveInteractionVariantMatcher<>() {

      @Override
      public Stream<String> whenMethodCall(MethodCallInteraction methodCall) {
        var additionalStatements = new ArrayList<String>();
        var joiner = createMethodChainJoiner();
        joiner.add(String.format("%s.expectMethodCall(%s, %s, \"%s\")",
                                 processorName,
                                 actorAsExpression(methodCall.source()),
                                 actorAsExpression(methodCall.target()),
                                 methodCall.methodName()));
        methodCall
            .argumentExpressions()
            .ifPresent(expressions -> addParametersToJoiner(expressions, joiner));
        methodCall
            .activityBar()
            .ifPresent(activityBar -> addActivityBarExpectations(activityBar,
                                                                 joiner,
                                                                 additionalStatements,
                                                                 processorName,
                                                                 defaultMatchingStrategy));

        //add ocl statements to joiner
        addOCLStatementsToJoiner(methodCall.oclStatements(),
                                 joiner,
                                 processorName,
                                 defaultMatchingStrategy);

        return Stream.concat(Stream.of(joiner.toString()), additionalStatements.stream());
      }

      @Override
      public Stream<String> whenConstructorCall(ConstructorCallInteraction constructorCall) {
        var additionalStatements = new ArrayList<String>();
        var joiner = createMethodChainJoiner();
        joiner.add(String.format("%s.expectConstructorCall(%s, \"%s\", object -> { %s; })",
                                 processorName,
                                 actorAsExpression(constructorCall.source()),
                                 constructorCall.initializationTypeName(),
                                 String.format("%s = object; %s.declare(object, %s)",
                                               constructorCall.objectName(),
                                               processorName,
                                               stringifyMatchingStrategy(defaultMatchingStrategy))));
        constructorCall
            .parameterExpressions()
            .ifPresent(expressions -> addParametersToJoiner(expressions, joiner));
        constructorCall
            .activityBar()
            .ifPresent(activityBar -> addActivityBarExpectations(activityBar,
                                                                 joiner,
                                                                 additionalStatements,
                                                                 processorName,
                                                                 defaultMatchingStrategy));
        addOCLStatementsToJoiner(constructorCall.oclStatements(),
                                 joiner,
                                 processorName,
                                 defaultMatchingStrategy);
        return Stream.concat(Stream.of(joiner.toString()), additionalStatements.stream());
      }

      @Override
      public Stream<String> whenReturn(ReturnInteraction returnInteraction) {
        var joiner = createMethodChainJoiner();
        joiner.add(String.format("%s.expectReturn(%s, %s)",
                                 processorName,
                                 actorAsExpression(returnInteraction.source()),
                                 actorAsExpression(returnInteraction.target())));
        returnInteraction
            .returnValue()
            .ifPresent(returnValue -> returnValue
                .expression()
                .ifPresentOrElse(valueExpression -> joiner.add(String.format(
                                     ".withValue(Optional.ofNullable(%s))",
                                     stringifyJavaExpression(valueExpression))),
                                 () -> joiner.add(".withAnyValue()")));
        addOCLStatementsToJoiner(returnInteraction.oclStatements(),
                                 joiner,
                                 processorName,
                                 defaultMatchingStrategy);
        return Stream.of(joiner.toString());
      }

      @Override
      public Stream<String> whenThrow(ThrowInteraction throwInteraction) {
        var joiner = createMethodChainJoiner();
        joiner.add(String.format("%s.expectThrow(%s, %s, \"%s\")",
                                 processorName,
                                 actorAsExpression(throwInteraction.source()),
                                 actorAsExpression(throwInteraction.target()),
                                 throwInteraction.errorClassName()));
        throwInteraction
            .constructorParameterExpressions()
            .ifPresent(expressions -> joiner.add(String.format(".withReferenceError(new %s(%s))",
                                                               throwInteraction.errorClassName(),
                                                               join(expressions))));
        addOCLStatementsToJoiner(throwInteraction.oclStatements(),
                                 joiner,
                                 processorName,
                                 defaultMatchingStrategy);
        return Stream.of(joiner.toString());
      }
    });
  }

  private void addParametersToJoiner(List<String> expressions, StringJoiner joiner) {
    joiner.add(String.format(".withParameters(%s)", join(expressions)));
  }

  private void addActivityBarExpectations(ActivityBar activityBar,
                                          StringJoiner joiner,
                                          ArrayList<String> additionalStatements,
                                          String processorName,
                                          MatchingStrategy defaultMatchingStrategy) {
    joiner.add(".withActivityBar()");
    //add body to stream
    var bodyInteractionStatements = activityBar
        .body()
        .stream()
        .flatMap(bodyInteraction -> createExpectStatements(bodyInteraction,
                                                           processorName,
                                                           defaultMatchingStrategy))
        .collect(Collectors.toList());
    additionalStatements.addAll(bodyInteractionStatements);

    //add expected-end to stream
    var activityEndJoiner = createMethodChainJoiner();
    activityEndJoiner.add(String.format("%s.expectActivityEnd()", processorName));
    addOCLStatementsToJoiner(activityBar.oclStatements(),
                             activityEndJoiner,
                             processorName,
                             defaultMatchingStrategy);
    additionalStatements.add(activityEndJoiner.toString());
  }

  private String actorAsExpression(Actor actor) {
    return actor.isStatic() ? "Optional.empty()" : actor.name();
  }

  private StringJoiner createMethodChainJoiner() {
    return new StringJoiner("\n    ");
  }

  private void addOCLStatementsToJoiner(List<OCLStatement> statements,
                                        StringJoiner joiner,
                                        String processorName,
                                        MatchingStrategy defaultMatchingStrategy) {
    for (var oclStatement : statements) {
      var ocl = oclStatement.matchVariant(new ExhaustiveOCLStatementVariantMatcher<String>() {
        @Override
        public String whenLetStatement(LetStatement letStatement) {
          var simpleStatements = createSimpleOCLStatements(letStatement,
                                                           processorName,
                                                           defaultMatchingStrategy);
          return String.format("OCLVariableDeclaration.fromExecutor" + "(() -> { %s; })",
                               simpleStatements.collect(STATEMENT_COLLECTOR));
        }

        @Override
        public String whenAssertStatement(AssertStatement assertStatement) {
          var expression = assertStatement.javaExpression();
          return String.format("OCLCondition.from(() -> %s, %s)",
                               expression,
                               stringifyJavaExpression(expression));
        }
      });
      joiner.add(String.format(".withOCL(%s)", ocl));
    }
  }

  private Stream<String> createSimpleOCLStatements(OCLStatement statement,
                                                   String processorName,
                                                   MatchingStrategy defaultMatchingStrategy) {
    return statement.matchVariant(new ExhaustiveOCLStatementVariantMatcher<>() {
      @Override
      public Stream<String> whenLetStatement(LetStatement letStatement) {
        var name = letStatement.name();
        var assignStatement = String.format("%s = %s", name, letStatement.valueExpression());
        var declareStatement = String.format("%s.declare(%s, %s)",
                                             processorName,
                                             name,
                                             stringifyMatchingStrategy(defaultMatchingStrategy));
        return Stream.of(assignStatement, declareStatement);
      }

      @Override
      public Stream<String> whenAssertStatement(AssertStatement assertStatement) {
        var expression = assertStatement.javaExpression();
        return Stream.of(String.format("Assertions.assertTrue(%s, %s)",
                                       expression,
                                       stringifyJavaExpression(expression)));
      }
    });
  }

  private String stringifyJavaExpression(String expression) {
    return '"' + expression.replace("\"", "\\\"") + '"';
  }

  private List<String> createObjectInitStatements(List<SDObject> sdObjects,
                                                  MatchingStrategy defaultMatchingStrategy,
                                                  String processorName) {
    return sdObjects.stream().flatMap(object -> {
      var name = object.name();
      var createStatement = String.format("%s = create%s()", name, capitalizeFirst(name));
      var matchingStrategy = stringifyMatchingStrategy(object
                                                           .matchingStrategy()
                                                           .orElse(defaultMatchingStrategy));
      var declareStatement = String.format("%s.declare(%s, %s)",
                                           processorName,
                                           name,
                                           matchingStrategy);
      return Stream.of(createStatement, declareStatement);
    }).collect(Collectors.toList());
  }

  private String stringifyMatchingStrategy(MatchingStrategy matchingStrategy) {
    return "MatchingStrategy." + matchingStrategy.name();
  }

  private String capitalizeFirst(String s) {
    return s.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  private Collection<Variable> getVariables(SDTest sdTest) {
    var objectStream = sdTest.sdObjects().stream().map(Variable::from);
    var preInteractionStream = sdTest
        .preInteractionOCLStatements()
        .stream()
        .flatMap(oclStatement -> getVariable(oclStatement).stream());
    var testInteractions = sdTest.trigger().stream().flatMap(trigger -> {
      var triggerInteractionStream = Stream.of(trigger.triggerInteraction());
      var resultingInteractionStream = trigger.resultingInteractions().stream();
      return Stream.concat(triggerInteractionStream, resultingInteractionStream);
    }).collect(Collectors.toList());
    return Stream
        .concat(Stream.concat(objectStream, preInteractionStream),
                collectDeclaredVariables(testInteractions).stream())
        .collect(Collectors.toList());
  }

  private void addImports(CD4C cd4c, ASTCDClass testClass, SDTest sdTest) {
    //copy import statements of the diagram
    for (var importStatement : sdTest.imports()) {
      var signature = createJavaSignature(importStatement);
      cd4c.addImport(testClass, signature);
    }
  }

  private String determineProcessorVariableName(Collection<Variable> attributes) {
    var nameWrapper = new Object() {
      String name = PROCESSOR_VARIABLE_NAME;
    };
    //make sure the name is not taken yet, otherwise append a number
    var suffix = 0;
    while (attributes.stream().anyMatch(attribute -> attribute.name().equals(nameWrapper.name))) {
      nameWrapper.name = PROCESSOR_VARIABLE_NAME + suffix++;
    }
    return nameWrapper.name;
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
          interaction.matchVariant(new InteractionVariantMatcher<Stream<Variable>,
              RuntimeException>() {
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

    public static Variable from(SDObject object) {
      return new Variable(object.name(), object.qualifiedTypeName());
    }

    public String name() {
      return name;
    }

    public String type() {
      return type;
    }

    @Override
    public boolean equals(Object object) {
      if (object == null || getClass() != object.getClass()) return false;
      Variable variable = (Variable) object;
      return Objects.equals(name, variable.name) && Objects.equals(type, variable.type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, type);
    }
  }
}
