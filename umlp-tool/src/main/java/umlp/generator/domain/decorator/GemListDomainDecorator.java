/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;/* (c) https://github.com/MontiCore/monticore */

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.Collection;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.ManagerDecorator.SUFFIX;

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

    origCD.getCDDefinition().getCDInterfacesList().stream()
            .filter(i -> !i.getSymbol().isIsDerived())
            .forEach(i -> astcdPackage.addAllCDElements(createClasses(origCD, i)));
  }

  protected Collection<ASTCDClass> createClasses(ASTCDCompilationUnit origCD, ASTCDType clazz) {
    List<ASTCDClass> retList = Lists.newArrayList();
    retList.add(createGemListClass(origCD, clazz));
    return retList;
  }

  protected ASTCDClass createGemListClass(ASTCDCompilationUnit origCD, ASTCDType domainClass) {
    String sourceName = domainClass.getName();
    String classname = sourceName
        + "GemList";
    String managerName = origCD.getCDDefinition().getName() + SUFFIX;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setName(classname)
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("GemListBuilder<" + sourceName + ">"))
      .build();

    // Imports
    addImports(origCD, domainClass.getName(), ast);

    // constructor
    getCD4C().addConstructor(ast, getConstructorTemplate(), classname);
    getCD4C().addConstructor(ast, getConstructorDefaultTemplate(), classname);

    // methods
    getCD4C().addMethod(ast, "umlp.gemlist.LoadObjects", sourceName, managerName);

    return ast;
  }

  protected String getConstructorDefaultTemplate() {
    return "umlp.gemlist.ConstructorDefault";
  }

  protected String getConstructorTemplate() {
    return "umlp.gemlist.Constructor";
  }

  protected void addImports(ASTCDCompilationUnit origCD, String domainName, ASTCDClass ast) {
    getCD4C().addImport(ast, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), domainName));
    getCD4C().addImport(ast, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), origCD.getCDDefinition().getName()+SUFFIX));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GemListBuilder"));
    getCD4C().addImport(ast, "umlp.backendrte.common.LoadResult");
  }

}
