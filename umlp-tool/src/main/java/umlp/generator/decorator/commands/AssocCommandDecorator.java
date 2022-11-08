/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.decorator.commands;/* (c) https://github.com/MontiCore/monticore */

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
import umlp.generator.decorator.AbstractDecorator;
import umlp.generator.decorator.GenService;

import java.util.Collection;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class AssocCommandDecorator extends AbstractDecorator {

  public AssocCommandDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
      .forEach(c ->
              CDSymbolTables.getAssociationsInHierarchy(c)
          .stream()
          .filter(a -> !a.getModifier().isDerived())
          .forEach(a -> astcdPackage.addAllCDElements(createClass(origCD, a, c))));
  }

  protected Collection<ASTCDClass> createClass(ASTCDCompilationUnit origCD, ASTCDAssocSide assoc, ASTCDClass clazz) {
    List<ASTCDClass> retList = Lists.newArrayList();
    if (GenService.isList(assoc)) {
      retList.add(createAddClass(origCD, assoc, clazz));
      retList.add(createRemoveClass(origCD, assoc, clazz));
    } else {
      retList.add(createSetClass(origCD, assoc, clazz));
    }
    return retList;
  }

  protected ASTCDClass createAddClass(ASTCDCompilationUnit origCD, ASTCDAssocSide assoc, ASTCDClass domainClass) {
    String classname = domainClass.getName()+"_add"+ StringTransformations.capitalize(assoc.getName());
    
    ASTCDClass ast = UMLPMill.cDClassBuilder()
      .setModifier(PUBLIC.build())
      .setName(classname)
      .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
      .build();

    String sourceName = domainClass.getName();
    String targetName = assoc.getMCQualifiedType().getDefiningSymbol().get().getName();

     // attributes and getter
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ASTCDAttribute attr = getCD4C().addAttribute(ast, "protected long gemId");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected long gemId" + targetName);
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected int indexInList");
    ast.addAllCDMembers(accessor.decorate(attr));

    // constructor
    getCD4C().addConstructor(ast, "umlp.commands.assocs.Add_Constructor", classname, COMMAND_PACKAGE,
      targetName);

    // methods
    getCD4C().addMethod(ast, "umlp.commands.assocs.Add_DoRun", assoc.getName(),
            sourceName, targetName, origCD.getCDDefinition().getName());
    getCD4C().addMethod(ast, "umlp.commands.assocs.GetEffectedGemIds", targetName);

    // Importe
    addImports(origCD, ast);
    
    return ast;
  }

  protected ASTCDClass createRemoveClass(ASTCDCompilationUnit origCD, ASTCDAssocSide assoc, ASTCDClass domainClass) {
    String classname = domainClass.getName()+"_remove"+ StringTransformations.capitalize(assoc.getSymbol().getName());

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(classname)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
            .build();

    String sourceName = domainClass.getName();
    String targetName = assoc.getMCQualifiedType().getDefiningSymbol().get().getName();

    // attributes and getter
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ASTCDAttribute attr = getCD4C().addAttribute(ast, "protected long gemId");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected long gemId" + targetName);
    ast.addAllCDMembers(accessor.decorate(attr));

    // constructor
    getCD4C().addConstructor(ast, "umlp.commands.assocs.Constructor", classname,
            COMMAND_PACKAGE, targetName);

    // methods
    getCD4C().addMethod(ast, "umlp.commands.assocs.Remove_DoRun", assoc.getName(),
            sourceName, targetName, origCD.getCDDefinition().getName());
    getCD4C().addMethod(ast, "umlp.commands.assocs.GetEffectedGemIds", targetName);

    // Importe
    addImports(origCD, ast);

    return ast;
  }

  protected ASTCDClass createSetClass(ASTCDCompilationUnit origCD, ASTCDAssocSide assoc, ASTCDClass domainClass) {
    String classname = domainClass.getName()+"_set"+ StringTransformations.capitalize(assoc.getName());

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(classname)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
            .build();

    String sourceName = domainClass.getName();
    String targetName = assoc.getMCQualifiedType().getDefiningSymbol().get().getName();

    // attributes and getter
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ASTCDAttribute attr = getCD4C().addAttribute(ast, "protected long gemId");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected long gemId" + targetName);
    ast.addAllCDMembers(accessor.decorate(attr));

    getCD4C().addConstructor(ast, "umlp.commands.assocs.Constructor", classname, COMMAND_PACKAGE,
            targetName);

    getCD4C().addMethod(ast, "umlp.commands.assocs.Set_DoRun", assoc.getName(),
            sourceName, targetName, origCD.getCDDefinition().getName());
    getCD4C().addMethod(ast, "umlp.commands.assocs.GetEffectedGemIds", targetName);

    addImports(origCD, ast);

    return ast;
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass ast) {
    // Importe
    getCD4C().addImport(ast, "umlp.command.StatusResult");
    getCD4C().addImport(ast, "umlp.command.Command");
    getCD4C().addImport(ast, "umlp.util.LogUtil");
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+".*");
    getCD4C().addImport(ast, "umlp.rte.NotificationScope");
    getCD4C().addImport(ast, "umlp.rte.GeneralErrorCollector");
  }
}
