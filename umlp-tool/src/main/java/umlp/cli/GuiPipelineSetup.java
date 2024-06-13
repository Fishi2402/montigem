package umlp.cli;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._symboltable.GUIDSLSymbols2Json;
import de.monticore.guidsl._symboltable.IGUIDSLArtifactScope;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.Names;
import umlp.GUIDSLReports;
import umlp.TagRelated;
import umlp.UMLPTool;
import umlp.cli.common.IOperation;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.GUINodeIdTrafo;
import umlp.generator.gui.genhtml.GUI2HtmlGenerator;
import umlp.generator.gui.genjava.GUI2JavaGenerator;
import umlp.generator.gui.gentypescript.GUI2TSGenerator;
import umlp.generator.gui.gentypescript.config.GUI2TSConfigGenerator;
import umlp.generator.gui.gentypescript.config.ModuleRoutingInfo;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static umlp.UMLPToolOptions.*;

public class GuiPipelineSetup {

  // TODO: Split this further into pipelines - right now the entire GUI workflow is a single op
  final IOperation<UMLPPipelineData> gui = data -> {
    Map<String, ModuleRoutingInfo> routesMap = new LinkedHashMap<>();

    // Prepare the SingletonLookup
    TagRelated.initLookup(data.importedSymbolPath);

    for (String guiPathString : data.cmd.getOptionValues(INPUT.getCMD())) {
      Path guiModel = Paths.get(guiPathString);
      data.guidsl_reportManagerFactory = new GUIDSLReports(data.outDir.getPath(), data.reportDir.getPath(), data.reportPathOutput, data.hwcPath);

      Reporting.init(data.outDir.getPath(), data.reportDir.getPath(), data.guidsl_reportManagerFactory);
      String report_on = guiModel.getFileName().toString().replace(".", "__");
      Reporting.on(report_on);

      // parse, add default component ids, create symbol table, check all cocos
      GUIDSLMill.init();
      ASTGUICompilationUnit astGui = data.tool.parseGUI(guiModel.toString());
      Reporting.reportParseInputFile(guiModel.toAbsolutePath(), report_on);
      GUINodeIdTrafo nodeIdTrafo = new GUINodeIdTrafo();
      nodeIdTrafo.transform(astGui);
      // logTrafoExec("GUINodeIdTrafo", astGui);

      IGUIDSLArtifactScope symbolTable = data.tool.createSymbolTable(astGui, Optional.of(guiModel), data.importedSymbolPath);
      data.tool.runDefaultCoCos(astGui);

      if (!routesMap.containsKey(astGui.getPackage())) {
        routesMap.put(astGui.getPackage(), new ModuleRoutingInfo(astGui.getMCPackageDeclaration().getMCQualifiedName().getPartsList()));
      }
      routesMap.get(astGui.getPackage()).add(astGui);

      // print or store symbol table
      if (SYMTAB.isSelected(data.cmd)) {
        String s = SYMTAB.getValue(data.cmd);
        if (null != s) {
          // store symbol table to passed file
          Path symbolPath = Paths.get(s + File.separator + Names.getPathFromPackage(symbolTable.getPackageName()) + File.separator + symbolTable.getName() + ".guisym");
          JsonPrinter.disableIndentation();
          data.tool.storeSymbols(symbolTable, symbolPath.toString());
        }
        else {
          //print (formatted!) symboltable to console
          GUIDSLSymbols2Json s2j = new GUIDSLSymbols2Json();
          JsonPrinter.enableIndentation();
          System.out.println(s2j.serialize(symbolTable));
        }
      }

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

      List<String> domainModels = new ArrayList<>();
      if (DOMAIN.isSelected(data.cmd)) {
        domainModels.addAll(Arrays.asList(data.cmd.getOptionValues(DOMAIN.getCMD())));
      }

      List<String> createModels = new ArrayList<>();
      if (GUI_CREATE.isSelected(data.cmd)) {
        createModels.addAll(Arrays.asList(data.cmd.getOptionValues(GUI_CREATE.getCMD())));
      }

      GUIGeneratorUtil.getInstance().init(domainModels, createModels);

      // Get all ASTGUIComponents
      GUIComponentsVisitor viewComponents = new GUIComponentsVisitor(astGui, genSetup.getGlex());
      genSetup.getGlex().setGlobalValue("viewComponents", viewComponents);
      genSetup.getGlex().setGlobalValue("genHelper", GUIGeneratorUtil.getInstance());
      genSetup.getGlex().setGlobalValue("UMLPGeneratorConfig", UMLPGeneratorConfig.getInstance().initFromCmd(data.cmd));

      GUI2JavaGenerator javaGenerator = new GUI2JavaGenerator(genSetup);
      String javaConfigTemplate = data.cmd.getOptionValue("ct_java", UMLPTool.DEFAULT_CONFIGTEMPLATE_GUI2JAVA);
      javaGenerator.generate(astGui, javaConfigTemplate);

      GUI2TSGenerator tsGenerator = new GUI2TSGenerator(genSetup);
      tsGenerator.generate(astGui, UMLPTool.DEFAULT_CONFIGTEMPLATE_GUI2TS);

      GUI2HtmlGenerator htmlGenerator = new GUI2HtmlGenerator(genSetup);
      String htmlConfigTemplate = data.cmd.getOptionValue("ct_html", UMLPTool.DEFAULT_CONFIGTEMPLATE_GUI2HTML);
      htmlGenerator.generate(astGui, htmlConfigTemplate);

      // write reports
      Reporting.flush(astGui);
      //Reporting.reportFileCreation(standaloneConfigPath);
    }

    //create routes file for every package and
    GeneratorSetup genSetup = new GeneratorSetup();
    // set output directory
    genSetup.setOutputDirectory(data.outDir);
    // set hwc directories
    genSetup.setHandcodedPath(data.hwcPath);
    // set template path
    genSetup.setAdditionalTemplatePaths(data.fpPath.getEntries().stream().map(p -> p.toFile()).collect(Collectors.toList()));
    GlobalExtensionManagement glex = data.tool.createGlex(data.tool.getModuleConfigTemplates(data.cmd));
    genSetup.setGlex(glex);
    genSetup.getGlex().setGlobalValue("genHelper", GUIGeneratorUtil.getInstance());
    genSetup.setTracing(false);
    GUI2TSConfigGenerator tsConfigGenerator = new GUI2TSConfigGenerator(genSetup, data.hwcPath);

    for (Map.Entry<String, ModuleRoutingInfo> packageInfo : routesMap.entrySet()) {
      String output = tsConfigGenerator.generateComponentsRouting(packageInfo, routesMap);
      Reporting.reportFileCreation(Paths.get(data.outDir.getPath(), output).toString());
    }
    List<String> outputs = tsConfigGenerator.generateAppConfigFiles();
    for (String output : outputs) {
      Reporting.reportFileCreation(Paths.get(data.outDir.getPath(), output).toString());
    }

    TagRelated.postInfo();

    Reporting.flush(new ASTGUICompilationUnit());
    return true;
  };

  void createGui(UMLPToolPipelineSetup umlpPipeline) {
    umlpPipeline.gui.register(gui);
  }

}
