package umlp.generator.domain.decorator;

import com.google.common.base.Joiner;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class EnsureCorrectManager4Decorator extends AbstractDecorator {

  //same package as normal test infrastructure
  public static final String PACKAGE = TestSetupDecorator.PACK_TEST_INFRASTRUCTURE;
  public static final String NAME_FORMAT = "EnsureCorrectManager4%s";
  public static final String CUSTOM_TEMPLATE_PATH =
      "umlp.test_infrastructure" + ".ensurecorrectmanager4.";
  public static final TemplateHookPoint DELEGATION_HOOKPOINT = new TemplateHookPoint(
      CUSTOM_TEMPLATE_PATH + "CallCheckManagerBody");
  public static final String BASIC_CONSTRUCTOR_TEMPLATE = "umlp.common.BasicConstructor";
  public static final String EMPTY_BODY_TEMPLATE = "cd2java.EmptyBody";
  private static final Class<?>[] PRIMITIVES = {
      byte.class,
      short.class,
      int.class,
      long.class,
      float.class,
      double.class,
      char.class,
      boolean.class
  };

  private final ObserverDecorator observerDecorator;
  private Collection<GenerationSummary> generationSummaries = List.of();

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

  private static boolean canBeResolved(String packageName, String typeName) {
    try {
      Class.forName(Joiners.DOT.join(packageName, typeName));
      return true;
    } catch (ClassNotFoundException e) {
      if (packageName.equals("java.lang")) {
        //primitive types can't be caught with that method
        for (Class<?> primitive : PRIMITIVES) {
          if (primitive.getName().equals(typeName)) return true;
        }
      }
      return false;
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
            generationSummary))));

  }

  private ASTCDClass createEnsureCorrectManager(ASTCDCompilationUnit origCD,
                                                ObserverDecorator.GenerationSummary observerGenerationSummary) {
    String cdName = origCD.getCDDefinition().getName();
    ASTCDType domainClass = observerGenerationSummary.domainClass();
    String domainClassName = domainClass.getName();

    String observerInterfaceName = domainClassName + ObserverDecorator.SUFFIX;
    ASTCDInterfaceUsage cdInterfaceUsage = getInterfaceUsageFacade().createCDInterfaceUsage(
        observerInterfaceName);
    ASTCDClass res = UMLPMill
        .cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(cdInterfaceUsage)
        .setName(String.format(NAME_FORMAT, domainClassName))
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
    for (ASTMCImportStatement importStatement : observerGenerationSummary.importStatements()) {
      cd4C.addImport(res, importStatement.getQName());
    }

    //methods
    cd4C.addMethod(res, CUSTOM_TEMPLATE_PATH + "CheckManager", cdName);
    //override listener methods to call checkManager()
    for (GeneratedMethodSignature gms : observerGenerationSummary.notifyMethodSignatures()) {
      ASTCDMethodSignature overriddenMethod = gms.signature().deepClone();
      makePublic(overriddenMethod);
      getGlex().replaceTemplate(EMPTY_BODY_TEMPLATE, overriddenMethod, DELEGATION_HOOKPOINT);
      res.addCDMember(overriddenMethod);

      //method parameter imports
      importParams:
      for (ASTCDParameter parameter : overriddenMethod.getCDParameterList()) {
        String typeName = parameter.getMCType().printType();

        //cut off generic part
        if (typeName.contains("<")) typeName = typeName.substring(0, typeName.indexOf('<'));

        if (typeName.contains(".")) continue; //filter out already qualified types

        //ignore everything that is already imported
        for (ASTMCImportStatement importStatement : cd4C.getImportList(res)) {
          if (importStatement.getQName().endsWith(typeName)) continue importParams;
        }

        //TODO would be better to check against the imports made by the import template
        //TODO would be even better to only import stuff from the default package
        //ignore everything that can already be resolved by java.lang or java.util
        if (canBeResolved("java.lang", typeName) || canBeResolved("java.util", typeName)) continue;

        cd4C.addImport(res, dot.join(defaultPackage, typeName));
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
