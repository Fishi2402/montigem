package umlp.generator.domain.decorator;

import com.google.common.base.Joiner;
import de.monticore.cd.methodtemplates.CD4C;
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

public class EnsureCorrectManagerDecorator extends AbstractDecorator {

  //same package as normal test infrastructure
  public static final String PACKAGE = TestSetupDecorator.PACK_TEST_INFRASTRUCTURE;
  public static final String NAME_FORMAT = "EnsureCorrectManager4%s";
  public static final String CUSTOM_TEMPLATE_PATH = "umlp.test_infrastructure.ensurecorrectmanager.";
  public static final TemplateHookPoint DELEGATION_HOOKPOINT = new TemplateHookPoint(
      CUSTOM_TEMPLATE_PATH + "CallCheckManagerBody");
  public static final String BASIC_CONSTRUCTOR_TEMPLATE = "umlp.common.BasicConstructor";
  public static final String EMPTY_BODY_TEMPLATE = "cd2java.EmptyBody";

  private final ObserverDecorator observerDecorator;

  public EnsureCorrectManagerDecorator(GlobalExtensionManagement glex, ObserverDecorator observerDecorator) {
    super(glex);
    this.observerDecorator = observerDecorator;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage aPackage = getPackage(origCD, decoratedCD, PACKAGE);
    observerDecorator.getGenerationDetails()
                     .forEach(generationDetails -> aPackage.addCDElement(withOrigin(
                         createEnsureCorrectManager(origCD, generationDetails))));

  }

  private ASTCDClass createEnsureCorrectManager(ASTCDCompilationUnit origCD, ObserverGenerationDetails generationDetails) {
    String cdName = origCD.getCDDefinition().getName();
    String domainClassName = generationDetails.domainClass().getName();

    ASTCDInterfaceUsage cdInterfaceUsage = getInterfaceUsageFacade().createCDInterfaceUsage(
        domainClassName + ObserverDecorator.SUFFIX);
    ASTCDClass res = UMLPMill.cDClassBuilder()
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
                                                   "protected final " + managerClassName + " manager");

    //constructor
    cd4C.addConstructor(res, BASIC_CONSTRUCTOR_TEMPLATE, res.getName(), List.of(managerAttr));

    //methods
    cd4C.addMethod(res, CUSTOM_TEMPLATE_PATH + "CheckManager", cdName);
    //override listener methods to call checkManager()
    for (ASTCDMethodSignature signature : generationDetails.notifyMethodSignatures()) {
      ASTCDMethodSignature overriddenMethod = signature.deepClone();
      getGlex().replaceTemplate(EMPTY_BODY_TEMPLATE, overriddenMethod, DELEGATION_HOOKPOINT);
      res.addCDMember(overriddenMethod);
    }

    addImports(origCD, res, generationDetails);

    return res;
  }

  private void addImports(ASTCDCompilationUnit origCD, ASTCDType type, ObserverGenerationDetails generationDetails) {
    CD4C cd4C = getCD4C();
    Joiner dot = Joiners.DOT;
    String defaultPackage = getPackageName(origCD, DEFAULT_PACKAGE);

    cd4C.addImport(type, dot.join(defaultPackage, "*"));
    for (ASTMCImportStatement importStatement : generationDetails.importStatements()) {
      cd4C.addImport(type, importStatement.getQName());
    }
  }
}
