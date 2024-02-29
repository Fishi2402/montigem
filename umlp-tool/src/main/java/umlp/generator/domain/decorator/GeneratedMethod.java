package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;

public class GeneratedMethod {

  private final String template;
  private final ASTCDMethodSignature signature;

  public GeneratedMethod(String template, ASTCDMethodSignature signature) {
    this.template = template;
    this.signature = signature;
  }

  public String template() {
    return template;
  }

  public ASTCDMethodSignature signature() {
    return signature;
  }
}
