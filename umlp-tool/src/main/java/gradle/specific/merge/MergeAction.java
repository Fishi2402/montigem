/* (c) https://github.com/MontiCore/monticore */
package gradle.specific.merge;

import com.google.common.collect.Sets;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.CDMerge;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.gradle.common.GradleLog;
import de.monticore.umlstereotype.UMLStereotypeMill;
import de.se_rwth.commons.logging.Log;
import org.gradle.internal.FileUtils;
import org.gradle.workers.WorkAction;
import umlp.UMLPTool;
import umlp._prettyprint.UMLPFullPrettyPrinter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.gradle.internal.impldep.org.junit.Assert.fail;

public abstract class MergeAction implements WorkAction<MergeActionParameter> {
  @Override
  public void execute() {
    umlpMerge(this.getParameters().getInputs().getFiles(), getParameters().getOutputFile().getAsFile().get());
  }

  public static void umlpMerge(Set<File> inputFiles, File outputFile){
    GradleLog.init();
    try {

      // Parse and convert to CDCompilationUnit
      List<ASTCDCompilationUnit> models = inputFiles.stream()
          .sorted((f1, f2) -> f2.getName().compareTo(f1.getName()))
          .map(File::toPath)
          .map(Path::toAbsolutePath)
          .map(MergeAction::parseAndCheck)
          .collect(Collectors.toList());

      String outputModelName = FileUtils.removeExtension(outputFile.getName());
      ASTCDCompilationUnit merge = CDMerge.merge(models, outputModelName,
          Set.of(MergeParameter.FAIL_AMBIGUOUS));

      if (merge == null) {
        Log.error("Merge was unsuccessful");
        fail("Merge was unsuccessful");
        return;
      }

      // HOTFIX: copy all stereos
      var stereos = models.stream().filter(e->e.getCDDefinition().getModifier().isPresentStereotype())
              .map(e->e.getCDDefinition().getModifier().getStereotype().getValuesList())
          .flatMap(List::stream).collect(Collectors.toSet());

      if (!merge.getCDDefinition().getModifier().isPresentStereotype() && !stereos.isEmpty()) {
        Log.warn("[MERGE-HOTFIX] COPY ALL STEREOTYPES (" + stereos.size() + ") INTO THE MERGED MODEL APPLIED");
        merge.getCDDefinition().getModifier().setStereotype(
            UMLStereotypeMill.stereotypeBuilder()
                .addAllValues(stereos)
                .build()
        );
      }
      // end hotfix

      UMLPFullPrettyPrinter printer = new UMLPFullPrettyPrinter();
      merge.accept(printer.getTraverser());


      try (PrintWriter out = new PrintWriter(new FileOutputStream(outputFile), true)) {
        out.println(printer.getPrinter().getContent());
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    } finally {
      // https://github.com/gradle/gradle/issues/18313 - Gradle is memory hungry
      UMLPTool.cleanUpAfterMemoryLeakingSingletons();
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
