package umlp.cli;

import cd2gui.CD2GUITool;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.CDMerge;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import umlp.*;
import umlp._symboltable.IUMLPArtifactScope;
import umlp.cli.common.IOperation;
import umlp.cli.common.Pipeline;
import umlp.generator.domain.UMLAddGemIdTrafo;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static umlp.UMLPToolOptions.MERGE;
import static umlp.UMLPToolOptions.PRETTYPRINT;

public class CD2GUIPipeline extends Pipeline<UMLPPipelineData> {

  public final IOperation<UMLPPipelineData> init;
  public final IOperation.ISimpleOperation2<UMLPPipelineData> addBuiltInTypes;
  public final IOperation<UMLPPipelineData> generateGui;
  public final Pipeline<DataWithAst> modelPipeline;
  public final Pipeline<DataWithAst> loadModelPathPipe;
  //public final IOperation<DataWithAst> mergeAST;
  
  
  public CD2GUIPipeline(IOperation<UMLPPipelineData> setupUMLPReports, IOperation<UMLPPipelineData> isUMLPFileCheck) {
    register("setupUMLPReports", setupUMLPReports);
    register("check_umlp_filetype", isUMLPFileCheck);
    register("init", init = data -> {
      MillConf.switchTo(MillConf.TAGS, MillConf.UMLP, MillConf.GUIDSL);
      TagRelated.initLookup(data.importedSymbolPath);
      UMLPMill.globalScope().setSymbolPath(data.importedSymbolPath);
      return true;
    });
    register("addBuiltInTypes", addBuiltInTypes = this::addBuiltInTypes);
    
    // Pipeline dealing with how to load models into the model path
    loadModelPathPipe = createLoadModelPathPipe();
    register("loadModelpath", IOperation.withDependantPipelines(data -> {
      String report_on = data.inputFile.getName().replace(".", "__");   // TODO: Is this name unique? Should it be unique?
      
      List<URL> models = MCPathUtil.getAllModels(data.modelPath, ".*[.]umlp");
      for (URL modelUrl : models) {
        try (Reader reader = new InputStreamReader(modelUrl.openStream())) {
          ASTCDCompilationUnit ast = UMLPMill.parser().parse(reader).orElseThrow();
          Reporting.reportParseInputFile(data.inputFile.toPath().toAbsolutePath(), report_on);
          Data dataWithAst = new Data(data);
          dataWithAst.setAst(ast);
          loadModelPathPipe.doWork(dataWithAst);
        } catch (IOException e) {
          e.printStackTrace();
          Log.error("Can not load model for url " + modelUrl, e);
          return false;
        }
      }
      return true;
    }, Collections.singletonList(loadModelPathPipe)));
    

    modelPipeline = createModelPipeline();
    register("generateGui",
        IOperation.withDependantPipelines(generateGui = data -> {
          modelPipeline.doWork(new Data(data));
          return true;
        }, Collections.singletonList(modelPipeline)));
    
    /*modelPipeline.registerAfter(model_pipe_symbol_table,"mergeAST", mergeAST = data -> {
      // First step of an AST: If --merge was provided, override AST with merged AST
      if (!data.getData().modelPath.isEmpty()){
        List<ASTCDCompilationUnit> mergeSet = new ArrayList<>();
        mergeSet.add(data.getAst());
        // Parse and report parsing of merge-inputs
        List<URL> models = MCPathUtil.getAllModels(data.getData().modelPath, ".*[.]umlp");
        for (URL modelUrl : models) {
          try (Reader reader = new InputStreamReader(modelUrl.openStream())) {
            ASTCDCompilationUnit ast = UMLPMill.parser().parse(reader).orElseThrow();
            mergeSet.add(ast);
          // Note: Do NOT use reportParseInputFile, as it resets it. Instead, use reportOpenInputFile
          Reporting.reportOpenInputFile(modelUrl.getFile().toString());
          } catch (IOException e) {
            e.printStackTrace();
            Log.error("Can not load model for url " + modelUrl, e);
            return false;
          }
        }
        // Construct a name for the merged file
        String cdName = "Merge";
        if (PRETTYPRINT.isSelected(data.getData().cmd)
            && PRETTYPRINT.getValue(data.getData().cmd) != null ){
          cdName = Path.of(PRETTYPRINT.getValue(data.getData().cmd)).getFileName().toString();
          if (PRETTYPRINT.getValue(data.getData().cmd).endsWith(".cd")){
            cdName = cdName.substring(0, cdName.length() - 3);
          } else if (PRETTYPRINT.getValue(data.getData().cmd).endsWith(".umlp")) {
            cdName = cdName.substring(0, cdName.length() - 5);
          } else {
            cdName = "Merge";
          }
        }
        ASTCDCompilationUnit merge = CDMerge.merge(mergeSet, cdName,
            Set.of(MergeParameter.FAIL_AMBIGUOUS));
        if (merge != null){
          // Continue with the merged AST
          data.setAst(merge);
        } else {
          // Abort (return false) and report error on unsuccessful merge
          Log.error("Merge was unsuccessful");
          return false;
        }
      }
      return true;
    });*/
  }

