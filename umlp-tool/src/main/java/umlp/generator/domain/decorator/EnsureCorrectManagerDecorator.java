package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.AbstractDecorator;

public class EnsureCorrectManagerDecorator extends AbstractDecorator {

  //same package as normal test infrastructure
  public static final String PACKAGE = TestSetupDecorator.PACK_TEST_INFRASTRUCTURE;

  public EnsureCorrectManagerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage aPackage = getPackage(origCD, decoratedCD, PACKAGE);
    //TODO generate stuff
  }


}
