package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

import java.util.Collection;
import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord") //compiler can't work with records
public final class ObserverGenerationDetails {

  private final ASTCDType domainClass;
  private final Collection<ASTCDMethodSignature> notifyMethodSignatures;
  private final Collection<ASTMCImportStatement> importStatements;

  public ObserverGenerationDetails(ASTCDType domainClass, Collection<ASTCDMethodSignature> notifyMethodSignatures, Collection<ASTMCImportStatement> importStatements) {
    this.domainClass = domainClass;
    this.notifyMethodSignatures = notifyMethodSignatures;
    this.importStatements = importStatements;
  }

  public ASTCDType domainClass() {
    return domainClass;
  }

  public Collection<ASTCDMethodSignature> notifyMethodSignatures() {
    return notifyMethodSignatures;
  }

  public Collection<ASTMCImportStatement> importStatements() {
    return importStatements;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (ObserverGenerationDetails) obj;
    return Objects.equals(this.domainClass,
                          that.domainClass) && Objects.equals(this.notifyMethodSignatures,
                                                              that.notifyMethodSignatures) && Objects.equals(
        this.importStatements,
        that.importStatements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(domainClass, notifyMethodSignatures, importStatements);
  }

  @Override
  public String toString() {
    return "ObserverGenerationDetails[" + "domainClass=" + domainClass + ", " + "notifyMethodSignatures=" + notifyMethodSignatures + ", " + "importStatements=" + importStatements + ']';
  }


}
