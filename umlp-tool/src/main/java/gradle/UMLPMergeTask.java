package gradle;

import com.google.common.collect.Sets;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.CDMerge;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import umlp.UMLPFullPrettyPrinter;
import umlp.UMLPTool;

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
    Set<ASTCDCompilationUnit> models = getSource().getFiles().stream()
        .map(File::getAbsolutePath)
        .map(this::parseAndCheck)
        .collect(Collectors.toSet());

    ASTCDCompilationUnit merge = CDMerge.merge(models, "Merge", Sets.newHashSet());
    UMLPFullPrettyPrinter printer = new UMLPFullPrettyPrinter();
    merge.accept(printer.getTraverser());


    try (PrintWriter out = new PrintWriter(new FileOutputStream(getOutputFile().get().getAsFile()), true)) {
      out.println(printer.getPrinter().getContent());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  protected ASTCDCompilationUnit parseAndCheck(String absolutePath) {
    UMLPTool tool = new UMLPTool();

    ASTCDCompilationUnit result = tool.parse(absolutePath);

    tool.runAfterParseTrafo(result);
    tool.createSymbolTable(result);
    tool.runDefaultCoCos(result);

    return result;
  }
}
