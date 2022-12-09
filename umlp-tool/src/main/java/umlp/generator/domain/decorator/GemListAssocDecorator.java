/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;/* (c) https://github.com/MontiCore/monticore */

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
import umlp.generator.AbstractDecorator;

import java.util.Collection;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class GemListAssocDecorator extends AbstractDecorator {

  public GemListAssocDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, GEMLIST_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c ->
            CDSymbolTables.getAssociationsInHierarchy(c)
                .stream()
                .filter(a -> !a.getModifier().isDerived())
                .forEach(a -> astcdPackage.addAllCDElements(createClasses(origCD, a, c)))
        );
  }

  protected Collection<ASTCDClass> createClasses(ASTCDCompilationUnit origCD, ASTCDAssocSide assoc, ASTCDClass clazz) {
    List<ASTCDClass> retList = Lists.newArrayList();
    if (GenService.isList(assoc)) {
      retList.add(createGemListClass(origCD, assoc, clazz));
    }
    return retList;
  }

  protected ASTCDClass createGemListClass(ASTCDCompilationUnit origCD, ASTCDAssocSide assoc, ASTCDClass domainClass) {
    String sourceName = domainClass.getName();
    String assocName = assoc.getName();
    String classname = sourceName
        + "_"
        + StringTransformations.capitalize(assocName)
        + "GemList";
    String targetName = assoc.getMCQualifiedType().getDefiningSymbol().get().getName();
    String managerName = origCD.getCDDefinition().getName() + ManagerDecorator.SUFFIX;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setName(classname)
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("GemListBuilder<" + targetName + ">"))
      .build();

    // Imports
    addImports(origCD, ast);

    // attributes and getter
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ASTCDAttribute attr = getCD4C().addAttribute(ast, "protected long ownerGemID");
    ast.addAllCDMembers(accessor.decorate(attr));

    // constructor
    getCD4C().addConstructor(ast, "umlp.gemlist.assocs.Constructor", classname);
    getCD4C().addConstructor(ast, "umlp.gemlist.assocs.ConstructorEmpty", classname);

    // methods
    getCD4C().addMethod(ast, "umlp.gemlist.LoadObjects", targetName, managerName);
    getCD4C().addMethod(ast, "umlp.gemlist.assocs.GetOwner", sourceName, managerName);
    getCD4C().addMethod(ast, "umlp.gemlist.assocs.InternalAdd", assocName);
    getCD4C().addMethod(ast, "umlp.gemlist.assocs.InternalSet", assocName);
    getCD4C().addMethod(ast, "umlp.gemlist.assocs.InternalRemove", assocName);

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
