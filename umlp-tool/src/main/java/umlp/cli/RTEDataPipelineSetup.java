package umlp.cli;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import umlp.UMLPMill;
import umlp.UMLPReports;
import umlp.UMLPTool;
import umlp._symboltable.IUMLPArtifactScope;
import umlp.cli.common.IOperation;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.rte.data.CDGenerator4RTE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static umlp.UMLPToolOptions.INPUT;

public class RTEDataPipelineSetup {

  final IOperation<UMLPPipelineData> rteCD = data -> {
    UMLPMill.init();
    BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope(), false);

    data.umlp_reportManagerFactory = new UMLPReports(data.outDir.getPath(), data.reportDir.getPath(), data.reportPathOutput, data.hwcPath, data.tool.buildJsonData(data.cmd));
    Reporting.init(data.outDir.getPath(), data.reportDir.getPath(), data.umlp_reportManagerFactory); // TODO was für ein Reporting Mechanism?


    for (String cdPathString : data.cmd.getOptionValues(INPUT.getCMD())) {
      Path cd = Paths.get(cdPathString);

      String report_on = cd.getFileName().toString().replace(".", "__");
      Reporting.on(report_on);

      // parse, add default component ids, create symbol table, check all cocos
      ASTCDCompilationUnit ast = data.tool.parse(cd.toString());
      data.tool.runAfterParseTrafo(ast);
      data.tool.createSymbolTable(ast);
      data.tool.runDefaultTrafos(ast);
      IUMLPArtifactScope symbolTable = data.tool.recreateSymbolTable(ast);
      // logTrafoExec("GUINodeIdTrafo", astGui);

      GeneratorSetup genSetup = new GeneratorSetup();
      // set output directory
      genSetup.setOutputDirectory(data.outDir);
      // set hwc directories
      genSetup.setHandcodedPath(data.hwcPath);
      // set template path
      genSetup.setAdditionalTemplatePaths(data.fpPath.getEntries().stream().map(p -> p.toFile()).collect(Collectors.toList()));
      GlobalExtensionManagement glex = data.tool.createGlex(data.tool.getModuleConfigTemplates(data.cmd));
      genSetup.setGlex(glex);
      CD4C.init(genSetup);

      // Get all ASTGUIComponents

      CDGenerator4RTE javaGenerator = new CDGenerator4RTE(genSetup, UMLPGeneratorConfig.getInstance());
      String javaConfigTemplate = data.cmd.getOptionValue("ct", UMLPTool.DEFAULT_CONFIGTEMPLATE_RTE2JAVA);
      javaGenerator.generate(ast, javaConfigTemplate);

      Reporting.flush(ast);
    }
    return true;
  };

  void createGUIRTETypes(UMLPToolPipelineSetup umlpPipeline) {
    umlpPipeline.guiRTETypes.register(rteCD);
  }
}
