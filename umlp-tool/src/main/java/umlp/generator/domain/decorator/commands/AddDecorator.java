/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.commands;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.Optional;
import java.util.Set;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

public class AddDecorator extends AbstractDeSerDecorator {

  public static final String SUFFIX = "_add";
  protected Set<String> supportedDeSers;

  public AddDecorator(GlobalExtensionManagement glex, Set<String> supportedDeSers) {
    super(glex);
    this.supportedDeSers = supportedDeSers;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    addedDeSers.clear();
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getSymbol().isIsDerived())
            .forEach(c -> {
              CDSymbolTables.getAttributesInHierarchy(c)
                      .stream()
                      .filter(a -> !a.getModifier().isDerived())
                      .filter(a -> GenService.isList(a))
                      .forEach(a -> {
                        astcdPackage.addCDElement(createClass(origCD, a.getName(), ((SymTypeOfGenerics) a.getSymbol().getType()).getArgument(0), c, false));
                        astcdPackage.addCDElement(createDeSer(origCD, a.getName(), a.getSymbol().getType(), c, false));
                      });
              CDSymbolTables.getAssociations(c)
                      .stream()
                      .filter(a -> !a.getModifier().isDerived())
                      .filter(a -> !GenService.isBidirectional(a))
                      .filter(a -> GenService.isList(a))
                      .forEach(a -> {
                        astcdPackage.addCDElement(createClass(origCD, a.getName(), a.getSymbol().getType(), c, true));
                        astcdPackage.addCDElement(createDeSer(origCD, a.getName(), getSymType("long"), c, true));
                      });
            });
    origCD.getCDDefinition().getCDInterfacesList().stream()
        .filter(c -> !c.getSymbol().isIsDerived())
            .forEach(c -> {
              CDSymbolTables.getAttributesInHierarchy(c)
                      .stream()
                      .filter(a -> !a.getModifier().isDerived())
                      .filter(GenService::isList)
                      .forEach(a -> {
                        astcdPackage.addCDElement(createClass(origCD, a.getName(), ((SymTypeOfGenerics) a.getSymbol().getType()).getArgument(0), c, false));
                        astcdPackage.addCDElement(createDeSer(origCD, a.getName(), a.getSymbol().getType(), c, false));
                      });
              CDSymbolTables.getAssociations(c)
                      .stream()
                      .filter(a -> !a.getModifier().isDerived())
                      .filter(a -> !GenService.isBidirectional(a))
                      .filter(GenService::isList)
                      .forEach(a -> {
                        astcdPackage.addCDElement(createClass(origCD, a.getName(), a.getSymbol().getType(), c, true));
                        astcdPackage.addCDElement(createDeSer(origCD, a.getName(), getSymType("long"), c, true));
                      });
            });
  }


  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, String name, SymTypeExpression type, ASTCDType domainClass, boolean isGem) {
    String classname = domainClass.getName() + SUFFIX
            + StringTransformations.capitalize(name);
    String attrName = isGem ? name + "GemId" : name;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(classname)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<Void>"))
            .build();

    ASTCDAttribute gemAttr = getCD4C().addAttribute(ast, "protected long gemId");
    ASTCDAttribute attr = getCD4C().addAttribute(ast, "protected " + (isGem ? "long" : type.print()) + " " + attrName);
    ASTCDAttribute indexAttr = getCD4C().addAttribute(ast, "protected int indexInList");

    getCD4C().addConstructor(ast, "umlp.commands.add.Constructor", classname, getPackageName(origCD, COMMAND_PACKAGE),
            attrName, isGem ? "long" : type.print());

    getCD4C().addMethod(ast, "umlp.commands.add.DoRun", name, domainClass.getName(), origCD.getCDDefinition().getName(), isGem, name, type.print());
    if (isGem) {
      getCD4C().addMethod(ast, "umlp.commands.GetEffectedGemIds", Lists.newArrayList("gemId", name + "GemId"));
    } else {
      getCD4C().addMethod(ast, "umlp.commands.GetEffectedGemIds", Lists.newArrayList("getGemId()"));
    }
    AccessorDecorator accessor = new AccessorDecorator(glex);
    ast.addAllCDMembers(accessor.decorate(gemAttr));
    ast.addAllCDMembers(accessor.decorate(attr));
    ast.addAllCDMembers(accessor.decorate(indexAttr));

    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("StatusResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, PathUtil.getBackendRTEUtilPath("LogUtil"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GeneralErrorCollector"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainClass.getName());
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + origCD.getCDDefinition().getName() + ManagerDecorator.SUFFIX);

    ASTCDCompilationUnit typeCD = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());
    if (isGem) {
      getCD4C().addImport(ast, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print());
    } else if (GenService.isCDEnum(type)) {
      getCD4C().addImport(ast,getPackageName(typeCD, DEFAULT_PACKAGE)+"."+ type.print());
    } else if (GenService.isJavaType(type)) {
      getCD4C().addImport(ast, type.printFullName());
    }

    return ast;
  }

  protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, String name, SymTypeExpression type, ASTCDType domainClass, boolean isGem) {
    String cmdName = domainClass.getName() + SUFFIX
            + StringTransformations.capitalize(name);

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + cmdName + ">"))
            .setName(cmdName + DE_SER)
            .build();

    if (isGem) {
      type = getSymType("long");
      name += "GemId";
    }

    SymTypeExpression innerType = type;
    if (GenService.isList(type)) {
      innerType = ((SymTypeOfGenerics) type).getArgument(0);
    } else if (GenService.isOptional(type)) {
      innerType = ((SymTypeOfGenerics) type).getArgument(0);
    }
    String jsonStr = GenService.calculateJSon(innerType);
    String typeName = innerType.printFullName();
    String getter = GenService.getGetterName(name, innerType);

    String baseClassDecorator = getPackageName(origCD, DEFAULT_PACKAGE) + "." + origCD.getCDDefinition().getName() + BaseClassObjectMapperDecorator.SUFFIX;
    boolean useDeSer = supportedDeSers.contains(typeName);
    boolean makeAbstract = jsonStr.equals("") && !useDeSer;
    getCD4C().addMethod(ast, "umlp.commands.deser.Add_FromJsonObject", cmdName, name, typeName,
        GenService.isInnerCDEnum(type), jsonStr, useDeSer, baseClassDecorator, makeAbstract);
    getCD4C().addMethod(ast, "umlp.commands.deser.Add_BuildJson", cmdName, name, typeName,
        GenService.isInnerCDEnum(type), getter, useDeSer,
        baseClassDecorator, makeAbstract);

    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainClass.getName());
    if (GenService.isCDEnum(innerType)) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + ((SymTypeOfGenerics)type).getArgument(0).print());
    } else if (GenService.isJavaType(innerType)) {
      getCD4C().addImport(ast, innerType.printFullName());
    }
    if (makeAbstract) {
      ast.setModifier(PUBLIC_ABSTRACT.build());
    }
    addedDeSers.add(ast);
    return ast;
  }

  // TODO (MB): Bessere Lösung?
  protected SymTypeExpression getSymType(String type) {
    Optional<TypeSymbol> primitive = BasicSymbolsMill.globalScope().resolveType(type);
    if (primitive.isPresent()) {
      return SymTypeExpressionFactory.createPrimitive(primitive.get());
    } else {
      return SymTypeExpressionFactory.createObscureType();
    }
  }
}
