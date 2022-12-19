/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.commands;/* (c) https://github.com/MontiCore/monticore */

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDBiDir;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.GenService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

public class AssocCommandDecorator extends AbstractDecorator {

  public static final String COMMAND_ASSOC_PACKAGE = COMMAND_PACKAGE + ".associations";
  public static final String ADD_SUFFIX = "_add";
  public static final String REMOVE_SUFFIX = "_remove";
  public static final String SET_SUFFIX = "_set";


  public AssocCommandDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_ASSOC_PACKAGE);
    origCD.getCDDefinition().getCDAssociationsList().stream()
            .filter(a -> !a.getModifier().isDerived())
            .filter(a -> a.getCDAssocDir() instanceof ASTCDBiDir).
            forEach(a -> astcdPackage.addAllCDElements(createClass(origCD, a)));
  }

  protected Collection<ASTCDClass> createClass(ASTCDCompilationUnit origCD, ASTCDAssociation assoc) {
    List<ASTCDClass> retList = Lists.newArrayList();
    ASTCDClass clazz = createAddClass(origCD, assoc);
    retList.add(createDeSer(origCD, clazz));
    retList.add(clazz);
    clazz = createRemoveClass(origCD, assoc);
    retList.add(createDeSer(origCD, clazz));
    retList.add(clazz);
    clazz = createSetClass(origCD, assoc);
    retList.add(createDeSer(origCD, clazz));
    retList.add(clazz);
    return retList;
  }

  protected ASTCDClass createAddClass(ASTCDCompilationUnit origCD, ASTCDAssociation assoc) {
    String classname = StringTransformations.capitalize(assoc.getName()) + ADD_SUFFIX;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(classname)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
            .build();

    String sourceName = assoc.getLeft().getName();
    String targetName = assoc.getRight().getName();
    String sourceType = assoc.getLeft().getSymbol().getType().print();
    String targetType = assoc.getRight().getSymbol().getType().print();

    // attributes and getter
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ASTCDAttribute attr = getCD4C().addAttribute(ast, "protected long " + StringTransformations.uncapitalize(sourceName) + "GemId");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected long " + StringTransformations.uncapitalize(targetName) + "GemId");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected int "+sourceName+"Index");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected int "+targetName+"Index");
    ast.addAllCDMembers(accessor.decorate(attr));

    // constructor
    getCD4C().addConstructor(ast, "umlp.commands.assocs.Constructor", classname, COMMAND_ASSOC_PACKAGE,
            sourceName, targetName);

    // methods
    getCD4C().addMethod(ast, "umlp.commands.assocs.Add_DoRun", assoc.getName(),
            sourceName, targetName, sourceType, targetType,
            GenService.isList(assoc.getLeft()), GenService.isList(assoc.getRight()),
            origCD.getCDDefinition().getName());
    getCD4C().addMethod(ast, "umlp.commands.GetEffectedGemIds",
            Lists.newArrayList(sourceName+"GemId", targetName+"GemId"));

    // Importe
    addImports(origCD, ast);

    return ast;
  }

  protected ASTCDClass createRemoveClass(ASTCDCompilationUnit origCD, ASTCDAssociation assoc) {
    String classname = StringTransformations.capitalize(assoc.getSymbol().getName()) + REMOVE_SUFFIX;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(classname)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
            .build();

    String sourceName = assoc.getLeft().getName();
    String targetName = assoc.getRight().getName();
    String sourceType = assoc.getLeft().getSymbol().getType().print();
    String targetType = assoc.getRight().getSymbol().getType().print();

    // attributes and getter
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ASTCDAttribute attr = getCD4C().addAttribute(ast, "protected long " + StringTransformations.uncapitalize(sourceName) + "GemId");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected long " + StringTransformations.uncapitalize(targetName) + "GemId");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected int "+sourceName+"Index");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected int "+targetName+"Index");
    ast.addAllCDMembers(accessor.decorate(attr));

    // constructor
    getCD4C().addConstructor(ast, "umlp.commands.assocs.Constructor", classname,
            COMMAND_ASSOC_PACKAGE, sourceName, targetName);

    // methods
    getCD4C().addMethod(ast, "umlp.commands.assocs.Remove_DoRun", assoc.getName(),
            sourceName, targetName, sourceType, targetType,
            GenService.isList(assoc.getLeft()), GenService.isList(assoc.getRight()),
            origCD.getCDDefinition().getName());
    getCD4C().addMethod(ast, "umlp.commands.GetEffectedGemIds",
            Lists.newArrayList(sourceName+"GemId", targetName+"GemId"));

    // Importe
    addImports(origCD, ast);

    return ast;
  }

  protected ASTCDClass createSetClass(ASTCDCompilationUnit origCD, ASTCDAssociation assoc) {
    String classname = StringTransformations.capitalize(assoc.getName() + SET_SUFFIX);

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(classname)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
            .build();

    String sourceName = assoc.getLeft().getName();
    String targetName = assoc.getRight().getName();
    String sourceType = assoc.getLeft().getSymbol().getType().print();
    String targetType = assoc.getRight().getSymbol().getType().print();

    // attributes and getter
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ASTCDAttribute attr = getCD4C().addAttribute(ast, "protected long old" + StringTransformations.capitalize(sourceName) + "GemId");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected long " + sourceName + "GemId");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected long old" + StringTransformations.capitalize(targetName) + "GemId");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected long " + targetName + "GemId");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected int " + sourceName +"Index");
    ast.addAllCDMembers(accessor.decorate(attr));
    attr = getCD4C().addAttribute(ast, "protected int " + targetName+"Index");
    ast.addAllCDMembers(accessor.decorate(attr));

    getCD4C().addConstructor(ast, "umlp.commands.assocs.Set_Constructor", classname, COMMAND_ASSOC_PACKAGE,
            sourceName, targetName);

    getCD4C().addMethod(ast, "umlp.commands.assocs.Set_DoRun",
            sourceName, targetName);
    getCD4C().addMethod(ast, "umlp.commands.assocs.Set_Target_Exchange", assoc.getName(),
            sourceName, targetName, sourceType, targetType, origCD.getCDDefinition().getName());
    getCD4C().addMethod(ast, "umlp.commands.assocs.Set_Source_Exchange", assoc.getName(),
            sourceName, targetName, sourceType, targetType,
            GenService.isList(assoc.getLeft()), GenService.isList(assoc.getRight()),
            origCD.getCDDefinition().getName());
    getCD4C().addMethod(ast, "umlp.commands.GetEffectedGemIds",
            Lists.newArrayList(sourceName+"GemId", targetName+"GemId"));

    addImports(origCD, ast);

    return ast;
  }

  protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, ASTCDClass cmdClass) {
    String cmdName = cmdClass.getName();

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + cmdName + ">"))
            .setName(cmdName+DE_SER)
            .build();

    getCD4C().addMethod(ast, "umlp.commands.assocs.FromJsonObject", cmdName,
            cmdClass.getCDAttributeList().stream().map(a -> a.getName()).collect(Collectors.toList()));
    getCD4C().addMethod(ast, "umlp.commands.assocs.BuildJson", cmdName,
            cmdClass.getCDAttributeList().stream().map(a -> a.getName()).collect(Collectors.toList()));

    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+".*");
    getCD4C().addImport(ast, "umlp.command.CommandDeSer");

    return ast;
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass ast) {
    // Importe
    getCD4C().addImport(ast, "com.google.common.collect.Lists");
    getCD4C().addImport(ast, "umlp.command.StatusResult");
    getCD4C().addImport(ast, "umlp.command.Command");
    getCD4C().addImport(ast, "umlp.command.CommandManager");
    getCD4C().addImport(ast, "umlp.util.LogUtil");
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+".*");
    getCD4C().addImport(ast, "umlp.rte.NotificationScope");
    getCD4C().addImport(ast, "umlp.rte.GeneralErrorCollector");
  }
}
