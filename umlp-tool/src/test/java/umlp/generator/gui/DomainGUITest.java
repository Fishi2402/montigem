/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.io.paths.MCPath;
import de.monticore.tagging.TagRepository;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import umlp.MillConf;
import umlp.TagRelated;
import umlp.UMLPTool;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._symboltable.UMLPSymbols2Json;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

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
    MillConf.switchTo(MillConf.TAGS, MillConf.UMLP);

    String symFolder = BUILD_FOLDER + "/guitests";

    var domainAST = cd2AST(domainPath);
    var domainSym = cdAST2Sym(symFolder, domainPath, domainAST);

    String otherCDPath = "packaged/Types.umlp";
    var otherAST = cd2AST(otherCDPath);
    cdAST2Sym(symFolder, otherCDPath, otherAST);

    MCPath importedSymbolPath;
    importedSymbolPath = new MCPath();
    importedSymbolPath.addEntry(Paths.get(symFolder));
    importedSymbolPath.addEntry(Paths.get(symbolPath));
    String[] guidslTypesJarPath = System.getProperty("guidsl-default-types").split(File.pathSeparator);
    for (String path : guidslTypesJarPath) {
      importedSymbolPath.addEntry(Paths.get(path));
    }

    var dummyTool = new UMLPTool();
    CommandLineParser parser = new DefaultParser();
    try {
      CommandLine cmd = parser.parse(dummyTool.initOptions(), new String[]{"-s", symFolder});
      TagRepository.clearTags(); // Make sure to unload all previous tags from earlier runs
      TagRelated.doStoreTagStereos(domainAST, dummyTool, domainSym, cmd);
    } catch (ParseException e) {
      fail();
    }

    MillConf.switchTo(MillConf.UMLP, MillConf.GUIDSL);
    UMLPTool.initGUIGlobalScope(importedSymbolPath);
    ASTGUICompilationUnit guiAst;
    var e = dummyTool.createAstWithSymtab(
        Paths.get("src/test/resources/" + modelPath)
    );
    guiAst = e.getKey();
    return guiAst;
  }

  public ASTCDCompilationUnit cd2AST(String cdPath) {
    return parse("gui/" + cdPath);
  }

  public IUMLPArtifactScope cdAST2Sym(String symFolder, String cdPath, ASTCDCompilationUnit ast) {
    UMLPSymbols2Json s2j = new UMLPSymbols2Json();
    String domainSymFile = symFolder + "/" + cdPath + "sym";
    runAfterParseTrafo(ast);
    createSymTab(ast);
    runDefaultTrafos(ast);
    IUMLPArtifactScope as = recreateSymbolTable(ast);
    s2j.store(as, domainSymFile);
    return as;
  }

}
