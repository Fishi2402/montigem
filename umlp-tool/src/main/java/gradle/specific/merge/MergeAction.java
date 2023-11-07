/* (c) https://github.com/MontiCore/monticore */
package gradle.specific.merge;

import com.google.common.collect.Sets;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.CDMerge;
import org.gradle.internal.FileUtils;
import org.gradle.workers.WorkAction;
import umlp.UMLPTool;
import umlp._prettyprint.UMLPFullPrettyPrinter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MergeAction implements WorkAction<MergeActionParameter> {
  @Override
  public void execute() {
    umlpMerge(this.getParameters().getInputs().getFiles(), getParameters().getOutputFile().getAsFile().get());
  }

  public static void umlpMerge(Set<File> inputFiles, File outputFile){

    // Parse and convert to CDCompilationUnit
    Set<ASTCDCompilationUnit> models = inputFiles.stream()
        .map(File::toPath)
        .map(Path::toAbsolutePath)
        .map(MergeAction::parseAndCheck)
        .collect(Collectors.toSet());

    String outputModelName = FileUtils.removeExtension(outputFile.getName());
    ASTCDCompilationUnit merge = CDMerge.merge(models, outputModelName, Sets.newHashSet());
    UMLPFullPrettyPrinter printer = new UMLPFullPrettyPrinter();
    merge.accept(printer.getTraverser());


    try (PrintWriter out = new PrintWriter(new FileOutputStream(outputFile), true)) {
      out.println(printer.getPrinter().getContent());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  protected static ASTCDCompilationUnit parseAndCheck(Path absolutePath) {
    UMLPTool tool = new UMLPTool();

    ASTCDCompilationUnit result = tool.parse(absolutePath.toString());

    tool.runAfterParseTrafo(result);
    tool.createSymbolTable(result);
    tool.runDefaultCoCos(result);

    return result;
  }
}
