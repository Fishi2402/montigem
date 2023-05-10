/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.BEDecorator;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.CommandObjectMapperDecorator;
import umlp.generator.domain.decorator.CommandObserverDecorator;
import umlp.generator.domain.decorator.CommandResultDeSerDecorator;
import umlp.generator.domain.decorator.CommandSendingObserverDecorator;
import umlp.generator.domain.decorator.DAODecorator;
import umlp.generator.domain.decorator.DAONeo4jDecorator;
import umlp.generator.domain.decorator.DbObserverDecorator;
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
import umlp.generator.domain.decorator.ManagerDbObserverDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.domain.decorator.ManagerImplDecorator;
import umlp.generator.domain.decorator.ManagerMockDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.domain.decorator.ShellAPIClassDecorator;
import umlp.generator.domain.decorator.ShellAPIDecorator;
import umlp.generator.domain.decorator.TestContextDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    DeSerDecorator deSerDecorator = new DeSerDecorator(glex, hwPath, supportedDesers);
    deSerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DeSerDecorator", FEDecorator.class.getName());

    // Generate builder classes
    BuilderDecorator builderDecorator = new BuilderDecorator(glex);
    builderDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: BuilderDecorator", FEDecorator.class.getName());

    // Generate DeSer builder classes
    DeSerBuilderDecorator deSerBuilderDecorator = new DeSerBuilderDecorator(glex, hwPath);
    deSerBuilderDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DeSerBuilderDecorator", FEDecorator.class.getName());

    // Generate ErrorCollector classes
    ErrorCollectorDecorator errorCollectorDecorator= new ErrorCollectorDecorator(glex);
    errorCollectorDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ErrorCollectorDecorator", FEDecorator.class.getName());

    ObserverDecorator observerDecorator = new ObserverDecorator(glex);
    observerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ObserverDecorator", FEDecorator.class.getName());

    ManagerDbObserverDecorator managerDbObserverDecorator = new ManagerDbObserverDecorator(glex);
    managerDbObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerDbObserverDecorator", FEDecorator.class.getName());

    DbObserverDecorator dbObserverDecorator= new DbObserverDecorator(glex);
    dbObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DbObserverDecorator", FEDecorator.class.getName());

    CommandSendingObserverDecorator cmdSendingDecorator = new CommandSendingObserverDecorator(glex);
    cmdSendingDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CommandSendingObserverDecorator", FEDecorator.class.getName());

    CommandObserverDecorator cmdDecorator = new CommandObserverDecorator(glex);
    cmdDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CommandObserverDecorator", FEDecorator.class.getName());

    DerivedClassDecorator derivedClassDecorator= new DerivedClassDecorator(glex);
    derivedClassDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DerivedClassDecorator", FEDecorator.class.getName());

    // Generate DiagramObserver classes
    DiagramObserverDecorator diagramObserverDecorator = new DiagramObserverDecorator(glex);
    diagramObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DiagramObserverDecorator", FEDecorator.class.getName());

    // Generate manager classes
    ManagerDecorator managerDecorator = new ManagerDecorator(glex);
    managerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerDecorator", FEDecorator.class.getName());

    ManagerImplDecorator managerImplDecorator = new ManagerImplDecorator(glex);
    managerImplDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerImplDecorator", FEDecorator.class.getName());

    ManagerClientImplDecorator managerClientImplDecorator = new ManagerClientImplDecorator(glex);
    managerClientImplDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerClientImplDecorator", FEDecorator.class.getName());

    ManagerMockDecorator managerMockDecorator = new ManagerMockDecorator(glex);
    managerMockDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerMockDecorator", FEDecorator.class.getName());

    DummyProviderDecorator dummyProviderDecorator = new DummyProviderDecorator(glex);
    dummyProviderDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DummyProviderDecorator", FEDecorator.class.getName());

    ErrorObserverDecorator errorObserverDecorator = new ErrorObserverDecorator(glex);
    errorObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ErrorObserverDecorator", FEDecorator.class.getName());

    GemListAssocDecorator gemListAssocDecorator = new GemListAssocDecorator(glex);
    gemListAssocDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: GemListAssocDecorator", FEDecorator.class.getName());

    GemListDomainDecorator gemListDomainDecorator = new GemListDomainDecorator(glex);
    gemListDomainDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: GemListDomainDecorator", FEDecorator.class.getName());

    CommandObjectMapperDecorator commandObjectMapperDecorator = new CommandObjectMapperDecorator(glex);
    commandObjectMapperDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CommandObjectMapperDecorator", FEDecorator.class.getName());

    BaseClassObjectMapperDecorator baseClassObjectMapperDecorator = new BaseClassObjectMapperDecorator(glex);
    baseClassObjectMapperDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: BaseClassObjectMapperDecorator", FEDecorator.class.getName());

    CommandResultDeSerDecorator commandResultDeSerDecorator = new CommandResultDeSerDecorator(glex);
    commandResultDeSerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CommandResultDeSerDecorator", FEDecorator.class.getName());

    TestContextDecorator testContextDecorator = new TestContextDecorator(glex);
    testContextDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: TestContextDecorator", ShellAPIDecorator.class.getName());

    glex.replaceTemplate("cd2java.Imports", new TemplateHookPoint("umlp.Imports"));
  }

}
