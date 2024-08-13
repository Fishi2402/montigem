package umlp.generator.test_infrastructure.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.domain.decorator.BEDecorator;
import umlp.generator.domain.decorator.DiagramObserverDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.domain.decorator.ShellAPIDecorator;

public class TestInfrastructureDecorator extends BEDecorator {

  boolean playwright = false;

  public TestInfrastructureDecorator(GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex, hwPath);

    if (glex.hasGlobalVar("UMLPGeneratorConfig")) {
      playwright = UMLPGeneratorConfig.getInstance().isUsePlaywright();
    }
  }

  @Override
  public void transform(ASTCDCompilationUnit cdAST, ASTCDCompilationUnit decoratedAST) {
    String logName = TestInfrastructureDecorator.class.getName();

    // START ARTIFACT PROVIDER
    //we need the artifacts of the following observers. The simplest approach is to just call them
    //again and throw away the created classes
    var dummyAST = new ASTCDCompilationUnit();

    ObserverDecorator observerDecorator = new ObserverDecorator(glex);
    observerDecorator.transform(cdAST, dummyAST);

    Log.info("After artifact providing decorator: ObserverDecorator", logName);

    DiagramObserverDecorator diagramObserverDecorator = new DiagramObserverDecorator(glex);
    diagramObserverDecorator.transform(cdAST, dummyAST);
    Log.info("After artifact providing decorator: DiagramObserverDecorator", logName);

    // END ARTIFACT PROVIDER

    TestContextDecorator testContextDecorator = new TestContextDecorator(glex);
    testContextDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: TestContextDecorator", ShellAPIDecorator.class.getName());

    TestSetupDecorator testSetupDecorator = new TestSetupDecorator(glex,
                                                                   observerDecorator,
                                                                   diagramObserverDecorator);
    testSetupDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: TestSetupDecorator", TestSetupDecorator.class.getName());

    if (playwright) {
      E2ETestDecorator e2ETestDecorator = new E2ETestDecorator(glex);
      e2ETestDecorator.transform(cdAST, decoratedAST);
      Log.info("After decorator: E2ETestDecorator", logName);
    }
  }
}
