/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.io.paths.MCPath;
import umlp.UMLPMill;
import umlp.UMLPTool;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._symboltable.UMLPSymbols2Json;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

public abstract class DomainGUITest extends AbstractGUITest {

  protected ASTGUICompilationUnit processGUIModel(
      String modelPath,
      String symbolPath
  ) {
    return processGUIModel(modelPath, symbolPath, "Domain.umlp");
  }

  protected ASTGUICompilationUnit processGUIModel(
      String modelPath,
      String symbolPath,
      String domainPath
  ) {
    UMLPMill.reset();
    UMLPMill.globalScope().clear();
    UMLPMill.init();
    BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope(), false);

    String symFolder = BUILD_FOLDER + "/guitests";

    cd2Sym(symFolder, domainPath);

    String otherCDFile = "packaged/Types.umlp";
    cd2Sym(symFolder, otherCDFile);

    MCPath importedSymbolPath;
    importedSymbolPath = new MCPath();
    importedSymbolPath.addEntry(Paths.get(symFolder));
    importedSymbolPath.addEntry(Paths.get(symbolPath));
    String[] guidslTypesJarPath = System.getProperty("guidsl-default-types").split(File.pathSeparator);
    for (String path : guidslTypesJarPath) {
      importedSymbolPath.addEntry(Paths.get(path));
    }

    ASTGUICompilationUnit guiAst;
    guiAst = parseGUI(modelPath);
    UMLPTool.createSymbolTable(
        guiAst,
        Optional.of(Paths.get("src/test/resources/" + modelPath)),
        importedSymbolPath
    );
    return guiAst;
  }

  public void cd2Sym(String symFolder, String cdPath) {
    UMLPSymbols2Json s2j = new UMLPSymbols2Json();
    String domainSymFile = symFolder + "/" + cdPath + "sym";
    ASTCDCompilationUnit ast = parse("gui/" + cdPath);
    runAfterParseTrafo(ast);
    IUMLPArtifactScope as = createSymTab(ast);
    runDefaultTrafos(ast);
    as = recreateSymbolTable(ast);
    s2j.store(as, domainSymFile);
  }

}
