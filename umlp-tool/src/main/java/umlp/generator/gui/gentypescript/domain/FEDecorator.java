/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import umlp.MillConf;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.BEDecorator;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.ClientDelegationManagerDecorator;
import umlp.generator.domain.decorator.CommandObjectMapperDecorator;
import umlp.generator.domain.decorator.CommandObserverDecorator;
import umlp.generator.domain.decorator.CommandResultDeSerDecorator;
import umlp.generator.domain.decorator.DeSerBuilderDecorator;
import umlp.generator.domain.decorator.DeSerDecorator;
import umlp.generator.domain.decorator.DiagramObserverDecorator;
import umlp.generator.domain.decorator.DomainClassDecorator;
import umlp.generator.domain.decorator.DomainInterfaceDecorator;
import umlp.generator.domain.decorator.ErrorCollectorDecorator;
import umlp.generator.domain.decorator.ErrorObserverDecorator;
import umlp.generator.domain.decorator.GemListAssocDecorator;
import umlp.generator.domain.decorator.GemListDomainDecorator;
import umlp.generator.domain.decorator.ManagerClientImplDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.domain.decorator.ManagerMockDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.domain.decorator.ValidationResultDeSerDecorator;
import umlp.generator.domain.decorator.ValidationResultDecorator;
import umlp.generator.gui.gentypescript.commands.TSAccessPolicyDecorator;

import java.nio.file.Path;

public class FEDecorator extends BEDecorator {

  protected final Path outPath;

