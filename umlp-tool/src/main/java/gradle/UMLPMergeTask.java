package gradle;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import umlp.UMLPFullPrettyPrinter;
import umlp.UMLPMerge;
import umlp.UMLPTool;
import umlp._ast.ASTUMLPCompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class UMLPMergeTask extends SourceTask {

  @OutputFile
  public abstract RegularFileProperty getOutputFile();

  @TaskAction
  public void run() {
    // Parse and convert to CDCompilationUnit
    Set<ASTUMLPCompilationUnit> models = getSource().getFiles().stream()
        .map(File::getAbsolutePath)
        .map(this::parseAndCheck)
        .collect(Collectors.toSet());

    ASTUMLPCompilationUnit merge = UMLPMerge.merge(models,"Merge");
    UMLPFullPrettyPrinter printer = new UMLPFullPrettyPrinter();
    merge.accept(printer.getTraverser());


    try (PrintWriter out = new PrintWriter(new FileOutputStream(getOutputFile().get().getAsFile()), true)) {
      out.println(printer.getPrinter().getContent());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  protected ASTUMLPCompilationUnit parseAndCheck(String absolutePath) {
    UMLPTool tool = new UMLPTool();

    ASTUMLPCompilationUnit result = tool.parse(absolutePath);

    tool.runAfterParseTrafo(result);
    tool.createSymbolTable(result);
    tool.runDefaultCoCos(result);

    return result;
  }
}
