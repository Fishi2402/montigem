package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.ocl.ocl.OCLMill;
import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.ocl.ocl._ast.ASTOCLConstraint;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.ocl.ocl._visitor.OCLTraverser;
import de.monticore.prettyprint.IndentPrinter;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.validator.ocl.visitors.CustomCommonExpressionsHandler;
import umlp.generator.validator.ocl.visitors.OCLContextCollector;
import umlp.generator.validator.ocl.OCLParser;
import umlp.generator.validator.ocl.visitors.OCLInvariantExpressionsVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ValidatorDecorator extends AbstractDecorator {
  public static final String SUFFIX = "Validator";

  private List<ASTOCLCompilationUnit> oclCompilationUnits;

  public ValidatorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  private final Map<String, List<String>> enumMap = new LinkedHashMap<>();

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDEnumsList().forEach(e -> {
      List<String> enumValues = e.getCDEnumConstantList().stream().map(ASTCDEnumConstant::getName).collect(Collectors.toList());
      enumMap.put(e.getName(), enumValues);
    });
    origCD.getCDDefinition().getCDClassesList().stream()
            .filter(c -> !c.getSymbol().isIsDerived())
            .filter(c -> !c.getSymbol().isIsAbstract())
            .forEach(c -> astcdPackage.addCDElement(withOrigin(createValidatorClassForDomainClass(c))));
  }

  protected void addImports(ASTCDClass validatorClass) {
    getCD4C().addImport(validatorClass, PathUtil.getBackendRTECommonPath("ValidationMessage"));
    getCD4C().addImport(validatorClass, PathUtil.getBackendRTECommonPath("ValidationStatus"));
    getCD4C().addImport(validatorClass, "java.time.ZonedDateTime");
    getCD4C().addImport(validatorClass, "java.time.chrono.ChronoLocalDateTime");
  }

  protected ASTCDElement createValidatorClassForDomainClass(ASTCDType domainClass) {
    String className = domainClass.getName();

    List<ASTCDMethodSignature> methods = validateOCLRules(domainClass);

    ASTCDClass validatorClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(className + SUFFIX)
            .addAllCDMembers(methods)
            .addCDMember(getCD4C().addMethod(domainClass, "umlp.validator.Validate", className, methods.stream().map(ASTCDMethodSignature::getName).collect(Collectors.toList())))
            .build();
    addImports(validatorClass);
    return validatorClass;
  }

  protected List<ASTCDMethodSignature> validateOCLRules(ASTCDType domainClass) {
    List<ASTCDMethodSignature> oclRules = new ArrayList<>();
    for(ASTOCLCompilationUnit compilationUnit : getOclCompilationUnits()) {
      OCLContextCollector oclContextCollector = new OCLContextCollector(compilationUnit);
      List<ASTOCLConstraint> constraints = oclContextCollector.collectConstraintsForType(domainClass.getName());
      for(ASTOCLConstraint constraint : constraints){
        if(constraint instanceof ASTOCLInvariant) {
          ASTOCLInvariant invariant = (ASTOCLInvariant) constraint;
          String oclMessage = invariant.getStereotypeList().stream().filter(s -> !s.getValue("message").isEmpty()).findFirst().map(s -> s.getValue("message")).orElse("");
          String inputParams = getInputParams(invariant);
          String oclRuleName = invariant.getName();
          String oclRule = OCLParser.generate(invariant.getExpression(), new IndentPrinter(), enumMap);
          OCLTraverser traverser = OCLMill.traverser();
          OCLInvariantExpressionsVisitor invariantExpressionsVisitor = new OCLInvariantExpressionsVisitor(invariant, oclContextCollector.collectParamDeclarationsForConstraint(constraint));
          traverser.add4CommonExpressions(invariantExpressionsVisitor);
          traverser.setCommonExpressionsHandler(new CustomCommonExpressionsHandler(traverser));
          traverser.traverse(invariant);
          List<String> attributes = invariantExpressionsVisitor.collectAttributeNamesForInvariant(invariant);
          oclRules.add(getCD4C().addMethod(domainClass, getValidationRuleTemplate(), oclRuleName, oclRule, oclMessage, inputParams, attributes));
        }
      }
    }

    return oclRules;
  }

  protected String getValidationRuleTemplate() {
    return "umlp.validator.ValidateRule";
  }

  public List<ASTOCLCompilationUnit> getOclCompilationUnits() {
    return oclCompilationUnits;
  }
  public void setOclCompilationUnits(List<ASTOCLCompilationUnit> oclCompilationUnits) {
    this.oclCompilationUnits = oclCompilationUnits;
  }

  private String getInputParams(ASTOCLInvariant invariant) {
    return invariant.getOCLContextDefinitionList().stream()
            .map(c -> c.getOCLParamDeclaration().getMCType().printType() + " " + c.getOCLParamDeclaration().getName())
            .collect(Collectors.joining(", "));
  }
}
