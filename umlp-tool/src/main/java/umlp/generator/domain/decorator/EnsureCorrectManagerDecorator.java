package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.AbstractDecorator;

public class EnsureCorrectManagerDecorator extends AbstractDecorator {

  //same package as normal test infrastructure
  public static final String PACKAGE = TestSetupDecorator.PACK_TEST_INFRASTRUCTURE;

  private final DiagramObserverDecorator diagramObserverDecorator;
  private final EnsureCorrectManager4Decorator ensureCorrectManager4Decorator;

  public EnsureCorrectManagerDecorator(GlobalExtensionManagement glex,
                                       DiagramObserverDecorator diagramObserverDecorator,
                                       EnsureCorrectManager4Decorator ensureCorrectManager4Decorator) {
    super(glex);
    this.diagramObserverDecorator = diagramObserverDecorator;
    this.ensureCorrectManager4Decorator = ensureCorrectManager4Decorator;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    var optGenerationSummary = diagramObserverDecorator.getGenerationSummary();
    if (optGenerationSummary.isPresent()) {
      ASTCDPackage aPackage = getPackage(origCD, decoratedCD, PACKAGE);
      aPackage.addCDElement(withOrigin(createEnsureCorrectManager(origCD,
                                                                  optGenerationSummary.get())));
    }
  }

  private ASTCDClass createEnsureCorrectManager(ASTCDCompilationUnit origCD,
                                                DiagramObserverDecorator.GenerationSummary diagramGenerationSummary) {

    return null; //TODO
  }
}
