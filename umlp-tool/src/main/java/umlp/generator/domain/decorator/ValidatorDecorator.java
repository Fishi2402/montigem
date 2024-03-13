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
  private final Map<ASTOCLParamDeclaration, List<ASTOCLInvariant>> relevantInvariantsForParamDeclaration = new LinkedHashMap<>();
  private final Map<ASTSetComprehension, String> setComprehensionContext = new LinkedHashMap<>();
  private final Map<ASTOCLInvariant, List<OCLInvariantTree>> directlyRelatedAssociationTreesForInvariant = new LinkedHashMap<>();
  private final Map<String, List<String>> enumMap = new LinkedHashMap<>();

  public ValidatorDecorator(GlobalExtensionManagement glex) {
    super(glex);
    oclInvariantTrees = new ArrayList<>();
  }

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
      traverser.add4SetExpressions(contextCollector);
      traverser.add4SetExpressions(invariantExpressionsVisitor);
      traverser.setCommonExpressionsHandler(new CustomCommonExpressionsHandler(traverser));
      traverser.setExpressionsBasisHandler(new CustomExpressionsBasisHandler(traverser));
      traverser.setOCLExpressionsHandler(new CustomOCLExpressionsHandler(traverser));
      traverser.setOCLHandler(new CustomOCLHandler(traverser));
      traverser.traverse(compilationUnit);

      this.oclInvariantTrees.addAll(invariantExpressionsVisitor.getAssociationTrees());
      this.paramDeclarationsForInvariant.putAll(contextCollector.getParamDeclarationsForInvariants());
      this.setComprehensionContext.putAll(contextCollector.getSetComprehensionContext());
    }

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
    ASTCDClass validatorClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(domainClass.getName() + SUFFIX)
        .build();

    List<ASTCDMethodSignature> methods = validateOCLRules(domainClass, validatorClass);
    getCD4C().addMethod(validatorClass, getValidateTemplate(), domainClass.getName(), methods.stream().map(ASTCDMethodSignature::getName).collect(Collectors.toList()));

    addImports(validatorClass);
    return validatorClass;
  }

  protected List<ASTCDMethodSignature> validateOCLRules(ASTCDType domainClass, ASTCDType validatorClass) {
    List<ASTCDMethodSignature> oclRules = new ArrayList<>();
    for(ASTOCLInvariant invariant : collectInvariantsForType(domainClass.getName())) {
      OCLInvariantTree oCLInvariantTree = this.oclInvariantTrees.stream().filter(a -> a.getInvariant().equals(invariant)).findFirst().get();
      String body = OCLParser.generate(invariant, new IndentPrinter(), enumMap, oCLInvariantTree);
      // Multiple context definitions were replaced by multiple invariants in the OCLASTPreProcessor
      ASTOCLParamDeclaration dec = invariant.getOCLContextDefinitionList().get(0).getOCLParamDeclaration();

      oclRules.add(getCD4C().addMethod(validatorClass, getValidateRuleTemplate(), domainClass.getName(), invariant.getName(), dec.getMCType().printType(), dec.getName(), body));
    }

    // The following only works if for an object of type Object and an association OtherObject -> Object the call object.getOtherObject() is possible
    /*for(ASTOCLInvariant invariant : getAnyRelevantInvariant(domainClass)) {
      String oclRuleName = invariant.getName();
      OCLInvariantTree oCLInvariantTree = this.oclInvariantTrees.stream().filter(a -> a.getInvariant().equals(invariant)).findFirst().get();

      Pair<OCLInvariantTree, OCLInvariantTree> splitAssociationTrees = oCLInvariantTree.splitTreeByDomainClass(domainClass);
      OCLInvariantTree leftTree = splitAssociationTrees.getLeft();
      String code = new AssociationTreePrettyPrinterTraverser(leftTree, new BackwardsAssociationTreePrettyPrinter()).prettyPrint().get(0);
      if(leftTree.getBranchRootNodes().size() == 1) {
        oclRuleName = oclRuleName + "For" + leftTree.getBranchRootNodes().get(0).getParameterDeclaration().get().getMCType().printType();
      }
      oclRules.add(getCD4C().addMethod(validatorClass, getValidateAssociatedRuleTemplate(), oclRuleName, domainClass.getName(), code));
    }*/

    return oclRules;
  }

  protected String getValidateTemplate() {
    return "umlp.validator.Validate";
  }

  protected String getValidateRuleTemplate() {
    return "umlp.validator.ValidateRule";
  }

  protected String getValidateAssociatedRuleTemplate() {
    return "umlp.validator.ValidateAssociatedRule";
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
  // Do not delete, this is relevant for the validation of associations (see commented code in this.validateOCLRules(...))
  private List<ASTOCLInvariant> getAnyRelevantInvariant(ASTCDType domainClass) {
    List<ASTOCLInvariant> result = new ArrayList<>();
    List<ASTOCLInvariant> possibleInvariants = paramDeclarationsForInvariant.keySet().stream()
            .filter(i -> !collectInvariantsForType(domainClass.getName()).contains(i))
            .collect(Collectors.toList());
    for(ASTOCLInvariant invariant : possibleInvariants) {
      OCLInvariantTree oclInvariantTree = oclInvariantTrees.stream().filter(a -> a.getInvariant().equals(invariant)).findFirst().get();
      List<OCLInvariantTreeNode> relevantBranches = oclInvariantTree.getBranchesRelatedToDomainClass(domainClass);
      if(!relevantBranches.isEmpty()) {
        result.add(invariant);
        directlyRelatedAssociationTreesForInvariant.computeIfAbsent(invariant, v -> new ArrayList<>()).add(oclInvariantTree);
        List<ASTOCLParamDeclaration> relevantParamDeclarations = invariant.getOCLContextDefinitionList().stream().map(ASTOCLContextDefinition::getOCLParamDeclaration).collect(Collectors.toList());
        for(ASTOCLParamDeclaration paramDeclaration : relevantParamDeclarations) {
          if(paramDeclaration.getMCType().printType().equals(domainClass.getName())) {
            relevantInvariantsForParamDeclaration.computeIfAbsent(paramDeclaration, v -> new ArrayList<>()).add(invariant);
          }
        }
      }
    }
    return result;
  }
}
