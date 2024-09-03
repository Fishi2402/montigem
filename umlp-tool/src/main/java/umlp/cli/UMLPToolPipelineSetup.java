package umlp.cli;

import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPReports;
import umlp.UMLPTool;
import umlp.cli.common.IOperation;
import umlp.cli.common.Pipeline;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static umlp.UMLPToolOptions.CD2GUI;
import static umlp.UMLPToolOptions.DEBUG;
import static umlp.UMLPToolOptions.GUIRTETypes;
import static umlp.UMLPToolOptions.GUIVariability;
import static umlp.UMLPToolOptions.HELP;
import static umlp.UMLPToolOptions.HWC_PATH;
import static umlp.UMLPToolOptions.INPUT;
import static umlp.UMLPToolOptions.MODEl_PATH;
import static umlp.UMLPToolOptions.OUTPUT;
import static umlp.UMLPToolOptions.PATH;
import static umlp.UMLPToolOptions.REPORTS;
import static umlp.UMLPToolOptions.REPORT_BASE;
import static umlp.UMLPToolOptions.TEST_INFRASTRUCTURE;
import static umlp.UMLPToolOptions.TMPL_PATH;

/**
 * Set-up for the tool-pipeline.
 * The options CD2GUI, GuiVar, UMLP (from a .umlp file), and GUI are moved into separate pipelines
 * (each defined in their own files)
 */
public class UMLPToolPipelineSetup {

  // A reuseable operation, showing the help
  final IOperation<UMLPPipelineData> showHelp;

  // a reuseable operation, setting up the UMLPReports
  final IOperation<UMLPPipelineData> setupUMLPReports = data -> {
    data.umlp_reportManagerFactory = new UMLPReports(data.outDir.getPath(), data.reportDir.getPath(), data.reportPathOutput, data.hwcPath, data.tool.buildJsonData(data.cmd));
    Reporting.init(data.outDir.getPath(), data.reportDir.getPath(), data.umlp_reportManagerFactory);
    String report_on = data.inputFile.getName().replace(".", "__");   // TODO: Is this name unique? Should it be unique?
    Reporting.on(report_on);
    return true;
  };

  final IOperation<UMLPPipelineData> isUMLPFileCheck = data -> {
    if (!data.inputFile.getName().endsWith(".umlp")) {
      Log.warn("option -cd2gui used without a classdiagram umlp file as input");
      return false;
    }
    return true;
  };

  // Next, the various operations are stored, such that they can be modified

  final IOperation<UMLPPipelineData> initLog;

  final IOperation<UMLPPipelineData> getInputFile;

  final IOperation<UMLPPipelineData> loadPath;

  final IOperation<UMLPPipelineData> loadModelPath;

  final IOperation<UMLPPipelineData> loadHWCPath;

  final IOperation<UMLPPipelineData> loadTmplPath;

  final Pipeline<UMLPPipelineData> cd2Gui;
  final Pipeline<UMLPPipelineData> testInfrastructure;
  final Pipeline<UMLPPipelineData> guiVar;
  final Pipeline<UMLPPipelineData> umlp;
  final Pipeline<UMLPPipelineData> gui;
  final Pipeline<UMLPPipelineData> guiRTETypes;


  // Specific separate workflows are defined in individual classes

  // they are selected based on if/elses

  CD2GUIPipelineSetup cd2GUIPipelineSetup;
  GuiVarPipelineSetup guiVarPipelineSetup;
  UMLPFilePipelineSetup umlpFilePipelineSetup;
  GuiPipelineSetup guiPipelineSetup;
  RTEDataPipelineSetup gUIRTEDataPipelineSetup;

  final Pipeline<UMLPPipelineData> main;

