/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.decorator;/* (c) https://github.com/MontiCore/monticore */

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;

import java.util.Collection;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class GemListDomainDecorator extends AbstractDecorator {

  public GemListDomainDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, GEMLIST_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c -> astcdPackage.addAllCDElements(createClasses(origCD, c)));
  }

  protected Collection<ASTCDClass> createClasses(ASTCDCompilationUnit origCD, ASTCDClass clazz) {
    List<ASTCDClass> retList = Lists.newArrayList();
    retList.add(createGemListClass(origCD, clazz));
    return retList;
  }

  protected ASTCDClass createGemListClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass) {
    String sourceName = domainClass.getName();
    String classname = sourceName
        + "GemList";
    String managerName = origCD.getCDDefinition().getName() + ManagerDecorator.SUFFIX;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setName(classname)
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("GemListBuilder<" + sourceName + ">"))
      .build();

    // Imports
    addImports(origCD, ast);

    // constructor
    getCD4C().addConstructor(ast, "umlp.gemlist.Constructor", classname);
    getCD4C().addConstructor(ast, "umlp.gemlist.ConstructorDefault", classname);

    // methods
    getCD4C().addMethod(ast, "umlp.gemlist.LoadObjects", sourceName, managerName);

    return ast;
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass ast) {
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+".*");
    getCD4C().addImport(ast, "umlp.util.LogUtil");
    getCD4C().addImport(ast, "umlp.rte.GeneralErrorCollector");
    getCD4C().addImport(ast, "umlp.rte.GemIdentifiable");
    getCD4C().addImport(ast, "umlp.rte.GemList");
    getCD4C().addImport(ast, "umlp.rte.GemListBuilder");
    getCD4C().addImport(ast, "umlp.rte.NotificationScope");
  }

}