  public FEDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    super(glex, hwPath);
    this.outPath = outPath;
  }

  @Override
  public void transform(ASTCDCompilationUnit cdAST, ASTCDCompilationUnit decoratedAST) {
    DomainClassDecorator domainClassDecorator;
    domainClassDecorator = new TSDomainClassDecorator(glex, hwPath, outPath);
    domainClassDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: CreateDecorator", FEDecorator.class.getName());

    DomainInterfaceDecorator domainInterfaceDecorator = new TSDomainInterfaceDecorator(glex, hwPath, outPath);
    domainInterfaceDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: CreateDecorator", FEDecorator.class.getName());

    // Don't generate DAOs
    /*
    if(!neo4j) {
      DAODecorator daoDecorator = new DAODecorator(glex);
      daoDecorator.transform(cdAST, decoratedAST);
      Log.debug("After decorator: DAODecorator", FEDecorator.class.getName());
    } else {
      DAODecorator daoDecorator = new DAONeo4jDecorator(glex);
      daoDecorator.transform(cdAST, decoratedAST);
      Log.debug("After decorator: DAODecorator", FEDecorator.class.getName());
    }
    /**/

    // Generate DeSer
    DeSerDecorator deSerDecorator = new TSDeSerDecorator(glex, hwPath, outPath, supportedDesers);
    deSerDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: DeSerDecorator", FEDecorator.class.getName());

    // Generate builder classes
    BuilderDecorator builderDecorator = new TSBuilderDecorator(glex, hwPath, outPath);
    builderDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: BuilderDecorator", FEDecorator.class.getName());

    // Generate DeSer builder classes
    DeSerBuilderDecorator deSerBuilderDecorator = new TSDeSerBuilderDecorator(glex, hwPath, outPath);
    deSerBuilderDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: DeSerBuilderDecorator", FEDecorator.class.getName());

    // Generate ErrorCollector classes
    ErrorCollectorDecorator errorCollectorDecorator = new TSErrorCollectorDecorator(glex, hwPath, outPath);
    errorCollectorDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: ErrorCollectorDecorator", FEDecorator.class.getName());

    ObserverDecorator observerDecorator = new TSObserverDecorator(glex, hwPath, outPath);
    observerDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: TSObserverDecorator", FEDecorator.class.getName());

    ObserverDecorator observerMixinDecorator = new TSObserverMixinDecorator(glex, hwPath, outPath);
    observerMixinDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: TSObserverMixinDecorator", FEDecorator.class.getName());

    TSAccessPolicyDecorator tsAccessPolicyDecorator = new TSAccessPolicyDecorator(glex, hwPath, outPath);
    tsAccessPolicyDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: TSAccessPolicyDecorator", FEDecorator.class.getName());

    /*
    ManagerDbObserverDecorator managerDbObserverDecorator = new ManagerDbObserverDecorator(glex);
    managerDbObserverDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: ManagerDbObserverDecorator", FEDecorator.class.getName());

    DbObserverDecorator dbObserverDecorator= new DbObserverDecorator(glex);
    dbObserverDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: DbObserverDecorator", FEDecorator.class.getName());

    CommandSendingObserverDecorator cmdSendingDecorator = new TSCommandSendingObserverDecorator(glex);
    cmdSendingDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: CommandSendingObserverDecorator", FEDecorator.class.getName());
    /**/

    CommandObserverDecorator cmdDecorator = new TSCommandObserverDecorator(glex, hwPath, outPath);
    cmdDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: CommandObserverDecorator", FEDecorator.class.getName());

    // Generate DiagramObserver classes
    DiagramObserverDecorator diagramObserverDecorator = new TSDiagramObserverDecorator(glex, hwPath, outPath);
    diagramObserverDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: DiagramObserverDecorator", FEDecorator.class.getName());

    // Generate manager classes
    ManagerDecorator managerDecorator = new TSManagerDecorator(glex, hwPath, outPath);
    managerDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: ManagerDecorator", FEDecorator.class.getName());

    /*
    ManagerImplDecorator managerImplDecorator = new ManagerImplDecorator(glex);
    managerImplDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: ManagerImplDecorator", FEDecorator.class.getName());
    /**/

    ManagerClientImplDecorator managerClientImplDecorator = new TSManagerClientImplDecorator(glex, hwPath, outPath);
    managerClientImplDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: ManagerClientImplDecorator", FEDecorator.class.getName());

    TSClientDelegationManagerDecorator clientDelegationManagerDecorator = new TSClientDelegationManagerDecorator(glex, hwPath, outPath);
    clientDelegationManagerDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: ClientDelegationManagerDecorator", FEDecorator.class.getName());

    ManagerMockDecorator managerMockDecorator = new TSManagerMockDecorator(glex, hwPath, outPath);
    managerMockDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: ManagerMockDecorator", FEDecorator.class.getName());

    /*
    DummyProviderDecorator dummyProviderDecorator = new DummyProviderDecorator(glex);
    dummyProviderDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: DummyProviderDecorator", FEDecorator.class.getName());
    /**/

    ErrorObserverDecorator errorObserverDecorator = new TSErrorObserverDecorator(glex, hwPath, outPath);
    errorObserverDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: ErrorObserverDecorator", FEDecorator.class.getName());

    GemListAssocDecorator gemListAssocDecorator = new TSGemListAssocDecorator(glex, hwPath, outPath);
    gemListAssocDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: GemListAssocDecorator", FEDecorator.class.getName());

    GemListDomainDecorator gemListDomainDecorator = new TSGemListDomainDecorator(glex, hwPath, outPath);
    gemListDomainDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: GemListDomainDecorator", FEDecorator.class.getName());

    CommandObjectMapperDecorator commandObjectMapperDecorator = new TSCommandObjectMapperDecorator(glex, hwPath, outPath);
    commandObjectMapperDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: CommandObjectMapperDecorator", FEDecorator.class.getName());

    BaseClassObjectMapperDecorator baseClassObjectMapperDecorator = new TSBaseClassObjectMapperDecorator(glex, hwPath, outPath);
    baseClassObjectMapperDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: BaseClassObjectMapperDecorator", FEDecorator.class.getName());

    CommandResultDeSerDecorator commandResultDeSerDecorator = new TSCommandResultDeSerDecorator(glex, hwPath, outPath);
    commandResultDeSerDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: CommandResultDeSerDecorator", FEDecorator.class.getName());

    if(hasOCL) {
      ValidationResultDecorator validationResultDecorator = new TSValidationResultDecorator(glex);
      validationResultDecorator.transform(cdAST, decoratedAST);
      Log.debug("After decorator: ValidationResultDecorator", FEDecorator.class.getName());
      ValidationResultDeSerDecorator validationResultDeSerDecorator = new TSValidationResultDeSerDecorator(glex);
      validationResultDeSerDecorator.transform(cdAST, decoratedAST);
      Log.debug("After decorator: ValidationResultDeSerDecorator", FEDecorator.class.getName());
      // OCLMill was used in the decorators. To assure UMLP functionality initialize the UMLP-Mill
    }

    /*
    TestContextDecorator testContextDecorator = new TestContextDecorator(glex);
    testContextDecorator.transform(cdAST, decoratedAST);
    Log.debug("After decorator: TestContextDecorator", FEDecorator.class.getName());
    /**/

    glex.replaceTemplate("cd2java.Imports", new TemplateHookPoint("umlp.Imports"));
  }

}
