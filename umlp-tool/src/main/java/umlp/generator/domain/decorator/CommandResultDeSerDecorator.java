/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class CommandResultDeSerDecorator extends AbstractDecorator {

  public static final String SUFFIX = "CommandResultDeSer";

  public CommandResultDeSerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(createClass(origCD));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass cmdResultDeSerClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(name+SUFFIX)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandResultDeSer"))
            .build();

    // add methods
    String packageName = getPackageName(origCD, DEFAULT_PACKAGE);
    List<String> classNames = origCD.getCDDefinition().getCDClassesList().stream()
            .filter(c -> !c.getModifier().isDerived())
            .map(c -> Joiners.DOT.join(packageName, c.getName())).collect(Collectors.toList());
    getCD4C().addMethod(cmdResultDeSerClass, "umlp.commandresultdeser.BuildJson", classNames);
    getCD4C().addMethod(cmdResultDeSerClass, "umlp.commandresultdeser.FromJson", classNames);

    addImports(cmdResultDeSerClass);

    return cmdResultDeSerClass;
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "umlp.command.*");
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(clazz, "umlp.rte.BaseClassObjectMapper");
    getCD4C().addImport(clazz, "java.util.Optional");
  }


}
