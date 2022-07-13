/* (c) https://github.com/MontiCore/monticore */
package umlp.generator;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cd4code.trafo.CD4CodeTrafo4Defaults;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.logging.Log;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp.generator.decorator.*;
import umlp.generator.decorator.commands.*;

/**
 * configures and employs generators of individual UML/P languages to generate
 * code. Currently, it uses only the CDGenerator.
 *
 * Later do: make this customizable as Groovy script
 */
public class UMLPGenerator {

  protected CDGenerator cdGenerator;

  protected static CD4CodeFullPrettyPrinter astPrinter;

  protected CD4CodeAfterParseTrafo cD4CodeAfterParseTrafo;

  protected CD4CodeTrafo4Defaults cD4CodeTrafo4Defaults;

  protected CD4CodeDirectCompositionTrafo cD4CodeDirectCompositionTrafo;

  protected CDAssociationCreateFieldsFromNavigableRoles cDAssociationCreateFieldsFromNavigableRoles;

  protected DomainClassDecorator domainClassDecorator;

  protected BuilderDecorator builderDecorator;

  protected ErrorCollectorDecorator errorCollectorDecorator;

  protected DiagramObserverDecorator diagramObserverDecorator;

  protected ErrorObserverDecorator errorObserverDecorator;
  
  protected ObserverDecorator observerDecorator;
  
  protected DbObserverDecorator dbObserverDecorator;
  
  protected DerivedClassDecorator derivedClassDecorator;
  
  protected ManagerDecorator managerDecorator;

  protected ManagerImplDecorator managerImplDecorator;

  protected DummyProviderDecorator dummyProviderDecorator;

  protected TopDecorator topDecorator;
  
  protected CreateDecorator createDecorator;
  
  protected ErrorDecorator errorDecorator;
  
  protected DeleteDecorator deleteDecorator;
  
  protected GetDecorator getDecorator;
  
  protected IdDecorator idDecorator;
  
  protected SetDecorator setDecorator;
  
  protected SizeDecorator sizeDecorator;

  protected CommandObserverDecorator commandObserverDecorator;
  
  public UMLPGenerator(GeneratorSetup genSetup) {
    astPrinter = new CD4CodeFullPrettyPrinter();

    genSetup.getGlex().setGlobalValue("cdPrinter", new CdUtilsPrinter());

    cdGenerator = new CDGenerator(genSetup);
    cD4CodeAfterParseTrafo = new CD4CodeAfterParseTrafo();
    cD4CodeTrafo4Defaults = new CD4CodeTrafo4Defaults();
    cD4CodeDirectCompositionTrafo = new CD4CodeDirectCompositionTrafo();
    cDAssociationCreateFieldsFromNavigableRoles = new CDAssociationCreateFieldsFromNavigableRoles();
    cDAssociationCreateFieldsFromNavigableRoles.setTraverser(CD4CodeMill.traverser());
    
    createDecorator = new CreateDecorator(genSetup.getGlex());
    errorDecorator = new ErrorDecorator(genSetup.getGlex());
    deleteDecorator = new DeleteDecorator(genSetup.getGlex());
    getDecorator = new GetDecorator(genSetup.getGlex());
    idDecorator = new IdDecorator(genSetup.getGlex());
    sizeDecorator = new SizeDecorator(genSetup.getGlex());
    setDecorator = new SetDecorator(genSetup.getGlex());
    
    commandObserverDecorator = new CommandObserverDecorator(genSetup.getGlex());
    domainClassDecorator = new DomainClassDecorator(genSetup.getGlex());
    builderDecorator = new BuilderDecorator(genSetup.getGlex());
    errorCollectorDecorator = new ErrorCollectorDecorator(genSetup.getGlex());
    diagramObserverDecorator = new DiagramObserverDecorator(genSetup.getGlex());
    errorObserverDecorator = new ErrorObserverDecorator(genSetup.getGlex());
    observerDecorator = new ObserverDecorator(genSetup.getGlex());
    dbObserverDecorator = new DbObserverDecorator(genSetup.getGlex());
    derivedClassDecorator = new DerivedClassDecorator(genSetup.getGlex());
    managerDecorator = new ManagerDecorator(genSetup.getGlex());
    managerImplDecorator = new ManagerImplDecorator(genSetup.getGlex());
    dummyProviderDecorator = new DummyProviderDecorator(genSetup.getGlex());
    topDecorator = new TopDecorator(genSetup.getHandcodedPath());
  }

  public void generate(ASTUMLPCompilationUnit modelAST) {

    // Translate AST of UMLP Model into initial CD-AST
    ASTCDCompilationUnit cdAST = UMLP2CDTrafo.run(modelAST);
    logTrafoExec("UMLP2CDTrafo", cdAST);

    // execute some basic CD-AST trafos (e.g., put everything in packages)
    cD4CodeAfterParseTrafo.transform(cdAST);
    logTrafoExec("CD4AnalysisAfterParseTrafo", cdAST);

    UMLP2CDTrafo.createCD4CodeSymTab(cdAST);
    logTrafoExec("createCD4CodeSymTab", cdAST);

    cD4CodeTrafo4Defaults.transform(cdAST);
    logTrafoExec("CD4CodeTrafo4Defaults", cdAST);

    cDAssociationCreateFieldsFromNavigableRoles.init(cdAST);
    cDAssociationCreateFieldsFromNavigableRoles.transform(cdAST);
    logTrafoExec("CDAssociationCreateFieldsFromNavigableRoles", cdAST);

    UMLP2CDTrafo.createCD4CodeSymTab(cdAST);
    logTrafoExec("createCD4CodeSymTab", cdAST);

    ASTCDCompilationUnit decoratedAST = UMLP2CDTrafo.createCompilationUnit(cdAST);

    domainClassDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("DomainClassDecorator", cdAST);

    // Generate builder classes
    builderDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("BuilderDecorator", cdAST);

    // Generate ErrorCollector classes
    errorCollectorDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("ErrorCollectorDecorator", cdAST);
  
    observerDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("ObserverDecorator", cdAST);
  
    dbObserverDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("dbObserverDecorator", cdAST);
  
    derivedClassDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("DerivedClassDecorator", cdAST);
    
    // Generate DiagramObserver classes
    diagramObserverDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("DiagramObserverDecorator", cdAST);

    // Generate manager classes
    managerDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("ManagerDecorator", cdAST);

    managerImplDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("ManagerImplDecorator", cdAST);

    dummyProviderDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("DummyProviderDecorator", cdAST);

    // TODO hier weitere trafos realisieren

    // Generate ErrorObserver interfaces
    errorObserverDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("ErrorObserverDecorator", cdAST);
    
    createDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("CreateDecorator", cdAST);
  
    errorDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("ErrorDecorator", cdAST);
  
    deleteDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("DeleteDecorator", cdAST);
  
    getDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("GetDecorator", cdAST);
  
    idDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("IdDecorator", cdAST);
  
    sizeDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("SizeDecorator", cdAST);
    
    setDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("SetDecorator", cdAST);
    
    commandObserverDecorator.transform(cdAST, decoratedAST);
    logTrafoExec("CommandDecorator", cdAST);

    // Top-Decorator
    topDecorator.decoratePackage(decoratedAST);

    // translate the CD-AST into Java with the CD Generator
    cdGenerator.generate(decoratedAST);
  }

  protected static void logTrafoExec(String trafo, ASTCDCompilationUnit ast){
    Log.info("========= CD after '"+trafo+"': =========", UMLPGenerator.class.getName());
    Log.info(astPrinter.prettyprint(ast), UMLPGenerator.class.getName());
  }
}
