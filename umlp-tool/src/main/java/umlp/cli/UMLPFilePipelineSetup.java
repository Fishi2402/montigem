package umlp.cli;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.CDMerge;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.ocl.ocl._parser.OCLParser;
import de.monticore.ocl.util.SymbolTableUtil;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.tagging.tags.TagsMill;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import umlp.MCPathUtil;
import umlp.TagRelated;
import umlp.UMLPMill;
import umlp.UMLPTool;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._symboltable.UMLPSymbols2Json;
import umlp.cli.common.IOperation;
import umlp.cli.common.Pipeline;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.UMLPGenerator;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.gui.gentypescript.domain.TSUMLPGenerator;
import umlp.generator.gui.gentypescript.java2ts.ComponentObserverManagerGenerator;
import umlp.generator.util.UMLPSymbolTables;
import umlp.generator.validator.ocl.OCLASTPreProcessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static umlp.UMLPToolOptions.FOR_CLIENT;
import static umlp.UMLPToolOptions.GEN_SYMBOLS_ONLY;
import static umlp.UMLPToolOptions.GUI_GEN_JSWEET;
import static umlp.UMLPToolOptions.MERGE;
import static umlp.UMLPToolOptions.OCL;
import static umlp.UMLPToolOptions.PRETTYPRINT;
import static umlp.UMLPToolOptions.SYMTAB;

public class UMLPFilePipelineSetup {


  final IOperation<UMLPPipelineData> init;
  final IOperation.ISimpleOperation2<UMLPPipelineData> addBuiltInTypes;

  Pipeline<ModelPathData> loadModelPathPipe;
  final IOperation<UMLPPipelineData> loadModelpath;
  Pipeline<Data> astPipe;
  final IOperation.ISimpleOperation<UMLPPipelineData> loadAST;

  final IOperation<Data> mergeAST;
  final IOperation.ISimpleOperation<Data> storeSymbols;

  final IOperation.ISimpleOperation<Data> writeSymbolsToSTD;
  final IOperation<Data> loadOCL;

  final IOperation.ISimpleOperation<Data> prettyPrint;
  final IOperation.ISimpleOperation<Data> storeTags;
  final IOperation.ISimpleOperation<Data> setupGenerator;
  final IOperation.ISimpleOperation<Data> generate;

