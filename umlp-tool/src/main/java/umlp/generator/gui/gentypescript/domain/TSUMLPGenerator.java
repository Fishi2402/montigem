/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.domain.UMLAssocToAttrTrafo;
import umlp.generator.domain.UMLAttrToAssocTrafo;
import umlp.generator.domain.UMLPGenerator;
import umlp.generator.domain.UMLSetDefaultsTrafo;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.gui.gentypescript.printer.TSUtilsPrinter;

import java.util.Arrays;

public class TSUMLPGenerator extends UMLPGenerator {

  public TSUMLPGenerator(GeneratorSetup genSetup) {
    super(genSetup);

    genSetup.getGlex().setGlobalValue("cdPrinter", new TSUtilsPrinter());
    genSetup.getGlex().setGlobalValue("genService", new GenService());
    cdGenerator = new TSCDGenerator(genSetup);

    genSetup.getGlex().replaceTemplate(
        CD2JavaTemplates.CLASS, new TemplateHookPoint(GUI2ComponentTemplates.CLASS)
    );
    genSetup.getGlex().replaceTemplate(
        CD2JavaTemplates.ATTRIBUTE, new TemplateHookPoint(GUI2ComponentTemplates.ATTRIBUTE)
    );

    /*
    genSetup.getGlex().replaceTemplate(
        CD2JavaTemplates.ANNOTATIONS, new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATIONS)
    );
    /**/
  }

  public void generate(ASTCDCompilationUnit cdAST, String configTemplate) {
    UMLAttrToAssocTrafo assocTrafo = new UMLAttrToAssocTrafo(CD4CodeMill.traverser());
    assocTrafo.transform(cdAST);
    logTrafoExec("UMLAttrToAssocTrafo", cdAST);

    UMLSetDefaultsTrafo assocNameTrafo = new UMLSetDefaultsTrafo(CD4CodeMill.traverser());
    assocNameTrafo.transform(cdAST);
    logTrafoExec("UMLSetDefaultsTrafo", cdAST);

    UMLAssocToAttrTrafo assoc2Trafo = new UMLAssocToAttrTrafo(CD4CodeMill.traverser());
    assoc2Trafo.transform(cdAST);
    logTrafoExec("UMLAssocToAttrTrafo", cdAST);

    // rebuild symbol table
    CD4CodeMill.globalScope().removeSubScope(cdAST.getEnclosingScope());
    UMLPMill.scopesGenitorDelegator().createFromAST(cdAST);
    cdAST.accept(new CD4CodeSymbolTableCompleter(cdAST).getTraverser());
    logTrafoExec("rebuildCD4CodeSymTab", cdAST);

    // create an empty classdiagram
    ASTCDCompilationUnit decoratedAST = createCompilationUnit(cdAST);



    TemplateController tc = genSetup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
    // translate the CD-AST into Java with the CD Generator
    hpp.processValue(tc, cdAST, Arrays.asList(decoratedAST, genSetup.getGlex(), genSetup.getHandcodedPath(), cdGenerator, schemaGenerator));
  }

  protected static void logTrafoExec(String trafo, ASTCDCompilationUnit ast){
    Log.info("========= CD after '"+trafo+"': =========", TSUMLPGenerator.class.getName());
  }

  public ASTCDCompilationUnit createCompilationUnit(ASTCDCompilationUnit cd) {
    // create an empty CD definition
    ASTCDDefinition cdDef = CD4CodeMill.cDDefinitionBuilder()
            .setName(cd.getCDDefinition().getName())
            .setModifier(CDModifier.PUBLIC.build())
            .build();

    // ... and add it to a new CD compilation unit
    ASTCDCompilationUnit newCD = CD4CodeMill.cDCompilationUnitBuilder()
            .setCDDefinition(cdDef)
            .build();

    // set the package
    if (cd.isPresentMCPackageDeclaration()) {
      newCD.setMCPackageDeclaration(cd.getMCPackageDeclaration());
    }

    return newCD;
  }
}
