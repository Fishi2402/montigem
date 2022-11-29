package umlp.generator.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;

public class BEDecorator extends AbstractDecorator {

  boolean neo4j = false;

  protected MCPath hwPath;

  public BEDecorator(GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex);
    this.hwPath = hwPath;
  }

  public BEDecorator(GlobalExtensionManagement glex, MCPath hwPath, boolean neo4j) {super((glex));
    this.neo4j = neo4j;
    this.hwPath = hwPath;
  }

  @Override
  public void transform(ASTCDCompilationUnit cdAST, ASTCDCompilationUnit decoratedAST) {
    DomainClassDecorator domainClassDecorator;

    if(neo4j) {
      domainClassDecorator = new DomainClassDecoratorForNeo4j(glex, hwPath);
      domainClassDecorator.transform(cdAST, decoratedAST);
      Log.info("After decorator: CreateDecorator", BEDecorator.class.getName());

      RepositoryDecorator repositoryDecorator = new RepositoryDecorator(glex);
      repositoryDecorator.transform(cdAST, decoratedAST);
      Log.info("After decorator: RepositoryDecorator", BEDecorator.class.getName());
    } else {
      domainClassDecorator = new DomainClassDecorator(glex, hwPath);
      domainClassDecorator.transform(cdAST, decoratedAST);
      Log.info("After decorator: CreateDecorator", BEDecorator.class.getName());
    }

    // Generate DeSer
    DeSerDecorator deSerDecorator = new DeSerDecorator(glex, hwPath);
    deSerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DeSerDecorator", BEDecorator.class.getName());

    // Generate builder classes
    BuilderDecorator builderDecorator = new BuilderDecorator(glex);
    builderDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: BuilderDecorator", BEDecorator.class.getName());

    // Generate DeSer builder classes
    DeSerBuilderDecorator deSerBuilderDecorator = new DeSerBuilderDecorator(glex, hwPath);
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

    GemListAssocDecorator gemListAssocDecorator = new GemListAssocDecorator(glex);
    gemListAssocDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: GemListAssocDecorator", BEDecorator.class.getName());

    GemListDomainDecorator gemListDomainDecorator = new GemListDomainDecorator(glex);
    gemListDomainDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: GemListDomainDecorator", BEDecorator.class.getName());

    TuplizerDecorator tuplitzerDecorator = new TuplizerDecorator(glex);
    tuplitzerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: TuplizerDecorator", BEDecorator.class.getName());
  }

}
