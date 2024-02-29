package umlp.generator.domain.decorator;

import com.google.common.base.Joiner;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDInterfaceUsage;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class EnsureCorrectManager4Decorator extends AbstractDecorator {

  //same package as normal test infrastructure
  public static final String PACKAGE = TestSetupDecorator.PACK_TEST_INFRASTRUCTURE;
  public static final String NAME_FORMAT = "EnsureCorrectManager4%s";
  public static final String CUSTOM_TEMPLATE_PATH = "umlp.test_infrastructure" +
                                                    ".ensurecorrectmanager.";
  public static final TemplateHookPoint DELEGATION_HOOKPOINT = new TemplateHookPoint(
      CUSTOM_TEMPLATE_PATH + "CallCheckManagerBody");
  public static final String BASIC_CONSTRUCTOR_TEMPLATE = "umlp.common.BasicConstructor";
  public static final String EMPTY_BODY_TEMPLATE = "cd2java.EmptyBody";

  private final ObserverDecorator observerDecorator;

  public EnsureCorrectManager4Decorator(GlobalExtensionManagement glex,
                                        ObserverDecorator observerDecorator) {
    super(glex);
    this.observerDecorator = observerDecorator;
  }

  private static void makePublic(ASTCDMethodSignature signature) {
    if (signature instanceof ASTCDMethod) {
      ASTCDMethod method = (ASTCDMethod) signature;
      method.setModifier(PUBLIC.build());
    }
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage aPackage = getPackage(origCD, decoratedCD, PACKAGE);
    observerDecorator
        .getGenerationSummaries()
        .forEach(generationSummary -> aPackage.addCDElement(withOrigin(createEnsureCorrectManager(
            origCD,
            generationSummary))));

  }

  private ASTCDClass createEnsureCorrectManager(ASTCDCompilationUnit origCD,
                                                ObserverDecorator.GenerationSummary generationSummary) {
    String cdName = origCD.getCDDefinition().getName();
    String domainClassName = generationSummary.domainClass().getName();

    ASTCDInterfaceUsage cdInterfaceUsage = getInterfaceUsageFacade().createCDInterfaceUsage(
        domainClassName + ObserverDecorator.SUFFIX);
    ASTCDClass res = UMLPMill
        .cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(cdInterfaceUsage)
        .setName(String.format(NAME_FORMAT, domainClassName))
        .build();

    CD4C cd4C = getCD4C();

    //attributes
    String managerClassName = cdName + ManagerDecorator.SUFFIX;
    ASTCDAttribute managerAttr = cd4C.addAttribute(res,
                                                   true,
                                                   false,
                                                   "protected final " + managerClassName +
                                                   " manager");

    //constructor
    cd4C.addConstructor(res, BASIC_CONSTRUCTOR_TEMPLATE, res.getName(), List.of(managerAttr));

    //methods
    cd4C.addMethod(res, CUSTOM_TEMPLATE_PATH + "CheckManager", cdName);
    //override listener methods to call checkManager()
    for (ASTCDMethodSignature signature : generationSummary.notifyMethodSignatures()) {
      ASTCDMethodSignature overriddenMethod = signature.deepClone();
      makePublic(overriddenMethod);
      getGlex().replaceTemplate(EMPTY_BODY_TEMPLATE, overriddenMethod, DELEGATION_HOOKPOINT);
      res.addCDMember(overriddenMethod);
    }

    addImports(origCD, res, generationSummary);

    return res;
  }

  private void addImports(ASTCDCompilationUnit origCD,
                          ASTCDType type,
                          ObserverDecorator.GenerationSummary generationSummary) {
    CD4C cd4C = getCD4C();
    Joiner dot = Joiners.DOT;
    String defaultPackage = getPackageName(origCD, DEFAULT_PACKAGE);

    cd4C.addImport(type, dot.join(defaultPackage, "*"));
    for (ASTMCImportStatement importStatement : generationSummary.importStatements()) {
      cd4C.addImport(type, importStatement.getQName());
    }
  }
}
