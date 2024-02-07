/* (c) https://github.com/MontiCore/monticore */
package guiTest;

import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl.GUIDSLTool;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._parser.GUIDSLParser;
import de.monticore.guidsl._symboltable.IGUIDSLArtifactScope;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp._symboltable.IUMLPArtifactScope;
import umlp.generator.AbstractDecorator;
import umlptest.AbstractUMLPTest;

import java.nio.file.Path;
import java.util.Optional;

public abstract class AbstractGUITest extends AbstractUMLPTest {

  protected static GlobalExtensionManagement createGlex() {
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setBeforeTemplate("cd2java.Class", AbstractDecorator.getOriginHookPoint());
    glex.setBeforeTemplate("cd2java.Enum", AbstractDecorator.getOriginHookPoint());
    glex.setBeforeTemplate("cd2java.Interface", AbstractDecorator.getOriginHookPoint());
    return glex;
  }

  public static ASTGUICompilationUnit parseGUI(String model) {
    return parseGUI("src/test/resources/", model);
  }

  public static ASTGUICompilationUnit parseGUI(String resources, String model) {
    try {
      GUIDSLParser parser = new GUIDSLParser();
      Optional<ASTGUICompilationUnit> optAst = parser.parse(resources + model);

      if (!parser.hasErrors() && optAst.isPresent()) {
        return optAst.get();
      }
      Log.error("0xA1050x72114 Model could not be parsed.");
    } catch (NullPointerException | java.io.IOException e) {
      Log.error("0xA1051x37376 Failed to parse " + model, e);
    }
    // should never be reached (unless failquick is off)
    return null;
  }

  protected IGUIDSLArtifactScope createSymTab(
      ASTGUICompilationUnit ast, Optional<Path> modelFilePathOpt, MCPath modelPath) {
    GUIDSLTool tool = new GUIDSLTool();
    tool.defaultImportTrafo(ast);
    tool.resetGSAndMill();
    if (modelFilePathOpt.isPresent()) {
      tool.registerLoadedFile(modelFilePathOpt.get().toString());
    }
    return tool.createSymbolTable(ast, modelPath);
  }

  protected String removeSpaces(String st) {
    return st.replaceAll("\\s+","");
  }

}
