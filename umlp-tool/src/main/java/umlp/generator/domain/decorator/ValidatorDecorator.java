package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.ocl.ocl.OCLMill;
import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.ocl.ocl._ast.ASTOCLContextDefinition;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.ocl.ocl._ast.ASTOCLParamDeclaration;
import de.monticore.ocl.ocl._visitor.OCLTraverser;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.gradle.internal.Pair;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.validator.ocl.OCLParser;
import umlp.generator.validator.ocl.associationTree.AssociationTreePrettyPrinterTraverser;
import umlp.generator.validator.ocl.associationTree.BackwardsAssociationTreePrettyPrinter;
import umlp.generator.validator.ocl.associationTree.OCLInvariantTree;
import umlp.generator.validator.ocl.associationTree.OCLInvariantTreeNode;
import umlp.generator.validator.ocl.visitors.CustomCommonExpressionsHandler;
import umlp.generator.validator.ocl.visitors.CustomExpressionsBasisHandler;
import umlp.generator.validator.ocl.visitors.CustomOCLExpressionsHandler;
import umlp.generator.validator.ocl.visitors.CustomOCLHandler;
import umlp.generator.validator.ocl.visitors.OCLContextCollector;
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
  private final List<OCLInvariantTree> oclInvariantTrees;
  private final Map<ASTOCLInvariant, List<ASTOCLParamDeclaration>> paramDeclarationsForInvariant = new LinkedHashMap<>();

  public ValidatorDecorator(GlobalExtensionManagement glex) {
    super(glex);
    oclInvariantTrees = new ArrayList<>();
  }

  private final Map<String, List<String>> enumMap = new LinkedHashMap<>();

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDEnumsList().forEach(e -> {
      List<String> enumValues = e.getCDEnumConstantList().stream().map(ASTCDEnumConstant::getName).collect(Collectors.toList());
      enumMap.put(e.getName(), enumValues);
    });

    for(ASTOCLCompilationUnit compilationUnit : getOclCompilationUnits()) {
      OCLTraverser traverser = OCLMill.traverser();
      OCLInvariantExpressionsVisitor invariantExpressionsVisitor = new OCLInvariantExpressionsVisitor(origCD);
      OCLContextCollector contextCollector = new OCLContextCollector();
      traverser.add4CommonExpressions(invariantExpressionsVisitor);
      traverser.add4ExpressionsBasis(invariantExpressionsVisitor);
      traverser.add4OCLExpressions(invariantExpressionsVisitor);
      traverser.add4OCL(contextCollector);
      traverser.add4OCL(invariantExpressionsVisitor);
      traverser.setCommonExpressionsHandler(new CustomCommonExpressionsHandler(traverser));
      traverser.setExpressionsBasisHandler(new CustomExpressionsBasisHandler(traverser));
      traverser.setOCLExpressionsHandler(new CustomOCLExpressionsHandler(traverser));
      traverser.setOCLHandler(new CustomOCLHandler(traverser));
      traverser.traverse(compilationUnit);

      this.oclInvariantTrees.addAll(invariantExpressionsVisitor.getAssociationTrees());
      this.paramDeclarationsForInvariant.putAll(contextCollector.getParamDeclarationsForInvariants());
    }

    String cdName = origCD.getCDDefinition().getName();

    origCD.getCDDefinition().getCDClassesList().stream()
            .filter(c -> !c.getSymbol().isIsDerived())
            .filter(c -> !c.getSymbol().isIsAbstract())
            .forEach(c -> astcdPackage.addCDElement(withOrigin(createValidatorClassForDomainClass(c, cdName))));
  }

  protected void addImports(ASTCDClass validatorClass) {
    getCD4C().addImport(validatorClass, PathUtil.getBackendRTECommonPath("ValidationMessage"));
    getCD4C().addImport(validatorClass, PathUtil.getBackendRTECommonPath("ValidationStatus"));
    getCD4C().addImport(validatorClass, "java.time.ZonedDateTime");
    getCD4C().addImport(validatorClass, "java.time.chrono.ChronoLocalDateTime");
  }

  protected ASTCDElement createValidatorClassForDomainClass(ASTCDType domainClass, String cdName) {
    String className = domainClass.getName();

    List<ASTCDMethodSignature> methods = validateOCLRules(domainClass, cdName);

    ASTCDClass validatorClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(className + SUFFIX)
            .addAllCDMembers(methods)
            .addCDMember(getCD4C().addMethod(domainClass, getValidateTemplate(), className, methods.stream().map(ASTCDMethodSignature::getName).collect(Collectors.toList())))
            .build();
    addImports(validatorClass);
    return validatorClass;
  }

  protected List<ASTCDMethodSignature> validateOCLRules(ASTCDType domainClass, String cdName) {
    List<ASTCDMethodSignature> oclRules = new ArrayList<>();
    for(ASTOCLInvariant invariant : collectInvariantsForType(domainClass.getName())) {
      String oclMessage = invariant.getStereotypeList().stream().filter(s -> !s.getValue("message").isEmpty()).findFirst().map(s -> s.getValue("message")).orElse("");
      String oclRuleName = invariant.getName();
      // Has to be present, else something else went wrong
      OCLInvariantTree oCLInvariantTree = this.oclInvariantTrees.stream().filter(a -> a.getInvariant().equals(invariant)).findFirst().get();
      String oclRule = OCLParser.generate(invariant.getExpression(), new IndentPrinter(), enumMap, oCLInvariantTree);
      oclRules.add(getCD4C().addMethod(domainClass, getValidateRuleTemplate(), oclRuleName, oclRule, oclMessage, domainClass.getName(), oCLInvariantTree.getBranchRootNodes()));
    }

    return oclRules;
  }

  protected String getValidateTemplate() {
    return "umlp.validator.Validate";
  }

  protected String getValidateRuleTemplate() {
    return "umlp.validator.ValidateRule";
  }

  public List<ASTOCLCompilationUnit> getOclCompilationUnits() {
    return oclCompilationUnits;
  }
  public void setOclCompilationUnits(List<ASTOCLCompilationUnit> oclCompilationUnits) {
    this.oclCompilationUnits = oclCompilationUnits;
  }

  private List<ASTOCLInvariant> collectInvariantsForType(String typeName) {
    List<ASTOCLInvariant> result = new ArrayList<>();
    paramDeclarationsForInvariant.forEach((invariant, paramDeclarations) -> {
      if(paramDeclarations.stream().map(ASTOCLParamDeclaration::getMCType).map(ASTMCType::printType).collect(Collectors.toList()).contains(typeName)) {
        result.add(invariant);
      }
    });
    return result;
  }
}
