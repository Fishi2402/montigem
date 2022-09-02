package gradle;

import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.CDMerge;
import de.se_rwth.commons.logging.Log;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import umlp.UMLPFullPrettyPrinter;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp._parser.UMLPParser;
import umlp.generator.CD2UMLPTrafo;
import umlp.generator.UMLP2CDTrafo;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public abstract class UMLPMergeTask extends SourceTask {

  @OutputFile
  public abstract RegularFileProperty getOutputFile();

  @TaskAction
  public void run() {
    Set<ASTCDCompilationUnit> cds = new LinkedHashSet<>();
    UMLPParser parser = umlp.UMLPMill.parser();

    try {
      for (File f : getSource()) {
        Optional<ASTUMLPCompilationUnit> astUMLP = parser.parse(f.getAbsolutePath());
        if (astUMLP.isEmpty()) {
          Log.error("Error Parsing UMLP Model " + f.getPath());
        }
        ASTCDCompilationUnit astCD = UMLP2CDTrafo.run(astUMLP.get());
        cds.add(astCD);
      }
    } catch (IOException e) {
      Log.error(e.getMessage());
    }


    ASTCDCompilationUnit merged = CDMerge.merge(cds);
    ASTUMLPCompilationUnit umlp = CD2UMLPTrafo.run(merged);
    UMLPFullPrettyPrinter umlpPrettyPrinter = new UMLPFullPrettyPrinter();
    umlp.accept(umlpPrettyPrinter.getTraverser());

    try (PrintWriter out = new PrintWriter(new FileOutputStream(getOutputFile().get().getAsFile()), true)) {
      out.println(umlpPrettyPrinter.getPrinter().getContent());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
