package umlp.generator.domain.decorator;

import com.google.common.base.Joiner;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDInterfaceUsage;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class EnsureCorrectManager4TypeDecorator extends AbstractDecorator {

  //same package as normal test infrastructure
  public static final String PACKAGE = TestSetupDecorator.PACK_TEST_INFRASTRUCTURE;
  public static final String NAME_FORMAT = "EnsureCorrectManager4%s";
  public static final String CUSTOM_TEMPLATE_PATH =
      "umlp.test_infrastructure" + ".ensurecorrectmanager4.";
  public static final TemplateHookPoint DELEGATION_HOOKPOINT = new TemplateHookPoint(
      CUSTOM_TEMPLATE_PATH + "CallCheckManagerBody");
  public static final String BASIC_CONSTRUCTOR_TEMPLATE = "umlp.common.BasicConstructor";

  private final ObserverDecorator observerDecorator;
  private Collection<GenerationSummary> generationSummaries = List.of();

  public EnsureCorrectManager4TypeDecorator(GlobalExtensionManagement glex,
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
    this.generationSummaries = new ArrayList<>();
    ASTCDPackage aPackage = getPackage(origCD, decoratedCD, PACKAGE);
    observerDecorator
        .getGenerationSummaries()
        .forEach(generationSummary -> aPackage.addCDElement(withOrigin(createEnsureCorrectManager(
            origCD,
            decoratedCD,
            generationSummary))));

  }

  private ASTCDClass createEnsureCorrectManager(ASTCDCompilationUnit origCD,
                                                ASTCDCompilationUnit decoratedCD,
                                                ObserverDecorator.GenerationSummary observerGenerationSummary) {
    String cdName = origCD.getCDDefinition().getName();
    ASTCDType domainClass = observerGenerationSummary.domainClass();
    String domainClassName = domainClass.getName();

    String observerInterfaceName = domainClassName + ObserverDecorator.SUFFIX;
    ASTCDInterfaceUsage cdInterfaceUsage = getInterfaceUsageFacade().createCDInterfaceUsage(
        observerInterfaceName);
    String resName = String.format(NAME_FORMAT, domainClassName);
    ASTCDClass res = UMLPMill
        .cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(cdInterfaceUsage)
        .setName(resName)
        .build();

    GenerationSummary generationSummary = new GenerationSummary(domainClass, res);
    generationSummaries.add(generationSummary);

    CD4C cd4C = getCD4C();
    Joiner dot = Joiners.DOT;
    String defaultPackage = getPackageName(origCD, DEFAULT_PACKAGE);


    //attributes
    String managerClassName = cdName + ManagerDecorator.SUFFIX;
    ASTCDAttribute managerAttr = cd4C.addAttribute(res,
                                                   false,
                                                   false,
                                                   "protected final " + managerClassName +
                                                   " manager");

    //constructor
    cd4C.addConstructor(res, BASIC_CONSTRUCTOR_TEMPLATE, res.getName(), List.of(managerAttr));

    //imports
    cd4C.addImport(res, dot.join(defaultPackage, managerClassName));
    cd4C.addImport(res, dot.join(defaultPackage, observerInterfaceName));
    for (ASTMCImportStatement importStatement : cd4C.getImportList(observerGenerationSummary.observerInterface())) {
      cd4C.addImport(res, importStatement.getQName());
    }

    //methods
    cd4C.addMethod(res, CUSTOM_TEMPLATE_PATH + "CheckManager", cdName);
    //override listener methods to call checkManager()
    for (GeneratedMethodSignature gms : observerGenerationSummary.notifyMethodSignatures()) {
      ASTCDMethodSignature overriddenMethod = replaceMethodBody(gms.signature(),
                                                                DELEGATION_HOOKPOINT,
                                                                res);
      makePublic(overriddenMethod);

      //method parameter imports
      importParams:
      for (ASTCDParameter parameter : overriddenMethod.getCDParameterList()) {
        ASTMCType parameterType = parameter.getMCType();
        String typeName = parameterType.printType();

        //cut off generic part
        if (typeName.contains("<")) typeName = typeName.substring(0, typeName.indexOf('<'));

        //filter out already qualified types
        if (typeName.contains(".")) continue;

        String importSignature = dot.join(defaultPackage, typeName);

        //ignore everything that is already imported
        for (ASTMCImportStatement importStatement : cd4C.getImportList(res)) {
          if (importStatement.getQName().equals(importSignature)) continue importParams;
        }

        //add an import for the parameter type, if it is part of the original or decorated class diagramm
        Collection<ASTCDElement> cdElements = new ArrayList<>();
        cdElements.addAll(origCD.getCDDefinition().getCDElementList());
        cdElements.addAll(decoratedCD.getCDDefinition().getCDElementList());

        for (ASTCDElement element : cdElements) {
          if (!(element instanceof ASTCDType)) continue;

          ASTCDType cdType = (ASTCDType) element;
          if (!cdType.getName().equals(typeName)) continue;

          cd4C.addImport(res, importSignature);
          break;
        }
      }
    }

    return res;
  }

  public Collection<GenerationSummary> getGenerationSummaries() {
    return generationSummaries;
  }

  public static final class GenerationSummary {

    private final ASTCDType domainClass;
    private final ASTCDClass generatedClass;

    public GenerationSummary(ASTCDType domainClass, ASTCDClass generatedClass) {
      this.domainClass = domainClass;
      this.generatedClass = generatedClass;
    }

    public ASTCDType domainClass() {
      return domainClass;
    }

    public ASTCDClass generatedClass() {
      return generatedClass;
    }
  }
}
