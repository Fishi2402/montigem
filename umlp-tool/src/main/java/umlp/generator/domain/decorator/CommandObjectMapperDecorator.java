/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.se_rwth.commons.StringTransformations.uncapitalize;

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

    // extended CDs
    List<String> commandDeSerNames = new ArrayList<>();
    DiagramSymbol currentDiagram = origCD.getCDDefinition().getSymbol();
    var superDiagrams = UMLPSymbolTables.getExtensionDiamond(currentDiagram).get(currentDiagram);
    for (DiagramSymbol superDiagram : superDiagrams) {
      String diagramName = superDiagram.getName();
      String deSerName = diagramName + "CommandDeSer";
      attr = getCD4C().addAttribute(mapperClass, "protected " + deSerName + " " + uncapitalize(deSerName));
      glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint("= new " + deSerName + "()"));
      commandDeSerNames.add(attr.getName());

      getCD4C().addImport(mapperClass, getPackageName(superDiagram, DEFAULT_PACKAGE) + "." + deSerName);
    }

    // add methods
    getCD4C().addMethod(mapperClass, getInitTemplate(), name);
    getCD4C().addMethod(mapperClass, getSerializeCommandTemplate(), commandDeSerNames);
    getCD4C().addMethod(mapperClass, getDeserializeCommandTemplate(), commandDeSerNames);
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
