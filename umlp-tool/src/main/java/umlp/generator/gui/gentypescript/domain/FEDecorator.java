/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.decorator.BEDecorator;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.CommandObjectMapperDecorator;
import umlp.generator.domain.decorator.CommandObserverDecorator;
import umlp.generator.domain.decorator.CommandResultDeSerDecorator;
import umlp.generator.domain.decorator.CommandSendingObserverDecorator;
import umlp.generator.domain.decorator.DeSerBuilderDecorator;
import umlp.generator.domain.decorator.DeSerDecorator;
import umlp.generator.domain.decorator.DerivedClassDecorator;
import umlp.generator.domain.decorator.DiagramObserverDecorator;
import umlp.generator.domain.decorator.DomainClassDecorator;
import umlp.generator.domain.decorator.DomainInterfaceDecorator;
import umlp.generator.domain.decorator.DummyProviderDecorator;
import umlp.generator.domain.decorator.ErrorCollectorDecorator;
import umlp.generator.domain.decorator.ErrorObserverDecorator;
import umlp.generator.domain.decorator.GemListAssocDecorator;
import umlp.generator.domain.decorator.GemListDomainDecorator;
import umlp.generator.domain.decorator.ManagerClientImplDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.domain.decorator.ManagerMockDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.gui.gentypescript.commands.TSAccessPolicyDecorator;

public class FEDecorator extends BEDecorator {

  public FEDecorator(GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex, hwPath);
  }

  @Override
  public void transform(ASTCDCompilationUnit cdAST, ASTCDCompilationUnit decoratedAST) {
    DomainClassDecorator domainClassDecorator;

    domainClassDecorator = new TSDomainClassDecorator(glex, hwPath);
    domainClassDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CreateDecorator", FEDecorator.class.getName());

    DomainInterfaceDecorator domainInterfaceDecorator = new DomainInterfaceDecorator(glex);
    domainInterfaceDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CreateDecorator", FEDecorator.class.getName());

    // Don't generate DAOs
    /*
    if(!neo4j) {
      DAODecorator daoDecorator = new DAODecorator(glex);
      daoDecorator.transform(cdAST, decoratedAST);
      Log.info("After decorator: DAODecorator", FEDecorator.class.getName());
    } else {
      DAODecorator daoDecorator = new DAONeo4jDecorator(glex);
      daoDecorator.transform(cdAST, decoratedAST);
      Log.info("After decorator: DAODecorator", FEDecorator.class.getName());
    }
    /**/

    // Generate DeSer
    DeSerDecorator deSerDecorator = new TSDeSerDecorator(glex, hwPath, supportedDesers);
    deSerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DeSerDecorator", FEDecorator.class.getName());

    // Generate builder classes
    BuilderDecorator builderDecorator = new TSBuilderDecorator(glex);
    builderDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: BuilderDecorator", FEDecorator.class.getName());

    // Generate DeSer builder classes
    DeSerBuilderDecorator deSerBuilderDecorator = new TSDeSerBuilderDecorator(glex, hwPath);
    deSerBuilderDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DeSerBuilderDecorator", FEDecorator.class.getName());

    // Generate ErrorCollector classes
    ErrorCollectorDecorator errorCollectorDecorator = new TSErrorCollectorDecorator(glex);
    errorCollectorDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ErrorCollectorDecorator", FEDecorator.class.getName());

    ObserverDecorator observerDecorator = new TSObserverDecorator(glex);
    observerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ObserverDecorator", FEDecorator.class.getName());

    TSAccessPolicyDecorator tsAccessPolicyDecorator = new TSAccessPolicyDecorator(glex);
    tsAccessPolicyDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: TSAccessPolicyDecorator", FEDecorator.class.getName());

    /*
    ManagerDbObserverDecorator managerDbObserverDecorator = new ManagerDbObserverDecorator(glex);
    managerDbObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerDbObserverDecorator", FEDecorator.class.getName());

    DbObserverDecorator dbObserverDecorator= new DbObserverDecorator(glex);
    dbObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DbObserverDecorator", FEDecorator.class.getName());

    CommandSendingObserverDecorator cmdSendingDecorator = new TSCommandSendingObserverDecorator(glex);
    cmdSendingDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CommandSendingObserverDecorator", FEDecorator.class.getName());
    /**/

    CommandObserverDecorator cmdDecorator = new TSCommandObserverDecorator(glex);
    cmdDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CommandObserverDecorator", FEDecorator.class.getName());

    DerivedClassDecorator derivedClassDecorator = new TSDerivedClassDecorator(glex);
    derivedClassDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DerivedClassDecorator", FEDecorator.class.getName());

    // Generate DiagramObserver classes
    DiagramObserverDecorator diagramObserverDecorator = new TSDiagramObserverDecorator(glex);
    diagramObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DiagramObserverDecorator", FEDecorator.class.getName());

    // Generate manager classes
    ManagerDecorator managerDecorator = new TSManagerDecorator(glex);
    managerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerDecorator", FEDecorator.class.getName());

    /*
    ManagerImplDecorator managerImplDecorator = new ManagerImplDecorator(glex);
    managerImplDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerImplDecorator", FEDecorator.class.getName());
    /**/

    ManagerClientImplDecorator managerClientImplDecorator = new TSManagerClientImplDecorator(glex);
    managerClientImplDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerClientImplDecorator", FEDecorator.class.getName());

    ManagerMockDecorator managerMockDecorator = new TSManagerMockDecorator(glex);
    managerMockDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerMockDecorator", FEDecorator.class.getName());

    /*
    DummyProviderDecorator dummyProviderDecorator = new DummyProviderDecorator(glex);
    dummyProviderDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DummyProviderDecorator", FEDecorator.class.getName());
    /**/

    ErrorObserverDecorator errorObserverDecorator = new TSErrorObserverDecorator(glex);
    errorObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ErrorObserverDecorator", FEDecorator.class.getName());

    GemListAssocDecorator gemListAssocDecorator = new TSGemListAssocDecorator(glex);
    gemListAssocDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: GemListAssocDecorator", FEDecorator.class.getName());

    GemListDomainDecorator gemListDomainDecorator = new TSGemListDomainDecorator(glex);
    gemListDomainDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: GemListDomainDecorator", FEDecorator.class.getName());

    CommandObjectMapperDecorator commandObjectMapperDecorator = new TSCommandObjectMapperDecorator(glex);
    commandObjectMapperDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CommandObjectMapperDecorator", FEDecorator.class.getName());

    BaseClassObjectMapperDecorator baseClassObjectMapperDecorator = new TSBaseClassObjectMapperDecorator(glex);
    baseClassObjectMapperDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: BaseClassObjectMapperDecorator", FEDecorator.class.getName());

    CommandResultDeSerDecorator commandResultDeSerDecorator = new TSCommandResultDeSerDecorator(glex);
    commandResultDeSerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CommandResultDeSerDecorator", FEDecorator.class.getName());

    /*
    TestContextDecorator testContextDecorator = new TestContextDecorator(glex);
    testContextDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: TestContextDecorator", FEDecorator.class.getName());
    /**/

    glex.replaceTemplate("cd2java.Imports", new TemplateHookPoint("umlp.Imports"));
  }

}
