/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class CommandResultDeSerDecorator extends AbstractDecorator {

  public static final String SUFFIX = "CommandResultDeSer";

  public CommandResultDeSerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(withOrigin(createClass(origCD)));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass cmdResultDeSerClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(name+SUFFIX)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandResultDeSer"))
            .build();

    // add methods
    List<String> classNames = getClassNames(origCD);
    // TODO AHe: DeSer load state, error checks for empty etc
    getCD4C().addMethod(cmdResultDeSerClass, getBuildJsonTemplate(), classNames);
    getCD4C().addMethod(cmdResultDeSerClass, getFromJsonTemplate());
    getCD4C().addMethod(cmdResultDeSerClass, getDeserSingleValueTemplate(), classNames);
    getCD4C().addMethod(cmdResultDeSerClass, getSerSingleValueTemplate(), classNames);

    addImports(cmdResultDeSerClass);

    return cmdResultDeSerClass;
  }

  protected List<String> getClassNames(ASTCDCompilationUnit origCD) {
    String packageName = getPackageName(origCD, DEFAULT_PACKAGE);
    List<String> classNames = new ArrayList<>();
    for (ASTCDClass c :  origCD.getCDDefinition().getCDClassesList()) {
      if (canSerialize(c)) {
        classNames.add(Joiners.DOT.join(packageName, c.getName()));
      }
    }
    return classNames;
  }

  protected boolean canSerialize(ASTCDClass c) {
    return !c.getModifier().isDerived() && !c.getModifier().isAbstract();
  }

  protected String getSerSingleValueTemplate() {
    return "umlp.commandresultdeser.SerSingleValue";
  }

  protected String getDeserSingleValueTemplate() {
    return "umlp.commandresultdeser.DeserSingleValue";
  }

  protected String getFromJsonTemplate() {
    return "umlp.commandresultdeser.FromJson";
  }

  protected String getBuildJsonTemplate() {
    return "umlp.commandresultdeser.BuildJson";
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.json.JsonElement");
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("BaseClassObjectMapper"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandResultDeSer"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("SimpleResult"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("ListResult"));

    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("BaseClassObjectMapper"));
  }


}