  public UMLPToolPipelineSetup() {
    // Define the "main" pipeline (which is followed for every tool-run)
    main = new Pipeline<>();
    // A reuseable operation, showing the help and exiting
    main.register(showHelp = data -> {
      if (data.getCmd() == null || !data.getCmd().getArgList().isEmpty() || HELP.isSelected(data.getCmd())) {
        org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
        formatter.setWidth(80);
        formatter.printHelp("UMLPTool", data.getCmdOptions());
        return false; // abort
      }
      return true;
    });
    main.register(initLog = data -> {
      // set desired log level without modifying the "failQuick" option
      boolean enableFailQuick = Log.isFailQuickEnabled();
      if (DEBUG.isSelected(data.getCmd())) {
        Log.initDEBUG();
      } else {
        Log.initWARN();
      }
      Log.enableFailQuick(enableFailQuick);
      return true;
    });
    main.register(getInputFile = data -> {
      if (!INPUT.isSelected(data.getCmd())) {
        Log.error("0xE0000 Indicating an input file with the option 'i' is mandatory");
      }
      data.inputFile = new File(INPUT.getValue(data.getCmd()));
      data.outDir = new File(OUTPUT.getValue(data.cmd, UMLPTool.DEFAULT_OUT));

      data.reportDir = new File(REPORTS.getValue(data.cmd, UMLPTool.DEFAULT_REPORT_OUT));
      String report_base = REPORT_BASE.getValue(data.cmd);
      if (!report_base.isEmpty()) {
        Path base_dir = Paths.get(report_base).toAbsolutePath();
        // Only relativize this path if both share a root
        data.reportPathOutput = p -> Objects.equals(p.getRoot(), base_dir.getRoot()) ? base_dir.relativize(p.toAbsolutePath()) : p.toAbsolutePath();
      } else {
        data.reportPathOutput = Path::toAbsolutePath;
      }
      return true;
    });
    main.register(loadPath = data -> {
      if (PATH.isSelected(data.cmd)) {
        for (String v : data.cmd.getOptionValues(PATH.getCMD())) {
          data.importedSymbolPath.addEntry(Paths.get(v));
        }
      }
      return true;
    });
    main.register(loadModelPath = data -> {
      if (MODEl_PATH.isSelected(data.cmd)) {
        for (String v : data.cmd.getOptionValues(MODEl_PATH.getCMD())) {
          data.modelPath.addEntry(Paths.get(v));
          data.importedSymbolPath.addEntry(Paths.get(v));
        }
      }
      return true;
    });
    main.register(loadHWCPath = data -> {
      if (HWC_PATH.isSelected(data.cmd)) {
        for (String v : data.cmd.getOptionValues(HWC_PATH.getCMD())) {
          data.hwcPath.addEntry(Paths.get(v));
        }
      }
      return true;
    });
    main.register(loadTmplPath = data -> {
      if (TMPL_PATH.isSelected(data.cmd)) {
        for (String v : data.cmd.getOptionValues(TMPL_PATH.getCMD())) {
          data.fpPath.addEntry(Paths.get(v));
        }
      }
      return true;
    });

    // Register the 5 alternatives: CD2GUI, GuiVar, UMLP or GUI or TEST_INFRASTRUCTURE
    guiRTETypes = new Pipeline<>();
    cd2Gui = new Pipeline<>();
    guiVar = new Pipeline<>();
    umlp = new Pipeline<>();
    gui = new Pipeline<>();
    testInfrastructure = new Pipeline<>();
    main.register(data -> {
      if (GUIRTETypes.isSelected(data.cmd)) {
        return guiRTETypes.doWork(data);
      }else  if (CD2GUI.isSelected(data.cmd)) {
        return cd2Gui.doWork(data);
      } else if (TEST_INFRASTRUCTURE.isSelected(data.cmd)) {
        return testInfrastructure.doWork(data);
      } else if (GUIVariability.isSelected(data.cmd)) {
        return guiVar.doWork(data);
      } else if (data.inputFile.getName().endsWith(".umlp")) {
        return umlp.doWork(data);
      } else if (data.inputFile.getName().endsWith(".gui")) {
        return gui.doWork(data);
      } else {
        Log.error("Unhandled input file");
        return true;
      }
    });

    // Next, actually populate the individual pipelines of the options
    cd2GUIPipelineSetup = new CD2GUIPipelineSetup();
    cd2GUIPipelineSetup.createCD2GUI(this);

    guiVarPipelineSetup = new GuiVarPipelineSetup();
    guiVarPipelineSetup.createGuiVar(this);

    umlpFilePipelineSetup = new UMLPFilePipelineSetup(this);

    guiPipelineSetup = new GuiPipelineSetup();
    guiPipelineSetup.createGui(this);

    gUIRTEDataPipelineSetup = new RTEDataPipelineSetup();
    gUIRTEDataPipelineSetup.createGUIRTETypes(this);

    // Finally (for every option), close report managers
    main.addFinally(data -> {
      if (data.umlp_reportManagerFactory != null) {
        data.umlp_reportManagerFactory.close();
      }
    });
    main.addFinally(data -> {
      if (data.guidsl_reportManagerFactory != null) {
        data.guidsl_reportManagerFactory.close();
      }
    });

  }

  /**
   * @return the main pipeline
   */
  public Pipeline<UMLPPipelineData> getMain() {
    return this.main;
  }
}
