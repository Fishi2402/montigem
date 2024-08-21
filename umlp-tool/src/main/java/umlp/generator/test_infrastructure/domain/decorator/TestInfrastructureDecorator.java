package umlp.generator.test_infrastructure.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDCompilationUnitBuilder;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.domain.decorator.BEDecorator;
import umlp.generator.domain.decorator.DiagramObserverDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.domain.decorator.ShellAPIDecorator;

import java.util.StringJoiner;

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
    usedDecorators.clear();

    //we don't want to regenerate the classes, so we use a new decorated ast and throw it away afterward
    ASTCDCompilationUnit dummyAST = new ASTCDCompilationUnitBuilder().setCDDefinition(cdAST.getCDDefinition())
        .build();

    //we need some information from the backend generation - so we just do it again
    super.transform(cdAST, dummyAST);
    ObserverDecorator observerDecorator = getUsedDecorator(ObserverDecorator.class).orElseGet(() -> new ObserverDecorator(
        glex));
    DiagramObserverDecorator diagramObserverDecorator = getUsedDecorator(DiagramObserverDecorator.class).orElseGet(
        () -> new DiagramObserverDecorator(glex));

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

  //used to debug this decorator since gradle doesn't show me any stacktraces
  private static class ExceptionDisplay extends RuntimeException {

    public static ExceptionDisplay from(Throwable throwable) {
      return new ExceptionDisplay(buildMessage(throwable), throwable);
    }

    private static String buildMessage(Throwable throwable) {
      StringJoiner messageJoiner = new StringJoiner("\n");
      messageJoiner.add(throwable.toString());
      for (StackTraceElement element : throwable.getStackTrace()) {
        messageJoiner.add("\t" + element.toString());
      }
      Throwable cause = throwable.getCause();
      if (cause != null) {
        messageJoiner.add("caused by: " + buildMessage(cause));
      }
      return messageJoiner.toString();
    }

    private ExceptionDisplay(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
