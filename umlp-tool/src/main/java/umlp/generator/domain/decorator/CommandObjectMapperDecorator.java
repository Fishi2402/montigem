/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.*;

public class CommandObjectMapperDecorator extends AbstractDecorator {

  public static final String SUFFIX = "CommandObjectMapper";

  public CommandObjectMapperDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(createClass(origCD));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass mapperClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(name+SUFFIX)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandObjectMapper"))
            .build();

    // add atributes
    ASTCDAttribute attr = getCD4C().addAttribute(mapperClass, "protected " + name + "CommandDeSer commandDeSer");
    glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint("= new " + name + "CommandDeSer()"));
    attr = getCD4C().addAttribute(mapperClass, "protected " + name + "CommandResultDeSer resultDeSer");
    glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint("= new " + name + "CommandResultDeSer()"));

    // add methods
    getCD4C().addMethod(mapperClass, "umlp.commandobjectmapper.Init", name);
    getCD4C().addMethod(mapperClass, "umlp.commandobjectmapper.SerializeCommand");
    getCD4C().addMethod(mapperClass, "umlp.commandobjectmapper.DeserializeCommand");
    getCD4C().addMethod(mapperClass, "umlp.commandobjectmapper.SerializeResponse");
    getCD4C().addMethod(mapperClass, "umlp.commandobjectmapper.DeserializeResponse");

    addImports(mapperClass);

    return mapperClass;
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandObjectMapper"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandResult"));

  }


}
