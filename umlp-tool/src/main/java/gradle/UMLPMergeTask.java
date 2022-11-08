package gradle;

import de.monticore.cd4analysis.prettyprint.CD4AnalysisFullPrettyPrinter;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.CDMerge;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import umlp.UMLPFullPrettyPrinter;
import umlp.UMLPTool;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp.generator.CD2UMLPTrafo;
import umlp.generator.UMLP2CDTrafo;

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
    Set<ASTCDCompilationUnit> cds = getSource().getFiles().stream()
        .map(File::getAbsolutePath)
        .map(this::parseAndCheck)
        .map(UMLP2CDTrafo::run)
        .collect(Collectors.toSet());

    // merge
    ASTCDCompilationUnit merged = CDMerge.merge(cds);

    //CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter();
    //merged.accept(printer.getTraverser());


    // Convert back to UMLPCompilationUnit
    ASTUMLPCompilationUnit umlp = CD2UMLPTrafo.run(merged);
    UMLPFullPrettyPrinter printer = new UMLPFullPrettyPrinter();
    umlp.accept(printer.getTraverser());


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