  public final IOperation.ISimpleOperation<DataWithAst> model_pipe_parse = data -> {
    ASTCDCompilationUnit ast = data.getTool().parse(data.getData().inputFile.getPath());
    Reporting.reportParseInputFile(data.getData().inputFile.toPath().toAbsolutePath(),
        getReportingOnModelName(
            data.getData().inputFile.toPath())); // TODO: causes NullPointerException
    data.setAst(ast);
  };

  public final IOperation.ISimpleOperation<DataWithAst> model_pipe_after_parse_trafos =
      data -> data.getTool().runAfterParseTrafo(data.getAst());

  public final IOperation.ISimpleOperation<DataWithAst> model_pipe_symbol_table =
      data -> data.getTool().createSymbolTable(data.getAst());

  public final IOperation.ISimpleOperation<DataWithAst> model_pipe_default_cocos =
      data -> data.getTool().runDefaultCoCos(data.getAst());

  public final IOperation.ISimpleOperation<DataWithAst> model_pipe_default_trafos =
      data -> data.getTool().runDefaultTrafos(data.getAst());

  public final IOperation.ISimpleOperation<DataWithAst> model_pipe_recreate_symbol_table =
      data -> data.getTool().recreateSymbolTable(data.getAst());

  public final IOperation.ISimpleOperation<DataWithAst> model_pipe_set_options = data -> {
    // Do not gen Forms for singletons
    for (String singletonClass : data.getTool().getSingletonClasses(data.getAst())) {
      data.addExtraOption("ExcludeForm", singletonClass);
      data.addExtraOption("NoOverview", singletonClass);
    }
  };

  public final IOperation.ISimpleOperation<DataWithAst> model_pipe_generate_gui = data -> {
    CD2GUITool guiTool =
        new CD2GUITool(data.getAst(), data.getData().outDir, data.getData().hwcPath,
            data.getExtraOptions().toArray(new String[0]));
    guiTool.generateGUI();
  };

  public final IOperation.ISimpleOperation<DataWithAst> model_pipe_add_gem_id_trafo = data -> {
    new UMLAddGemIdTrafo(UMLPMill.inheritanceTraverser()).transform(data.getAst());
  };

  public final IOperation.ISimpleOperation<DataWithAst> model_pipe_recreate_and_store_symbol_table =
      data -> {
        // TODO (MB): Ist diese Lösung okay?
        // Create a customized symbol table for the generated GUI-models
        IUMLPArtifactScope symbolTable = data.getTool().recreateSymbolTable(data.getAst());
        Path symbolPath = Paths.get(
            data.getData().outDir.getPath() + File.separator + "symbols" + File.separator
                + Names.getPathFromPackage(symbolTable.getPackageName()) + File.separator
                + symbolTable.getName() + ".umlpsym");
        JsonPrinter.disableIndentation();
        data.getTool().storeSymbols(symbolTable, symbolPath.toString());
      };

