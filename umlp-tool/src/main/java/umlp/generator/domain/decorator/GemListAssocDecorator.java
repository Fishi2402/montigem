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
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.Collection;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.ManagerDecorator.SUFFIX;

public class GemListAssocDecorator extends AbstractDecorator {

  public GemListAssocDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, GEMLIST_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream()
            .filter(c -> !c.getSymbol().isIsDerived())
            .forEach(c -> CDSymbolTables.getAssociations(c)
                    .stream()
                    .filter(a -> !a.getModifier().isDerived())
                    .forEach(a -> astcdPackage.addAllCDElements(createClasses(origCD, a, c))));

    origCD.getCDDefinition().getCDInterfacesList().stream()
            .filter(i -> !i.getSymbol().isIsDerived())
            .forEach(i -> CDSymbolTables.getAssociations(i)
                    .stream()
                    .filter(a -> !a.getModifier().isDerived())
                    .forEach(a -> astcdPackage.addAllCDElements(createClasses(origCD, a, i))));
  }

  protected Collection<ASTCDClass> createClasses(ASTCDCompilationUnit origCD, ASTCDAssocSide assoc, ASTCDType clazz) {
    List<ASTCDClass> retList = Lists.newArrayList();
    if(GenService.isList(assoc)) {
      retList.add(createGemListClass(origCD, assoc, clazz));
    }
    return retList;
  }

  protected ASTCDClass createGemListClass(ASTCDCompilationUnit origCD, ASTCDAssocSide assoc, ASTCDType domainClass) {
    String sourceName = domainClass.getName();
    String assocName = assoc.getName();
    String className = getName(domainClass, assoc);
    CDTypeSymbol targetSymbol = (CDTypeSymbol) assoc.getMCQualifiedType().getDefiningSymbol().get();
    String targetName = targetSymbol.getName();
    String managerName = origCD.getCDDefinition().getName() + ManagerDecorator.SUFFIX;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(className)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("GemListBuilder<" + targetName + ">"))
            .build();

    // Imports
    ASTCDType targetType = targetSymbol.getAstNode();
    ASTCDCompilationUnit targetCD = UMLPSymbolTables.getDiagramFor(targetType);
    addImports(origCD, sourceName, targetCD, targetName, ast);

    // attributes and getter
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ASTCDAttribute attr = getCD4C().addAttribute(ast, "protected long ownerGemID");
    ast.addAllCDMembers(accessor.decorate(attr));

    // constructor
    getCD4C().addConstructor(ast, "umlp.gemlist.assocs.Constructor", className);
    getCD4C().addConstructor(ast, "umlp.gemlist.assocs.ConstructorEmpty", className);

    // methods
    getCD4C().addMethod(ast, "umlp.gemlist.LoadObjects", targetName, managerName);
    getCD4C().addMethod(ast, "umlp.gemlist.assocs.GetOwner", sourceName, managerName);
    getCD4C().addMethod(ast, "umlp.gemlist.assocs.InternalAdd", assocName);
    getCD4C().addMethod(ast, "umlp.gemlist.assocs.InternalSet", assocName);
    getCD4C().addMethod(ast, "umlp.gemlist.assocs.InternalRemove", assocName);

    return ast;
  }

  protected void addImports(ASTCDCompilationUnit origCD, String domainName,
                            ASTCDCompilationUnit targetCD, String targetName, ASTCDClass ast) {
    getCD4C().addImport(ast, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), domainName));
    getCD4C().addImport(ast, Joiners.DOT.join(getPackageName(targetCD, DEFAULT_PACKAGE), targetName));
    getCD4C().addImport(ast, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), origCD.getCDDefinition().getName() + SUFFIX));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GemListBuilder"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(ast, "umlp.backendrte.common.LoadResult");
  }

  public static String getName(ASTCDType type, ASTCDAssocSide assoc) {
    return type.getName()
            + "_"
            + StringTransformations.capitalize(assoc.getName())
            + "GemList";
  }

}
