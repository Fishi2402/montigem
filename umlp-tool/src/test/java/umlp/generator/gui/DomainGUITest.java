/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.io.paths.MCPath;
import umlp.generator.gui.AbstractGUITest;
import umlp.UMLPMill;
import umlp.UMLPTool;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._symboltable.UMLPSymbols2Json;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

public class DomainGUITest extends AbstractGUITest {

  protected ASTGUICompilationUnit processGUIModel(
      String modelPath,
      String symbolPath
  ) {
    UMLPMill.reset();
    UMLPMill.globalScope().clear();
    UMLPMill.init();
    BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope());

    String symFolder = BUILD_FOLDER + "/guitests";
    String domainSymFile = symFolder + "/Domain.umlpsym";
    ASTCDCompilationUnit ast = parse("gui/Domain.umlp");
    runAfterParseTrafo(ast);
    IUMLPArtifactScope as = createSymTab(ast);
    runDefaultTrafos(ast);
    recreateSymbolTable(ast);

    UMLPSymbols2Json s2j = new UMLPSymbols2Json();
    s2j.store(as, domainSymFile);

    ASTGUICompilationUnit guiAst;
    MCPath importedSymbolPath;

    guiAst = parseGUI(modelPath);
    importedSymbolPath = new MCPath();
    importedSymbolPath.addEntry(Paths.get(symFolder));
    importedSymbolPath.addEntry(Paths.get(symbolPath));
    String[] guidslTypesJarPath = System.getProperty("guidsl-default-types").split(File.pathSeparator);
    for (String path : guidslTypesJarPath) {
      importedSymbolPath.addEntry(Paths.get(path));
    }
    UMLPTool.createSymbolTable(
        guiAst,
        Optional.of(Paths.get("src/test/resources/" + modelPath)),
        importedSymbolPath
    );
    return guiAst;
  }
}
