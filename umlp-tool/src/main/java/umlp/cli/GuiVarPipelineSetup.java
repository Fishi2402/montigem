package umlp.cli;

import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.io.FileReaderWriter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import umlp.GUIDSLReports;
import umlp.UMLPTool;
import umlp.cli.common.IOperation;
import umlp.generator.gui.guivariability.GUIVariabilityHandler;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static umlp.UMLPToolOptions.INPUT;

public class GuiVarPipelineSetup {

  // TODO: Split this further into pipelines - right now the entire GuiVar workflow is a single
  final IOperation<UMLPPipelineData> guiVar = data -> {
    List<Path> inputs = Arrays.stream(data.cmd.getOptionValues(INPUT.getCMD())).map(Path::of).collect(Collectors.toList());

    data.guidsl_reportManagerFactory = new GUIDSLReports(data.outDir.getPath(), data.reportDir.getPath(), data.reportPathOutput, data.hwcPath);
    Reporting.init(data.outDir.getPath(), data.reportDir.getPath(), data.guidsl_reportManagerFactory);
    GUIDSLMill.init();
    LinkedList<ASTGUICompilationUnit> baseASTs = new LinkedList<>();
    LinkedList<ASTGUICompilationUnit> variationASTs = new LinkedList<>();

    for (Path model : inputs) {
      String report_on = model.getFileName().toString().replace(".", "__");
      Reporting.on(report_on);
      ASTGUICompilationUnit parsedModel = UMLPTool.parseGUI(model.toString());
      Reporting.reportParseInputFile(model.toAbsolutePath(), report_on);

      if (data.tool.isGUIVariabilityFile(parsedModel)) {
        variationASTs.add(parsedModel);
      }
      else {
        baseASTs.add(parsedModel);
      }
    }

    for (ASTGUICompilationUnit variationAST : variationASTs) {
      List<ASTGUICompilationUnit> adaptedAST = baseASTs.stream().filter(ast -> ast.getComponentName().equals(variationAST.getGUIComponentDeclaration().getGUIComponentAdaptation().getTarget())).collect(Collectors.toList());
      if (adaptedAST.size() != 1) {
        Log.error("expected exactly one matching file to adapt with GUIVariability, but found multiple or none");
      }
      GUIVariabilityHandler deltaHandler = new GUIVariabilityHandler(adaptedAST.get(0));
      variationAST.accept(deltaHandler.getTraverser());
    }

    for (ASTGUICompilationUnit productAST : baseASTs) {
      Reporting.on(productAST.getComponentName() + "__gui");
      Path outputPath = data.outDir.toPath().resolve(        //file structure needs to reflect package declarations
          productAST.getMCPackageDeclaration().getMCQualifiedName().toString().replace(".", "/").concat("/" + productAST.getComponentName() + ".gui"));
      GUIDSLFullPrettyPrinter pp = new GUIDSLFullPrettyPrinter(new IndentPrinter(), true);
      FileReaderWriter.storeInFile(outputPath, pp.prettyprint(productAST));

      Reporting.reportFileCreation(outputPath.toString());
      Reporting.flush(productAST);
    }
    return true;
  };

  void createGuiVar(UMLPToolPipelineSetup umlpPipeline) {
    umlpPipeline.guiVar.register(guiVar);

  }

}
