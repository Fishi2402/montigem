/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.commands;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlstereotype._ast.ASTStereotypeBuilder;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.AccessPolicyDecorator;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.util.DecoratorUtil;
import umlp.generator.util.PathUtil;
import umlp.generator.util.TagLookUp;
import umlp.generator.util.UMLPSymbolTables;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.types.check.SymTypePrimitive.box;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

public class OwnCommandDecorator extends AbstractDeSerDecorator {

  public static final String COMMAND = "command";

  protected Set<String> supportedDeSers;

  public OwnCommandDecorator(GlobalExtensionManagement glex, Set<String> supportedDeSers) {
    super(glex);
    this.supportedDeSers = supportedDeSers;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    addedDeSers.clear();
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c -> c.getCDMethodList()
            .stream()
            .filter(m -> GenService.hasStereotype(m.getModifier(), COMMAND))
            .filter(m -> TagLookUp.isCommand(m.getSymbol()))
            .forEach(m -> {
              astcdPackage.addCDElement(withOrigin(createCommandClass(origCD, m)));
              astcdPackage.addCDElement(withOrigin(createCommandDeSer(origCD, m)));
            }));


    origCD.getCDDefinition().getCDInterfacesList().stream()
        .filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c -> c.getCDMethodList()
            .stream()
            .filter(m -> GenService.hasStereotype(m.getModifier(), COMMAND))
            .filter(m -> TagLookUp.isCommand(m.getSymbol()))
            .forEach(m -> {
              astcdPackage.addCDElement(withOrigin(createCommandClass(origCD, m)));
              astcdPackage.addCDElement(withOrigin(createCommandDeSer(origCD, m)));
            }));

  }

  protected ASTCDClass createCommandClass(ASTCDCompilationUnit origCD, ASTCDMethod meth) {
    String classname = StringTransformations.capitalize(meth.getName());

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(classname)
        .build();
    ast.setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(extendedClass(meth, ast)));
    ast.setModifier(PUBLIC_ABSTRACT.build());

    ASTStereotypeBuilder stereotype = UMLPMill.stereotypeBuilder();
    stereotype.addValues(
        TopDecorator.NEEDS_TOP_STEREO_BUILDER.apply(
            String.format("Custom implementation for class: %s required. Contains doAction", classname)));
    ast.getModifier().setStereotype(stereotype.build());

    List<ASTCDAttribute> attrList = Lists.newArrayList();
    meth.getCDParameterList().forEach(p -> attrList.add(getCD4C().addAttribute(ast, getAttributeForMethodParameter(p))));

    getCD4C().addConstructor(ast, getConstructorTemplate(), classname, getPackageName(origCD, COMMAND_PACKAGE), attrList);

    AccessorDecorator accessor = DecoratorUtil.streamlessListAcessor(glex);
    attrList.forEach(a -> ast.addAllCDMembers(accessor.decorate(a)));

    getCD4C().addMethod(ast, getDoActionTemplate(), buildReturnType(meth, ast));

    getCD4C().addMethod(ast, getGetEffectedGemIdsTemplate(), Lists.newArrayList());

    addImports(ast);

    List<SymTypeExpression> domainParameters = new ArrayList<>();
    meth.getCDParameterList().forEach(p -> {
      SymTypeExpression type = p.getSymbol().getType();
      if (GenService.isCDType(type)) {
        domainParameters.add(type);
      }
      addImport(type, ast);
    });

    getCD4C().addMethod(ast, getCheckAccessPolicyTemplate(), domainParameters);

    return ast;
  }

  public static String getAttributeForMethodParameter(ASTCDParameter param) {
    if (isGem(param.getSymbol().getType())) {
      return "protected long " + param.getName() + "GemId";
    }
    if (isGemList(param.getSymbol().getType())) {
      return "protected List<Long> " + param.getName() + "GemId";
    }
    return "protected " + UMLPMill.prettyPrint(param.getMCType(), false) + " " + param.getName();
  }

  public static boolean isGem(SymTypeExpression type) {
    return GenService.isCDType(type)
        || (GenService.isOptional(type) && GenService.isCDType(((SymTypeOfGenerics) type).getArgument(0)));
  }

  public static boolean isGemList(SymTypeExpression type) {
    return GenService.isList(type) && GenService.isCDType(((SymTypeOfGenerics) type).getArgument(0));
  }

  protected void addImports(ASTCDClass ast) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, PathUtil.getBackendRTEUtilPath("LogUtil"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GeneralErrorCollector"));
    getCD4C().addImport(ast, "com.google.common.collect.Sets");
    getCD4C().addImport(ast, "java.util.ListIterator");
    getCD4C().addImport(ast, "java.util.Spliterator");
  }

  protected void addImport(SymTypeExpression type, ASTCDClass ast) {
    if (GenService.isCDType(type)) {
      ASTCDCompilationUnit typeCD = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());
      getCD4C().addImport(ast, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print());
      getCD4C().addImport(ast, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print()
          + AccessPolicyDecorator.SUFFIX);
    } else if (GenService.isCDEnum(type)) {
      ASTCDCompilationUnit typeCD = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());
      getCD4C().addImport(ast, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print());
    } else if (GenService.isJavaType(type)) {
      addJavaImport(ast, type);
    }
  }

  protected void addJavaImport(ASTCDClass ast, SymTypeExpression type) {
    getCD4C().addImport(ast, box(type.printFullName()));
  }

  protected String extendedClass(ASTCDMethod command, ASTCDClass ast) {
    SymTypeExpression returnTypeSymbol = command.getSymbol().getType();
    if (returnTypeSymbol.isVoidType()) {
      return buildExtendedCommandType("Void");
    }
    if (returnTypeSymbol.isGenericType()) {
      SymTypeExpression innerType = ((SymTypeOfGenerics) returnTypeSymbol).getArgumentList().get(0);
      addImport(innerType, ast);
      return buildExtendedCommandType(innerType.getTypeInfo().getName());
    }
    addImport(returnTypeSymbol, ast);
    return buildExtendedCommandType(returnTypeSymbol.getTypeInfo().getName());
  }

  public String buildReturnType(ASTCDMethod command, ASTCDClass ast) {
    ASTMCReturnType retType = command.getMCReturnType();
    if (retType.isPresentMCVoidType()) {
      getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("StatusResult"));
      return "StatusResult";
    } else if (retType.isPresentMCType()) {
      ASTMCType mcType = retType.getMCType();
      if (GenService.isList(mcType)) {
        return buildListResult(GenService.getInnerType(mcType), ast);
      }
      if (GenService.isOptional(mcType)) {
        return buildSimpleResult(GenService.getInnerType(mcType), ast);
      }
      return buildSimpleResult(UMLPMill.prettyPrint(mcType, false), ast);
    }
    return buildSimpleResult("Object", ast);
  }

  protected String getConstructorTemplate() {
    return "umlp.commands.own.Constructor";
  }

  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.commands.GetEffectedGemIds";
  }

  protected String getDoActionTemplate() {
    return "umlp.commands.own.DoAction";
  }

  protected String getCheckAccessPolicyTemplate() {
    return "umlp.commands.own.CheckAccessPolicy";
  }

  protected static String buildExtendedCommandType(String typeParam) {
    return new StringBuilder(StringTransformations.capitalize(COMMAND)).append("<")
        .append(box(typeParam)).append(">").toString();
  }

  protected String buildSimpleResult(String typeParam, ASTCDClass ast) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("SimpleResult"));
    return new StringBuilder("SimpleResult").append("<").append(box(typeParam)).append(">").toString();
  }

  protected String buildListResult(String typeParam, ASTCDClass ast) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("ListResult"));
    getCD4C().addImport(ast, "java.util.List");
    return new StringBuilder("ListResult").append("<").append(box(typeParam)).append(", List<")
        .append(box(typeParam)).append(">>").toString();
  }

  protected ASTCDClass createCommandDeSer(ASTCDCompilationUnit origCD, ASTCDMethod meth) {
    String cmdName = StringTransformations.capitalize(meth.getName());

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + cmdName + ">"))
        .setName(cmdName + DE_SER)
        .build();

    String baseClassDecorator = origCD.getCDDefinition().getName() + BaseClassObjectMapperDecorator.SUFFIX;

    List<String> paramNameList = new ArrayList<>();
    List<SymTypeExpression> typeList = new ArrayList<>();
    List<Boolean> userDeserList = new ArrayList<>();

    boolean useDeSer = true;
    boolean jsonEmpty = true;

    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));

    for (ASTCDParameter p : meth.getCDParameterList()) {
      SymTypeExpression type = p.getSymbol().getType();
      String paramName = p.getName();
      if (isGem(type)) {
        type = GenService.getSymType("long");
        paramName += "GemId";
      }
      paramNameList.add(paramName);
      typeList.add(type);
      if (GenService.isJavaType(type)) {
        addDeSerJavaImport(ast, type.printFullName());
      }

      String typeName = GenService.printTypeFullName(type);
      userDeserList.add(supportedDeSers.contains(typeName));

      jsonEmpty = jsonEmpty && GenService.calculateJsonInclGenerics(type, paramName).isEmpty();
      useDeSer = useDeSer && supportedDeSers.contains(typeName);

      addDeSerImport(ast, origCD, supportedDeSers.contains(typeName), GenService.isCDOrInnerCDEnum(type),
          baseClassDecorator, typeName);

      if (GenService.isList(type)) {
        getCD4C().addMethod(ast, "umlp.commands.deser.OwnCommand_FromJsonObjectParametersList", p.getName());
        getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonElement");
      }
    }

    boolean makeAbstract = jsonEmpty && !useDeSer;

    if (makeAbstract) {
      ast.setModifier(PUBLIC_ABSTRACT.build());
    }

    getCD4C().addMethod(ast, getOwnCommand_FromJsonObjectTemplate(), cmdName, paramNameList, typeList,
        userDeserList, baseClassDecorator, makeAbstract);
    getCD4C().addMethod(ast, getOwnCommand_BuildJsonTemplate(), cmdName, paramNameList, typeList,
        userDeserList, baseClassDecorator, makeAbstract);

    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));

    addedDeSers.add(ast);
    return ast;
  }

  protected void addDeSerJavaImport(ASTCDClass ast, String typeName) {
    getCD4C().addImport(ast, typeName);
  }

  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD, boolean useDeSer,
                                boolean isCDEnum, String baseClassDecorator, String typeName) {
    if (isCDEnum) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + Names.getSimpleName(typeName));
    }
    if (useDeSer) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + baseClassDecorator);
    }
  }

  protected String getOwnCommand_BuildJsonTemplate() {
    return "umlp.commands.deser.OwnCommand_BuildJson";
  }

  protected String getOwnCommand_FromJsonObjectTemplate() {
    return "umlp.commands.deser.OwnCommand_FromJsonObject";
  }

  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD,
                                ASTCDType domainClass, SymTypeExpression type, SymTypeExpression innerType, String cmdName) {
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainClass.getName());
    if (GenService.isCDEnum(innerType)) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + ((SymTypeOfGenerics) type).getArgument(0).print());
    }
    else if (GenService.isJavaType(innerType)) {
      getCD4C().addImport(ast, innerType.printFullName());
    }
  }

}