  Pipeline<DataWithAst> createModelPipeline() {
    Pipeline<DataWithAst> pipe = new Pipeline<>();
    pipe.register("model_pipe_parse", model_pipe_parse);
    pipe.register("model_pipe_after_parse_trafos", model_pipe_after_parse_trafos);
    pipe.register("model_pipe_symbol_table", model_pipe_symbol_table);
    pipe.register("model_pipe_default_cocos", model_pipe_default_cocos);
    pipe.register("model_pipe_default_trafos", model_pipe_default_trafos);
    pipe.register("model_pipe_recreate_symbol_table", model_pipe_recreate_symbol_table);
    pipe.register("model_pipe_set_options", model_pipe_set_options);
    pipe.register("model_pipe_generate_gui", model_pipe_generate_gui);
    pipe.register("model_pipe_add_gem_id_trafo", model_pipe_add_gem_id_trafo);
    pipe.register("model_pipe_recreate_and_store_symbol_table",
        model_pipe_recreate_and_store_symbol_table);

    pipe.addFinally(data -> Reporting.flush(data.getAst()));
    return pipe;
  }
  
  public final IOperation.ISimpleOperation<DataWithAst> create_pipe_afterParseTrafo = data -> {
    data.getTool().runAfterParseTrafo(data.getAst());
  };
  public final IOperation.ISimpleOperation<DataWithAst> create_pipe_create_st = data -> {
    data.getTool().createSymbolTable(data.getAst());
  };
  public final IOperation.ISimpleOperation<DataWithAst> create_pipe_run_def_coco = data -> {
    data.getTool().runDefaultCoCos(data.getAst());
  };
  public final IOperation.ISimpleOperation<DataWithAst> create_pipe_run_def_trafos = data -> {
    data.getTool().runDefaultTrafos(data.getAst());
  };
//  public final IOperation.ISimpleOperation<DataWithAst> create_pipe_recreate_st = data -> {
//    data.setSymbolTable(data.getTool().recreateSymbolTable(data.getAst()));
//  };
  
  <T extends DataWithAst> Pipeline<T> createLoadModelPathPipe() {
    Pipeline<DataWithAst> pipe = new Pipeline<>();
    pipe.register("create_pipe_afterParseTrafo", create_pipe_afterParseTrafo);
    pipe.register("create_pipe_create_st", create_pipe_create_st);
    pipe.register("create_pipe_run_def_coco", create_pipe_run_def_coco);
    pipe.register("create_pipe_run_def_trafos", create_pipe_run_def_trafos);
//    pipe.register("create_pipe_recreate_st", create_pipe_recreate_st);
    return (Pipeline<T>) pipe;
  }

  protected final String getReportingOnModelName(Path modelPath) {
    return modelPath.getFileName().toString()
        .replace(".", "__"); // TODO: Is this name unique? Should it be unique?
  }

  public static class Data implements DataWithAst {

    final UMLPPipelineData data;
    ASTCDCompilationUnit ast;
    List<String> extraOptions;

    public Data(UMLPPipelineData data) {
      this.data = data;
      this.extraOptions = new ArrayList<>();
    }

    @Override
    public UMLPPipelineData getData() {
      return data;
    }

    @Override
    public UMLPTool getTool() {
      return data.tool;
    }

    @Override
    public ASTCDCompilationUnit getAst() {
      return ast;
    }

    @Override
    public void setAst(ASTCDCompilationUnit ast) {
      this.ast = ast;
    }

    @Override
    public List<String> getExtraOptions() {
      return extraOptions;
    }

    @Override
    public void addExtraOption(String key, String value) {
      extraOptions.add("-");
      extraOptions.add(key);
      extraOptions.add(value);
    }
  }

  public interface DataWithAst {

    UMLPPipelineData getData();

    UMLPTool getTool();

    ASTCDCompilationUnit getAst();

    void setAst(ASTCDCompilationUnit ast);

    List<String> getExtraOptions();

    void addExtraOption(String key, String value);
  }

  void addBuiltInTypes() {
    BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope(), false);
  }

}
