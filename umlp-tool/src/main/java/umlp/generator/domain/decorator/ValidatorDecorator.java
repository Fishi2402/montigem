package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.ocl.ocl._ast.ASTOCLConstraint;
import de.monticore.ocl.ocl._ast.ASTOCLContextDefinition;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.validator.ocl.OCLContextCollector;
import umlp.generator.validator.ocl.OCLParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ValidatorDecorator extends AbstractDecorator {
  public static final String SUFFIX = "Validator";

  private ASTOCLCompilationUnit oclCompilationUnit;

  public ValidatorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
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
    OCLContextCollector oclContextCollector = new OCLContextCollector(getOclCompilationUnit());
    List<ASTOCLConstraint> constraints = oclContextCollector.collectConstraintsForType(domainClass.getName());
    for(ASTOCLConstraint constraint : constraints){
      if(constraint instanceof ASTOCLInvariant) {
        ASTOCLInvariant invariant = (ASTOCLInvariant) constraint;
        String inputParams = getInputParams(invariant);
        String oclRuleName = invariant.getName();
        String oclRule = OCLParser.generate(invariant.getExpression(), new IndentPrinter());
        oclRules.add(getCD4C().addMethod(domainClass, "umlp.validator.ValidateRule", oclRuleName, oclRule, "", inputParams));
      }
    }
    return oclRules;
  }

  public ASTOCLCompilationUnit getOclCompilationUnit() {
    return oclCompilationUnit;
  }
  public void setOclCompilationUnit(ASTOCLCompilationUnit oclCompilationUnit) {
    this.oclCompilationUnit = oclCompilationUnit;
  }

  private String getInputParams(ASTOCLInvariant invariant) {
    return invariant.getOCLContextDefinitionList().stream()
            .map(c -> c.getOCLParamDeclaration().getMCType().printType() + " " + c.getOCLParamDeclaration().getName())
            .collect(Collectors.joining(", "));
  }
}
