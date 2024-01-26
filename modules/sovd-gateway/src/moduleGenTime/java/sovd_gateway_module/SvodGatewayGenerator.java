package sovd_gateway_module;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import umlp.UMLPMill;

public class SvodGatewayGenerator {
  private final GeneratorSetup genSetup;

  public SvodGatewayGenerator(GeneratorSetup genSetup){
    this.genSetup = genSetup;
  }

  public void generate(ASTCDCompilationUnit decoratedAST){
    System.out.println("Hello world! Got cd \n" + UMLPMill.prettyPrint(decoratedAST, true));
  }
}
