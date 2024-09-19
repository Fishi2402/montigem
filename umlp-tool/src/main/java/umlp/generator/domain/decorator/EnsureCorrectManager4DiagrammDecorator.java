package umlp.generator.domain.decorator;

import com.google.common.base.Joiner;
import de.monticore.cd.facade.CDModifier;
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
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.test_infrastructure.domain.decorator.TestSetupDecorator;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class EnsureCorrectManager4DiagrammDecorator extends AbstractDecorator {

  //same package as normal test infrastructure
  public static final String PACKAGE = TestSetupDecorator.PACK_TEST_INFRASTRUCTURE;
  public static final String NAME_FORMAT = "EnsureCorrectManager%s";
  public static final String CUSTOM_TEMPLATE_PATH =
      "umlp.test_infrastructure" + ".ensurecorrectmanager.";
  public static final StringHookPoint RETURN_NOTIFICATION_SCOPE_MINIMAL_HOOK_POINT =
      new StringHookPoint(
      "return NotificationScope.MINIMAL;");

  private final DiagramObserverDecorator diagramObserverDecorator;
  private final EnsureCorrectManager4TypeDecorator ensureCorrectManager4TypeDecorator;

  public EnsureCorrectManager4DiagrammDecorator(GlobalExtensionManagement glex,
                                                DiagramObserverDecorator diagramObserverDecorator,
                                                EnsureCorrectManager4TypeDecorator ensureCorrectManager4TypeDecorator) {
    super(glex);
    this.diagramObserverDecorator = diagramObserverDecorator;
    this.ensureCorrectManager4TypeDecorator = ensureCorrectManager4TypeDecorator;
  }

  private static void makePublic(ASTCDMethodSignature signature) {
    if (signature instanceof ASTCDMethod) {
      ASTCDMethod method = (ASTCDMethod) signature;
      method.setModifier(PUBLIC.build());
    }
  }

  static String getAttributeName(String attributeType) {
    return Character.toLowerCase(attributeType.charAt(0)) + attributeType.substring(1);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    var optGenerationSummary = diagramObserverDecorator.getGenerationSummary();
    if (optGenerationSummary.isPresent()) {
      ASTCDPackage aPackage = getPackage(origCD, decoratedCD, PACKAGE);
      aPackage.addCDElement(withOrigin(createEnsureCorrectManager(origCD,
                                                                  optGenerationSummary.get())));
    }
  }

  private ASTCDClass createEnsureCorrectManager(ASTCDCompilationUnit origCD,
                                                DiagramObserverDecorator.GenerationSummary diagramGenerationSummary) {

    String cdName = origCD.getCDDefinition().getName();
    String observerInterfaceName = cdName + DiagramObserverDecorator.SUFFIX;
    ASTCDInterfaceUsage cdInterfaceUsage = getInterfaceUsageFacade().createCDInterfaceUsage(observerInterfaceName);
    ASTCDClass res = UMLPMill
        .cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setCDInterfaceUsage(cdInterfaceUsage)
        .setName(String.format(NAME_FORMAT, cdName))
        .build();

    CD4C cd4C = getCD4C();
    Joiner dot = Joiners.DOT;
    String defaultPackage = getPackageName(origCD, DEFAULT_PACKAGE);

    //ensureCorrectManager4 attributes
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for (EnsureCorrectManager4TypeDecorator.GenerationSummary ecm4GenerationSummary :
        ensureCorrectManager4TypeDecorator.getGenerationSummaries()) {
      //attribute
      ASTCDClass generatedClass = ecm4GenerationSummary.generatedClass();
      String attributeType = generatedClass.getName();
      String attributeName = getAttributeName(attributeType);
      ASTCDAttribute attribute = cd4C.addAttribute(res,
                                                   false,
                                                   false,
                                                   "protected final " + attributeType + " " +
                                                   attributeName);
      attributes.add(attribute);

      //override notifyAdd method
      ASTCDType domainClass = ecm4GenerationSummary.domainClass();
      for (var notifyMethodSignature : diagramGenerationSummary.notifyMethodSignatures()) {
        //find the notifyAdd method for the attribute type
        if (notifyMethodSignature.changedType() != domainClass) continue;

        GeneratedMethodSignature gms = notifyMethodSignature.generatedMethodSignature();
        if (!gms.template().equals(DiagramObserverDecorator.NOTIFY_ADD_TEMPLATE)) continue;

        ASTCDMethodSignature signature = gms.signature();
        String observableParamName = signature.getCDParameter(0).getName();
        TemplateHookPoint notifyAddBodyHookPoint = new TemplateHookPoint(CUSTOM_TEMPLATE_PATH + "NotifyAddBody",
                                                                         attributeName,
                                                                         observableParamName);
        ASTCDMethodSignature overriddenMethod = replaceMethodBody(signature,
                                                                  notifyAddBodyHookPoint,
                                                                  res);
        makePublic(overriddenMethod);

        break; //there is only one notify add method per domain class
      }

      //import for changed type
      cd4C.addImport(res, dot.join(defaultPackage, domainClass.getName()));
    }
    //constructor
    cd4C.addConstructor(res,
                        CUSTOM_TEMPLATE_PATH + "Constructor",
                        cdName,
                        res.getName(),
                        attributes);

    //overwrite getScope method
    for (GeneratedMethodSignature gms : diagramGenerationSummary.otherMethodSignatures()) {
      if (!gms.template().equals(DiagramObserverDecorator.GET_SCOPE_GLOBAL_TEMPLATE)) continue;

      ASTCDMethodSignature overriddenMethod = replaceMethodBody(gms.signature(), RETURN_NOTIFICATION_SCOPE_MINIMAL_HOOK_POINT, res);
      makePublic(overriddenMethod);

      break; //there is only one getScope method
    }

    //imports
    cd4C.addImport(res, dot.join(defaultPackage, observerInterfaceName));
    cd4C.addImport(res, dot.join(defaultPackage, cdName + ManagerDecorator.SUFFIX));
    for (ASTMCImportStatement importStatement : diagramGenerationSummary.importStatements()) {
      cd4C.addImport(res, importStatement.getQName());
    }

    return res;
  }
}
