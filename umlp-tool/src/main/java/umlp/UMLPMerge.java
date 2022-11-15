package umlp;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.CDMerge;
import de.monticore.cdmerge.config.MergeParameter;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp.generator.CD2UMLPTrafo;
import umlp.generator.UMLP2CDTrafo;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class serves as an API for CDMerge.
 */
public class UMLPMerge {

  /**
   * Uses CDMerge to merge input-CDs.
   * WARNING: Mill is reset during the procedure!
   * @param models to be merged
   * @param name of the resulting model
   * @return merge of input models
   */
  public static ASTUMLPCompilationUnit merge(Set<ASTUMLPCompilationUnit> models, String name) {
    ASTCDCompilationUnit cd =
        CDMerge.merge(models.stream().map(UMLP2CDTrafo::run).collect(Collectors.toSet()), name, new HashSet<>());
    UMLPMill.reset();
    UMLPMill.init();
    return CD2UMLPTrafo.run(cd);
  }

  /**
   * Uses CDMerge to merge input-CDs according to the given Merge Parameters.
   * WARNING: Mill is reset during the procedure!
   * @param models to be merged
   * @param name of the resulting model
   * @param parameters configures the merge
   * @return merge of input models
   */
  public static ASTUMLPCompilationUnit merge(Set<ASTUMLPCompilationUnit> models, String name,
      Set<MergeParameter> parameters) {
    ASTCDCompilationUnit cd =
        CDMerge.merge(models.stream().map(UMLP2CDTrafo::run).collect(Collectors.toSet()), name, parameters);
    UMLPMill.reset();
    UMLPMill.init();
    return CD2UMLPTrafo.run(cd);
  }

}
