/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.*;

public class CommandObjectMapperDecorator extends AbstractDecorator {

  public static final String SUFFIX = "CommandObjectMapper";

  public CommandObjectMapperDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(withOrigin(createClass(origCD)));
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
    getCD4C().addMethod(mapperClass, getInitTemplate(), name);
    getCD4C().addMethod(mapperClass, getSerializeCommandTemplate());
    getCD4C().addMethod(mapperClass, getDeserializeCommandTemplate());
    getCD4C().addMethod(mapperClass, getSerializeResponseTemplate());
    getCD4C().addMethod(mapperClass, getDeserializeResponseTemplate());

    addImports(mapperClass);

    return mapperClass;
  }

  protected String getInitTemplate() {
    return "umlp.commandobjectmapper.Init";
  }

  protected String getSerializeCommandTemplate() {
    return "umlp.commandobjectmapper.SerializeCommand";
  }

  protected String getDeserializeCommandTemplate() {
    return "umlp.commandobjectmapper.DeserializeCommand";
  }

  protected String getSerializeResponseTemplate() {
    return "umlp.commandobjectmapper.SerializeResponse";
  }

  protected String getDeserializeResponseTemplate() {
    return "umlp.commandobjectmapper.DeserializeResponse";
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandObjectMapper"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandResult"));

  }


}
