package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import umlp.generator.AbstractDecorator;

public class BEDecorator extends AbstractDecorator {

  public BEDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit cdAST, ASTCDCompilationUnit decoratedAST) {
    DomainClassDecorator domainClassDecorator = new DomainClassDecorator(glex);
    domainClassDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CreateDecorator", BEDecorator.class.getName());

    // Generate DeSer
    DeSerDecorator deSerDecorator = new DeSerDecorator(glex);
    deSerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DeSerDecorator", BEDecorator.class.getName());

    // Generate builder classes
    BuilderDecorator builderDecorator = new BuilderDecorator(glex);
    builderDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: BuilderDecorator", BEDecorator.class.getName());

    // Generate DeSer builder classes
    DeSerBuilderDecorator deSerBuilderDecorator = new DeSerBuilderDecorator(glex);
    deSerBuilderDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DeSerBuilderDecorator", BEDecorator.class.getName());

    // Generate ErrorCollector classes
    ErrorCollectorDecorator errorCollectorDecorator= new ErrorCollectorDecorator(glex);
    errorCollectorDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ErrorCollectorDecorator", BEDecorator.class.getName());

    ObserverDecorator observerDecorator = new ObserverDecorator(glex);
    observerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ObserverDecorator", BEDecorator.class.getName());

    DbObserverDecorator dbObserverDecorator= new DbObserverDecorator(glex);
    dbObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DbObserverDecorator", BEDecorator.class.getName());

    CommandSendingObserverDecorator cmdSendingDecorator = new CommandSendingObserverDecorator(glex);
    cmdSendingDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CommandSendingObserverDecorator", BEDecorator.class.getName());

    DerivedClassDecorator derivedClassDecorator= new DerivedClassDecorator(glex);
    derivedClassDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DerivedClassDecorator", BEDecorator.class.getName());

    // Generate DiagramObserver classes
    DiagramObserverDecorator diagramObserverDecorator = new DiagramObserverDecorator(glex);
    diagramObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DiagramObserverDecorator", BEDecorator.class.getName());

    // Generate manager classes
    ManagerDecorator managerDecorator = new ManagerDecorator(glex);
    managerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerDecorator", BEDecorator.class.getName());

    ManagerImplDecorator managerImplDecorator = new ManagerImplDecorator(glex);
    managerImplDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerImplDecorator", BEDecorator.class.getName());

    DummyProviderDecorator dummyProviderDecorator = new DummyProviderDecorator(glex);
    dummyProviderDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DummyProviderDecorator", BEDecorator.class.getName());

    ErrorObserverDecorator errorObserverDecorator = new ErrorObserverDecorator(glex);
    errorObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ErrorObserverDecorator", BEDecorator.class.getName());

    // TODO: remove
    /*TypeAdapterDecorator typeAdapterDecorator = new TypeAdapterDecorator(glex);
    typeAdapterDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: TypeAdapterDecorator", BEDecorator.class.getName());*/
  }

}
