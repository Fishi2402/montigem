package de.monticore.doc;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._parser.GUIDSLParser;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MontigemDocTool {

  public static void main(String[] args) {
    Options options = new Options();
    Option i = Option.builder("i").longOpt("input").hasArg()
        .desc("Directory where documented GUI models are").required().build();
    Option md = Option.builder().longOpt("md").hasArg()
        .desc("Directory where generated MD files will be").build();
    Option latex = Option.builder().longOpt("latex").hasArg()
        .desc("Directory where the separate generated LaTeX files will be as independent files").build();
    Option mainLatex = Option.builder().longOpt("mainLatex").hasArg()
        .desc("Directory where generated LaTeX files composed as one file will be").build();
    Option examples = Option.builder().longOpt("examples").hasArg()
        .desc("Directory where example GUI models are").build();
    Option images = Option.builder().longOpt("images").hasArg()
        .desc("Directory where example images are").build();
    Option hwc = Option.builder().longOpt("hwc").hasArg()
        .desc("Directory where hand-written extensions of example GUI models are").build();
    Option cd = Option.builder().longOpt("cd").hasArg()
        .desc("Directory where CDs used in documented GUI models are").build();
    Option preProc = Option.builder().longOpt("preproc").hasArg()
        .desc("A preprocessing phase is added to the example files").build();

    OptionGroup output = new OptionGroup();
    output
        .addOption(md)
        .addOption(latex)
        .addOption(mainLatex)
        .setRequired(true);

    options
        .addOption(i)
        .addOptionGroup(output)
        .addOption(examples)
        .addOption(images)
        .addOption(hwc)
        .addOption(cd)
        .addOption(preProc);

    info("Started doc extraction");

    CommandLineParser parser = new DefaultParser();
    try {
      CommandLine cmd = parser.parse(options, args);

      File outDir;
      if (cmd.hasOption("md")) {
        outDir = Paths.get(cmd.getOptionValue("md")).toFile();
      }
      else if (cmd.hasOption("latex")) {
        outDir = Paths.get(cmd.getOptionValue("latex")).toFile();
      }
      else {
        outDir = Paths.get("./md").toFile();
        Log.warn("0xD0011 Output directory and output format were not"
            + " specified, setting a default directory `./md` and output format"
            + " Markdown. Please specify one using either `--md` or `--latex`"
            + " flag. For example:\n\t gradle --md ./build/md"
        );
      }

      String[] iDirArray = cmd.getOptionValues("i");
      List<Path> guiComponentModelDirList = new ArrayList<>();
      for (String dir : iDirArray) {
        Path dirPath = Paths.get(dir);
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
          Log.error("0xD000B Provided component model directory is not a valid directory");
          return;
        }
        guiComponentModelDirList.add(dirPath);
      }

      Optional<Path> guiExampleModelDir = Optional.empty();
      if (cmd.hasOption("examples")) {
        guiExampleModelDir = Optional.of(Paths.get(cmd.getOptionValue("examples")));
        Path dir = guiExampleModelDir.get();
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
          Log.error("0xD000A Provided example model directory is not a valid directory");
          return;
        }
      }

      Optional<Path> imageExampleDir = Optional.empty();
      if (cmd.hasOption("images")) {
        imageExampleDir = Optional.of(Paths.get(cmd.getOptionValue("images")));
        Path dir = imageExampleDir.get();
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
          Log.error("0xD0009 Provided example image directory is not a valid directory");
          return;
        }
      }

      Optional<Path> codeExampleDir = Optional.empty();
      if (cmd.hasOption("hwc")) {
        codeExampleDir = Optional.of(Paths.get(cmd.getOptionValue("hwc")));
        Path dir = codeExampleDir.get();
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
          Log.error("0xD0008 Provided example code directory is not a valid directory");
          return;
        }
      }

      List<Path> cdTypesDirList = new ArrayList<>();
      if (cmd.hasOption("cd")) {
        String[] cdDirArray = cmd.getOptionValues("cd");
        for (String dir : cdDirArray) {
          Path dirPath = Paths.get(dir);
          if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            Log.error("0xD0007 Provided CD models directory is not a valid directory");
            return;
          }
          cdTypesDirList.add(dirPath);
        }
      }

      GUIDSLMill.init();
      CD4CodeMill.init();

      List<GUIDocArtifact> docs = new ArrayList<>();
      try {
        for (Path guiComponentModelDir : guiComponentModelDirList) {
          Files.walkFileTree(guiComponentModelDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
              if (file.toString().endsWith("gui")) {
                GUIDocArtifact doc = new GUIDocArtifact(outDir.toPath());
                ASTGUICompilationUnit ast = parseGUI(file.toString());
                doc.setComponentAST(ast);
                doc.setComponentLibraryName(file.getParent().getFileName().toString());
                try {
                  doc.setComponentModel(Files.readString(file));
                } catch (IOException e) {
                  Log.error("0xD0006 Error reading file contents: " + e.getMessage());
                }
                docs.add(doc);
              }

              return FileVisitResult.CONTINUE;
            }
          });
        }

        for (Path cdTypesDir : cdTypesDirList) {
          Files.walkFileTree(cdTypesDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
              if (file.toString().endsWith("cd") || file.toString().endsWith("umlp")) {
                ASTCDCompilationUnit ast = parseCD(file.toString());
                if (ast == null) {
                  Log.warn("0xD0005 Something with CD parsing");
                }
                else {
                  for (GUIDocArtifact doc : docs) {
                    for (ASTMCImportStatement imp : doc.getComponentAST().getMCImportStatementList()) {
                      String typeName = ast.getCDDefinition().getName();
                      if (imp.getQName().contains(typeName)) {
                        try {
                          doc.addCD(typeName, ast, Files.readString(file));
                        } catch (IOException e) {
                          Log.error("0xD0004 Error reading file contents: " + e.getMessage());
                        }
                      }
                    }
                  }
                }
              }

              return FileVisitResult.CONTINUE;
            }
          });
        }

        List<String> componentNames = new ArrayList<>();
        Map<String, Boolean> isCDDisplayed = new HashMap<>();
        for (GUIDocArtifact doc : docs) {
          componentNames.add(doc.getComponentAST().getComponentName());
          for (String cdEl : doc.getCDList()) {
            if (!isCDDisplayed.containsKey(cdEl)) {
              isCDDisplayed.put(cdEl, false);
            }
          }
        }

        for (GUIDocArtifact doc : docs) {
          for (ASTMCImportStatement imp : doc.getComponentAST().getMCImportStatementList()) {
            for (String componentName : componentNames) {
              if (imp.getQName().contains(componentName) && !componentName.equals(doc.getComponentAST().getComponentName())) {
                doc.addRelatedComponent(componentName);
              }
            }
          }

          for (String cdEl : doc.getCDList()) {
            if (!isCDDisplayed.get(cdEl)) {
              doc.addCdTypesDisplay(cdEl);
              isCDDisplayed.put(cdEl, true);
            }
          }
        }

        if (guiExampleModelDir.isPresent()) {
          Files.walkFileTree(guiExampleModelDir.get(), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
              if (file.toString().endsWith("gui")) {
                String componentName = file.getFileName().toString();
                // Remove extension (if any)
                int dotIndex = componentName.lastIndexOf('.');
                if (dotIndex != -1) {
                  componentName = componentName.substring(0, dotIndex);
                }

                for (GUIDocArtifact doc : docs) {
                  try {
                    doc.addIfModelExample(Files.readString(file), componentName);
                  } catch (IOException e) {
                    Log.error("0xD0003 Error reading file contents: " + e.getMessage());
                  }
                }
              }

              return FileVisitResult.CONTINUE;
            }
          });
        }

        if (imageExampleDir.isPresent()) {
          Files.walkFileTree(imageExampleDir.get(), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
              // Split into component name and file extension
              String fileName = file.getFileName().toString();
              String componentName = "";
              String fileExtension = "";

              int dotIndex = fileName.lastIndexOf('.');
              if (dotIndex != -1) {
                componentName = fileName.substring(0, dotIndex);
                fileExtension = fileName.substring(dotIndex + 1);
              }

              if (fileExtension.equalsIgnoreCase("png")
                  || fileExtension.equalsIgnoreCase("jpg")
                  || fileExtension.equalsIgnoreCase("gif")
              ) {
                for (GUIDocArtifact doc : docs) {
                  doc.addIfImageExample(file, componentName);
                }
              }

              return FileVisitResult.CONTINUE;
            }
          });
        }

        if (codeExampleDir.isPresent()) {
          Files.walkFileTree(codeExampleDir.get(), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
              // TODO: ignore folders given as parameters
              if (dir.getFileName().toString().equals("build")) {
                return FileVisitResult.SKIP_SUBTREE;
              }
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
              if (file.toString().endsWith("ts") || file.toString().endsWith("java")) {
                String componentName = file.getFileName().toString();
                // Remove extension (if any)
                int dotIndex = componentName.lastIndexOf('.');
                if (dotIndex != -1) {
                  componentName = componentName.substring(0, dotIndex);
                }

                for (GUIDocArtifact doc : docs) {
                  try {
                    doc.addIfCodeExample(Files.readString(file), componentName);
                  } catch (IOException e) {
                    Log.error("0xD0002 Error reading file contents: " + e.getMessage());
                  }
                }
              }

              return FileVisitResult.CONTINUE;
            }
          });
        }
      } catch (IOException e) {
        Log.error("0xD0001 Error traversing the directory: " + e.getMessage());
      }

      if (cmd.hasOption("latex")) {
        GUI2LaTeX gui2Latex = new GUI2LaTeX(outDir);
        gui2Latex.generate(docs, Boolean.parseBoolean(cmd.getOptionValue("preproc")));
      }
      else {
        GUI2MD gui2MD = new GUI2MD(outDir);
        gui2MD.generateMain(docs);
        for (GUIDocArtifact doc : docs) {
          gui2MD.generate(doc);
        }
      }
    } catch (ParseException e) {
      Log.error("0xD0000 Could not process DocTool parameters: " + e.getMessage());
    }
  }

  protected static void info(String msg) {
    Log.info(msg, MontigemDocTool.class.getSimpleName());
  }

  protected static ASTGUICompilationUnit parseGUI(String model) {
    try {
      GUIDSLParser parser = GUIDSLMill.parser();
      Optional<ASTGUICompilationUnit> optAst = parser.parse(model);

      if (!parser.hasErrors() && optAst.isPresent()) {
        return optAst.get();
      }
      Log.error("0xD000B Model could not be parsed.");
    } catch (NullPointerException | java.io.IOException e) {
      Log.error("0xD000C Failed to parse " + model, e);
    }
    return null;
  }

  protected static ASTCDCompilationUnit parseCD(String model) {
    try {
      CD4CodeParser parser = CD4CodeMill.parser();
      Optional<ASTCDCompilationUnit> optAst = parser.parse(model);

      if (!parser.hasErrors() && optAst.isPresent()) {
        return optAst.get();
      }
      Log.error("0xD000D Model could not be parsed.");
    } catch (NullPointerException | java.io.IOException e) {
      Log.error("0xD000E Failed to parse " + model, e);
    }
    return null;
  }
}
