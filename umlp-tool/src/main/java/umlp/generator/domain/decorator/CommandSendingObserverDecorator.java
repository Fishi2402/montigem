/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.commands.AssocCommandDecorator.COMMAND_ASSOC_PACKAGE;

public class CommandSendingObserverDecorator extends AbstractDecorator {

  public static final String SUFFIX = "CommandSendingObserver";

  public CommandSendingObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, CLIENT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
            .forEach(c -> astcdPackage.addCDElement(createClass(origCD, c)));

    origCD.getCDDefinition().getCDInterfacesList().stream().filter(i -> !i.getModifier().isDerived())
            .forEach(i -> astcdPackage.addCDElement(createClass(origCD, i)));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDType domainClass) {
    String classname = domainClass.getName() + SUFFIX;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(domainClass.getName()+"Observer"))
            .setName(classname)
            .build();

    getCD4C().addMethod(ast, "umlp.commandobserver.GetScope");

    // Attribute
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
            .filter(a -> !a.getModifier().isDerived())
            .forEach(a -> {
              if (GenService.isList(a)) {
                getCD4C().addMethod(ast, "umlp.commandsendingobserver.NotifyAddAttribute",
                        domainClass.getName(), a.getName(), GenService.getInnerType(a.getMCType()),
                        a.getName(), false);
                getCD4C().addMethod(ast, "umlp.commandsendingobserver.NotifyRemoveAttribute",
                        domainClass.getName(), a.getName(), GenService.getInnerType(a.getMCType()),
                        a.getName(), false);
                getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE)+"."+
                        domainClass.getName() + "_add" + StringTransformations.capitalize(a.getName()));
                getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE)+"."+
                        domainClass.getName() + "_remove" + StringTransformations.capitalize(a.getName()));
              } else {
                getCD4C().addMethod(ast, "umlp.commandsendingobserver.NotifySetAttribute",
                        domainClass.getName(), a.getName(), GenService.getType(a.getSymbol().getType()),
                        a.getName(), false);
                getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE)+"."+
                        domainClass.getName() + "_set" + StringTransformations.capitalize(a.getName()));
              }
            });

    // Assoziationen
    CDSymbolTables.getAssociations(domainClass).stream()
            .filter(a -> !(a.getModifier().isDerived() || a.getModifier().isReadonly()))
            .forEach(a -> {
              boolean isBidirectional = GenService.isBidirectional(a);
              String assocName = a.getSymbol().getAssoc().getAssociation().getName();
               if (GenService.isList(a)) {
                getCD4C().addMethod(ast, "umlp.commandsendingobserver.NotifyAddAttribute", domainClass.getName(),
                        a.getName(), "long", assocName, isBidirectional);
                getCD4C().addMethod(ast, "umlp.commandsendingobserver.NotifyRemoveAttribute", domainClass.getName(),
                        a.getName(), "long", assocName, isBidirectional);
                if (isBidirectional) {
                  getCD4C().addImport(ast, getPackageName(origCD, COMMAND_ASSOC_PACKAGE)+"."+
                          StringTransformations.capitalize(assocName)+"_add");
                  getCD4C().addImport(ast, getPackageName(origCD, COMMAND_ASSOC_PACKAGE)+"."+
                          StringTransformations.capitalize(assocName)+"_remove");
                } else {
                  getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." +
                          domainClass.getName() + "_add" + StringTransformations.capitalize(a.getName()));
                  getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." +
                          domainClass.getName() + "_remove" + StringTransformations.capitalize(a.getName()));
                }
              } else {
                getCD4C().addMethod(ast, "umlp.commandsendingobserver.NotifySetAttribute", domainClass.getName(),
                        a.getName(), "long", assocName, isBidirectional);
                if (isBidirectional) {
                  getCD4C().addImport(ast, getPackageName(origCD, COMMAND_ASSOC_PACKAGE)+"."+
                          StringTransformations.capitalize(assocName)+"_set");
                } else {
                  getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." +
                          domainClass.getName() + "_set" + StringTransformations.capitalize(a.getName()));

                }
              }
            });
    addImports(origCD, ast, domainClass);

    return ast;
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass clazz, ASTCDType domainClass) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandManager"));
    getCD4C().addImport(clazz, getPackageName(origCD, DEFAULT_PACKAGE)+"."+domainClass.getName());
    getCD4C().addImport(clazz, getPackageName(origCD, DEFAULT_PACKAGE)+"."+domainClass.getName()+ObserverDecorator.SUFFIX);

  }

}
