/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.UMLPGeneratorConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BEDecorator extends AbstractDecorator {

  boolean neo4j = false;

  boolean spring = false;



  protected MCPath hwPath;

  protected Set<String> supportedDesers = new HashSet<>();

  public BEDecorator(GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex);
    this.hwPath = hwPath;

    initExtraDeSers();

    if(glex.hasGlobalVar("UMLPGeneratorConfig")){
      UMLPGeneratorConfig config = (UMLPGeneratorConfig) glex.getGlobalVar("UMLPGeneratorConfig");
      spring = config.isUseSpring();
    }
  }

  public BEDecorator(GlobalExtensionManagement glex, MCPath hwPath, boolean neo4j) {
    this(glex, hwPath);
    this.neo4j = neo4j;
  }

  private void initExtraDeSers() {
    // TODO: Why is there another set in CommandDecorator?
    supportedDesers.add("java.time.LocalDate");
    supportedDesers.add("java.time.LocalDateTime");
    supportedDesers.add("java.time.LocalTime");
  }

  @Override
  public void transform(ASTCDCompilationUnit cdAST, ASTCDCompilationUnit decoratedAST) {
    DomainClassDecorator domainClassDecorator;

    domainClassDecorator = new DomainClassDecorator(glex, hwPath);
    domainClassDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CreateDecorator", BEDecorator.class.getName());

    DomainInterfaceDecorator domainInterfaceDecorator = new DomainInterfaceDecorator(glex);
    domainInterfaceDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CreateDecorator", BEDecorator.class.getName());

    // Generate DAOs
    if(!neo4j) {
      DAODecorator daoDecorator = new DAODecorator(glex);
      daoDecorator.transform(cdAST, decoratedAST);
      Log.info("After decorator: DAODecorator", BEDecorator.class.getName());
    } else {
      DAODecorator daoDecorator = new DAONeo4jDecorator(glex);
      daoDecorator.transform(cdAST, decoratedAST);
      Log.info("After decorator: DAODecorator", BEDecorator.class.getName());
    }

    // Generate DeSer
    DeSerDecorator deSerDecorator = new DeSerDecorator(glex, hwPath, supportedDesers);
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

    ManagerDbObserverDecorator managerDbObserverDecorator = new ManagerDbObserverDecorator(glex);
    managerDbObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerDbObserverDecorator", BEDecorator.class.getName());

    DbObserverDecorator dbObserverDecorator= new DbObserverDecorator(glex);
    dbObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DbObserverDecorator", BEDecorator.class.getName());

    CommandSendingObserverDecorator cmdSendingDecorator = new CommandSendingObserverDecorator(glex);
    cmdSendingDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CommandSendingObserverDecorator", BEDecorator.class.getName());

    CommandObserverDecorator cmdDecorator = new CommandObserverDecorator(glex);
    cmdDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CommandObserverDecorator", BEDecorator.class.getName());

    AccessPolicyDecorator accessDecorator = new AccessPolicyDecorator(glex);
    accessDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: AccessPolicyDecorator", BEDecorator.class.getName());

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

    DelegationManagerDecorator delegationManagerDecorator = new DelegationManagerDecorator(glex);
    delegationManagerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DelegationManagerDecorator", BEDecorator.class.getName());

    ManagerImplDecorator managerImplDecorator = new ManagerImplDecorator(glex);
    managerImplDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerImplDecorator", BEDecorator.class.getName());

    ManagerClientImplDecorator managerClientImplDecorator = new ManagerClientImplDecorator(glex);
    managerClientImplDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerClientImplDecorator", BEDecorator.class.getName());

    ManagerMockDecorator managerMockDecorator = new ManagerMockDecorator(glex);
    managerMockDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ManagerMockDecorator", BEDecorator.class.getName());

    DummyProviderDecorator dummyProviderDecorator = new DummyProviderDecorator(glex);
    dummyProviderDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DummyProviderDecorator", BEDecorator.class.getName());

    ErrorObserverDecorator errorObserverDecorator = new ErrorObserverDecorator(glex);
    errorObserverDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: ErrorObserverDecorator", BEDecorator.class.getName());

    GemListAssocDecorator gemListAssocDecorator = new GemListAssocDecorator(glex);
    gemListAssocDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: GemListAssocDecorator", BEDecorator.class.getName());

    GemListDomainDecorator gemListDomainDecorator = new GemListDomainDecorator(glex);
    gemListDomainDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: GemListDomainDecorator", BEDecorator.class.getName());

    CommandObjectMapperDecorator commandObjectMapperDecorator = new CommandObjectMapperDecorator(glex);
    commandObjectMapperDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CommandObjectMapperDecorator", BEDecorator.class.getName());

    BaseClassObjectMapperDecorator baseClassObjectMapperDecorator = new BaseClassObjectMapperDecorator(glex);
    baseClassObjectMapperDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: BaseClassObjectMapperDecorator", BEDecorator.class.getName());

    CommandResultDeSerDecorator commandResultDeSerDecorator = new CommandResultDeSerDecorator(glex);
    commandResultDeSerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CommandResultDeSerDecorator", BEDecorator.class.getName());

    if (spring) {
      ShellAPIDecorator shellAPIDecorator = new ShellAPIDecorator(glex);
      shellAPIDecorator.transform(cdAST, decoratedAST);
      Log.info("After decorator: ShellAPIDecorator", ShellAPIDecorator.class.getName());

      ShellAPIClassDecorator shellApiClassDecorator = new ShellAPIClassDecorator(glex, hwPath, supportedDesers);
      shellApiClassDecorator.transform(cdAST, decoratedAST);
      Log.info("After decorator: ShellAPIClassDecorator", ShellAPIClassDecorator.class.getName());

      ServerApplicationDecorator serverApplicationDecorator = new ServerApplicationDecorator(glex, neo4j);
      serverApplicationDecorator.transform(cdAST, decoratedAST);
      Log.info("After decorator: ServerApplicationDecorator", BEDecorator.class.getName());

    }


    TestContextDecorator testContextDecorator = new TestContextDecorator(glex);
    testContextDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: TestContextDecorator", ShellAPIDecorator.class.getName());

    TestSetupDecorator testSetupDecorator = new TestSetupDecorator(glex, neo4j);
    testSetupDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: TestContextDecorator", TestSetupDecorator.class.getName());

    glex.replaceTemplate("cd2java.Imports", new TemplateHookPoint("umlp.Imports"));
  }


  public void addSupportedDeSer(String... deSers) {
    this.supportedDesers.addAll(Arrays.asList(deSers));
  }

  public void setNeo4j(boolean neo4j) {
    this.neo4j = neo4j;
  }

  public void setSpring(boolean shellAPI) {
    this.spring = shellAPI;
  }
}
