package umlp.cli;

import cd2gui.CD2GUITool;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.Names;
import umlp.UMLPMill;
import umlp.UMLPReports;
import umlp._symboltable.IUMLPArtifactScope;
import umlp.cli.common.IOperation;
import umlp.generator.domain.UMLAddGemIdTrafo;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CD2GUIPipelineSetup {


  // TODO: Split this further into pipelines - right now the entire CD2GUI workflow is a single
  final IOperation<UMLPPipelineData> cd2gui = data -> {
    data.umlp_reportManagerFactory = new UMLPReports(data.outDir.getPath(), data.reportDir.getPath(), data.reportPathOutput, data.hwcPath, data.tool.buildJsonData(data.cmd));   //TODO: Maybe refactor into a method to remove duplicate

    Reporting.init(data.outDir.getPath(), data.reportDir.getPath(), data.umlp_reportManagerFactory);
    String report_on = data.inputFile.getName().replace(".", "__");   // TODO: Is this name unique? Should it be unique?
    Reporting.on(report_on);

    UMLPMill.init();
    BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope(), false);

    // parse, create symbol table, check all cocos
    ASTCDCompilationUnit ast = data.tool.parse(data.inputFile.getPath());
    Reporting.reportParseInputFile(data.inputFile.toPath().toAbsolutePath(), report_on); // TODO: causes NullPointerException
    data.tool.runAfterParseTrafo(ast);
    data.tool.createSymbolTable(ast);
    data.tool.runDefaultCoCos(ast);
    data.tool.runDefaultTrafos(ast);
    data.tool.recreateSymbolTable(ast);

    // Do not gen Forms for singletons
    List<String> extraOpts = new ArrayList<>();
    for (String singletonClass : data.tool.getSingletonClasses(ast)) {
      extraOpts.add("-");
      extraOpts.add("ExcludeForm");
      extraOpts.add(singletonClass);

      extraOpts.add("-");
      extraOpts.add("NoOverview");
      extraOpts.add(singletonClass);
    }

    CD2GUITool guiTool = new CD2GUITool(ast, data.outDir, data.hwcPath, extraOpts.toArray(new String[0]));
    guiTool.generateGUI();

    // TODO (MB): Ist diese Lösung okay?
    // Create a customized symbol table for the generated GUI-models
    new UMLAddGemIdTrafo(UMLPMill.inheritanceTraverser()).transform(ast);
    IUMLPArtifactScope symbolTable = data.tool.recreateSymbolTable(ast);
    Path symbolPath = Paths.get(data.outDir.getPath() + File.separator + "symbols" + File.separator + Names.getPathFromPackage(symbolTable.getPackageName()) + File.separator + symbolTable.getName() + ".umlpsym");
    JsonPrinter.disableIndentation();
    data.tool.storeSymbols(symbolTable, symbolPath.toString());

    Reporting.flush(ast);
    return true;
  };

  void createCD2GUI(UMLPToolPipelineSetup umlpPipeline) {
    umlpPipeline.cd2Gui.register(umlpPipeline.isUMLPFileCheck);
    umlpPipeline.cd2Gui.register(cd2gui);
  }

}