  UMLPFilePipelineSetup(UMLPToolPipelineSetup umlpPipeline) {
    umlpPipeline.umlp.register(umlpPipeline.setupUMLPReports);
    umlpPipeline.umlp.register(init = data -> {
      // If we were to with to use concrete syntax, we would have to use the UMLPTagDefinitionMill
      if (SYMTAB.isSelected(data.cmd)) {
        TagsMill.init(); // instead we only support generic tagging models
      }
      UMLPMill.init();
      UMLPMill.globalScope().setSymbolPath(data.importedSymbolPath);
      return true;
    });
    umlpPipeline.umlp.register(addBuiltInTypes = this::addBuiltInTypes);
    umlpPipeline.umlp.register(loadModelpath = data -> {
      String report_on = data.inputFile.getName().replace(".", "__");   // TODO: Is this name unique? Should it be unique?

      List<URL> models = MCPathUtil.getAllModels(data.modelPath, ".*[.]umlp");
      for (URL modelUrl : models) {
        try (Reader reader = new InputStreamReader(modelUrl.openStream())) {
          ASTCDCompilationUnit ast = UMLPMill.parser().parse(reader).orElseThrow();
          Reporting.reportParseInputFile(data.inputFile.toPath().toAbsolutePath(), report_on);
          loadModelPathPipe.doWork(new ModelPathData(data, ast));
        } catch (IOException e) {
          Log.error("Can not load model for url " + modelUrl);
          return false;
        }
      }
      return true;
    });
    // Pipeline dealing with how to load models into the model path
    loadModelPathPipe = createPipe(ModelPathData.class);

    // Next, we load the item into the AST
    umlpPipeline.umlp.register(loadAST = data -> {
      String report_on = data.inputFile.getName().replace(".", "__");   // TODO: Is this name unique? Should it be unique?

      // parse, create symbol table, check all cocos
      ASTCDCompilationUnit ast = data.tool.parse(data.inputFile.getPath());
      Reporting.reportParseInputFile(data.inputFile.toPath().toAbsolutePath(), report_on); // TODO: causes NullPointerException
      astPipe.doWork(new Data(data, ast));
    });
    // Pipeline for dealing with parsed ASTs
    astPipe = createPipe(Data.class);
    // Insert merge operation at the front of the ast pipeline
    astPipe.registerFirst(mergeAST = data -> {
      // First step of an AST: If --merge was provided, override AST with merged AST
      if (MERGE.isSelected(data.data.cmd)){
        Set<ASTCDCompilationUnit> mergeSet = new LinkedHashSet<>();
        mergeSet.add(data.data.tool.parse(data.data.inputFile.getPath()));
        // Parse and report parsing of merge-inputs
        for (String mergeFile : data.data.cmd.getOptionValues("mrg")) {
          File inputMergeFile = new File(mergeFile);
          mergeSet.add(data.data.tool.parse(inputMergeFile.getAbsolutePath()));
          // Note: Do NOT use reportParseInputFile, as it resets it. Instead, use reportOpenInputFile
          Reporting.reportOpenInputFile(inputMergeFile.toPath().toAbsolutePath().toString());
        }
        // Construct a name for the merged file
        String cdName = "Merge";
        if (PRETTYPRINT.isSelected(data.data.cmd)
            && PRETTYPRINT.getValue(data.data.cmd) != null ){
          cdName = Path.of(PRETTYPRINT.getValue(data.data.cmd)).getFileName().toString();
          if (PRETTYPRINT.getValue(data.data.cmd).endsWith(".cd")){
            cdName = cdName.substring(0, cdName.length() - 3);
          } else if (PRETTYPRINT.getValue(data.data.cmd).endsWith(".umlp")) {
            cdName = cdName.substring(0, cdName.length() - 5);
          } else {
            cdName = "Merge";
          }
        }
        ASTCDCompilationUnit merge = CDMerge.merge(mergeSet, cdName, new HashSet<>());
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
    });
    astPipe.register(storeSymbols = data -> {
      if (SYMTAB.isSelected(data.data.cmd) && SYMTAB.getValue(data.data.cmd) != null) {
        String s = SYMTAB.getValue(data.data.cmd);
        // store symbol table to passed file
        data.symbolPath = Paths.get(s + File.separator + Names.getPathFromPackage(data.symbolTable.getPackageName()) + File.separator + data.symbolTable.getName() + ".umlpsym");
        JsonPrinter.disableIndentation();
        // Due to our broken-on-purpose-language aggregation (Tags and UMLP initialized separately)
        // we can simply store the UMLP symbol table (and ignore any tags in this regard)
        data.data.tool.storeSymbols(data.symbolTable, data.symbolPath.toString());
      }
    });
    astPipe.register(writeSymbolsToSTD = data -> {
      if (SYMTAB.isSelected(data.data.cmd) && SYMTAB.getValue(data.data.cmd) == null) {
        //print (formatted!) symboltable to console
        UMLPSymbols2Json s2j = new UMLPSymbols2Json();
        JsonPrinter.enableIndentation();
        data.symbolTable.accept(s2j.getTraverser());
        System.out.println(s2j.getSerializedString());
      }
    });

    astPipe.register(loadOCL = data -> {
      if (SYMTAB.isSelected(data.data.cmd) && SYMTAB.getValue(data.data.cmd) != null) {
        if (OCL.isSelected(data.data.cmd)) {
          MCPath oclModelPath = new MCPath();
          for (String v : data.data.cmd.getOptionValues(OCL.getCMD())) {
            oclModelPath.addEntry(Paths.get(v));
          }
          List<URL> oclFiles = MCPathUtil.getAllModels(oclModelPath, ".*[.]ocl");
          if (!oclFiles.isEmpty()) {
            for (URL modelUrl : oclFiles) {
              try {
                File oclFile = new File(modelUrl.toURI());
                OCLParser oclParser = new OCLParser();
                Optional<ASTOCLCompilationUnit> oclCompilationUnit;
                try {
                  oclCompilationUnit = oclParser.parse(oclFile.getAbsolutePath());
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
                if (oclCompilationUnit.isEmpty()) {
                  Log.error("0xE0001 Could not parse OCL file");
                  return false;
                }
                ASTOCLCompilationUnit oCLAst = oclCompilationUnit.get();
                UMLPTool.getOclCompilationUnits().add(OCLASTPreProcessor.preProcessInvariants(oCLAst));
                SymbolTableUtil.prepareMill();
                UMLPSymbolTables.addCd4cSymbols();
                SymbolTableUtil.loadSymbolFile(data.symbolPath.toString());
                SymbolTableUtil.runSymTabGenitor(oCLAst);
                SymbolTableUtil.runSymTabCompleter(oCLAst);
              } catch (URISyntaxException e) {
                throw new RuntimeException(e);
              }
            }
            AbstractDecorator.hasOCL = true;
          }
        }
      }
      return true;
    });

    astPipe.register(prettyPrint = data -> {
      // pretty print model and either output on stdout or store to file
      if (PRETTYPRINT.isSelected(data.data.cmd)) {
        data.data.tool.prettyPrint(data.ast, PRETTYPRINT.getValue(data.data.cmd));
      }
    });
    astPipe.register(storeTags = data -> {
      TagRelated.doStoreTagStereos(data.ast, data.data.tool, data.symbolTable, data.data.cmd);
    });

    //
    astPipe.register(setupGenerator = data -> {
      data.genSetup = new GeneratorSetup();
      data.genSetup.setGlex(UMLPTool.createGlex(data.data.tool.getModuleConfigTemplates(data.data.cmd)));
      // set output directory
      data.genSetup.setOutputDirectory(data.data.outDir);

      // set hwc directories
      data.genSetup.setHandcodedPath(data.data.hwcPath);

      // set template path
      data.genSetup.setAdditionalTemplatePaths(data.data.fpPath.getEntries().stream().map(Path::toFile).collect(Collectors.toList()));
      CD4C.init(data.genSetup);
    });

    // Finally, generate
    astPipe.register(generate = data -> {
      UMLPGeneratorConfig config = UMLPGeneratorConfig.getInstance().initFromCmd(data.data.cmd);
      if (FOR_CLIENT.isSelected(data.data.cmd)) {
        TSUMLPGenerator generator = new TSUMLPGenerator(data.genSetup, config);
        String configTemplate = data.data.cmd.getOptionValue("ct", UMLPTool.DEFAULT_CONFIGTEMPLATE_GUI2JAVA2TS);
        generator.generate(data.ast, configTemplate);
      } else if (GUI_GEN_JSWEET.isSelected(data.data.cmd)) {
        ComponentObserverManagerGenerator componentObserverManagerGenerator = new ComponentObserverManagerGenerator(data.genSetup);
        componentObserverManagerGenerator.generate(data.ast);
      } else if (!GEN_SYMBOLS_ONLY.isSelected(data.data.cmd)) {
        // execute generator
        UMLPGenerator generator = new UMLPGenerator(data.genSetup, config);
        String configTemplate = data.data.cmd.getOptionValue("ct", UMLPTool.DEFAULT_CONFIGTEMPLATE);
        generator.generate(data.ast, configTemplate);
      }
    });

    astPipe.addFinally(data -> {
      // write reports
      Reporting.flush(data.ast);
    });

  }

  final IOperation.ISimpleOperation<DataWithCDASTAndTool> create_pipe_afterParseTrafo = data -> {
    data.getTool().runAfterParseTrafo(data.getAST());
  };
  final IOperation.ISimpleOperation<DataWithCDASTAndTool> create_pipe_create_st = data -> {
    data.getTool().createSymbolTable(data.getAST());
  };
  final IOperation.ISimpleOperation<DataWithCDASTAndTool> create_pipe_run_def_coco = data -> {
    data.getTool().runDefaultCoCos(data.getAST());
  };
  final IOperation.ISimpleOperation<DataWithCDASTAndTool> create_pipe_run_def_trafos = data -> {
    data.getTool().runDefaultTrafos(data.getAST());
  };
  final IOperation.ISimpleOperation<DataWithCDASTAndTool> create_pipe_recreate_st = data -> {
    data.setSymbolTable(data.getTool().recreateSymbolTable(data.getAST()));
  };

  <T extends DataWithCDASTAndTool> Pipeline<T> createPipe(Class<T> clazz) {
    Pipeline<DataWithCDASTAndTool> pipe = new Pipeline<>();
    pipe.register(create_pipe_afterParseTrafo);
    pipe.register(create_pipe_create_st);
    pipe.register(create_pipe_run_def_coco);
    pipe.register(create_pipe_run_def_trafos);
    pipe.register(create_pipe_recreate_st);
    return (Pipeline<T>) pipe;
  }

  static class ModelPathData implements DataWithCDASTAndTool {
    final UMLPPipelineData data;
    final ASTCDCompilationUnit ast;

    public ModelPathData(UMLPPipelineData data, ASTCDCompilationUnit ast) {
      this.data = data;
      this.ast = ast;
    }

    @Override
    public ASTCDCompilationUnit getAST() {
      return this.ast;
    }

    @Override
    public UMLPTool getTool() {
      return this.data.tool;
    }

    @Override
    public void setSymbolTable(IUMLPArtifactScope symbolTable) {
      // do nothing
    }
  }

  static class Data implements DataWithCDASTAndTool {
    final UMLPPipelineData data;
    ASTCDCompilationUnit ast;

    public Data(UMLPPipelineData data, ASTCDCompilationUnit ast) {
      this.data = data;
      this.ast = ast;
    }

    IUMLPArtifactScope symbolTable;
    Path symbolPath;
    GeneratorSetup genSetup;

    @Override
    public ASTCDCompilationUnit getAST() {
      return this.ast;
    }

    @Override
    public UMLPTool getTool() {
      return this.data.tool;
    }

    public void setAst(ASTCDCompilationUnit ast) {
      this.ast = ast;
    }

    @Override
    public void setSymbolTable(IUMLPArtifactScope symbolTable) {
      this.symbolTable = symbolTable;
    }
  }

  void addBuiltInTypes() {
    BuiltInTypes.addBuiltInTypes(UMLPMill.globalScope(), false);
  }


  interface DataWithCDAST {
    ASTCDCompilationUnit getAST();
    void setSymbolTable(IUMLPArtifactScope symbolTable);
  }

  interface DataWithTool {
    UMLPTool getTool();
  }

  interface DataWithCDASTAndTool extends DataWithCDAST, DataWithTool {
  }

}
