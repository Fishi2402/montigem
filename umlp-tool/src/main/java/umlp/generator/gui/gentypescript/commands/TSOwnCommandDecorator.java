/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;

import com.google.common.collect.Lists;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.AccessPolicyDecorator;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.commands.OwnCommandDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.gui.gentypescript.method.TSAccessorDecorator;
import umlp.generator.gui.gentypescript.printer.TSUtilsPrinter;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

public class TSOwnCommandDecorator extends OwnCommandDecorator {

  public TSOwnCommandDecorator(GlobalExtensionManagement glex, Set<String> supportedDeSers) {
    super(glex, supportedDeSers);
  }

  protected ASTCDClass createCommandClass(ASTCDCompilationUnit origCD, ASTCDMethod meth) {
    String classname = StringTransformations.capitalize(meth.getName());

    ASTCDClass ast = UMLPMill.cDClassBuilder()
                         .setModifier(PUBLIC.build())
                         .setName(classname)
                         .build();
    ast.setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(extendedClass(meth, ast)));
    // FE commands don't necessarily need an implementation
    /*
    ast.setModifier(PUBLIC_ABSTRACT.build());

    ASTStereotypeBuilder stereotype = UMLPMill.stereotypeBuilder();
    stereotype.addValues(
        TopDecorator.NEEDS_TOP_STEREO_BUILDER.apply(
            String.format("Custom implementation for class: %s required. Contains doRun ", classname)));
    ast.getModifier().setStereotype(stereotype.build());
    */

    List<ASTCDAttribute> attrList = Lists.newArrayList();
    meth.getCDParameterList().forEach(p -> attrList.add(getCD4C().addAttribute(ast, getAttributeForMethodParameter(p))));


    TSAccessorDecorator accessor = new TSAccessorDecorator(glex);
    attrList.forEach(a -> ast.addAllCDMembers(accessor.decorate(a)));

    getCD4C().addMethod(ast, getDoActionTemplate(), buildReturnType(meth, ast).printType());
    addConstructor(ast, classname, getPackageName(origCD, COMMAND_PACKAGE), attrList);
    // getCD4C().addConstructor(ast, getConstructorTemplate(), classname, getPackageName(origCD, COMMAND_PACKAGE), attrList);
    // getCD4C().addMethod(ast, getGetEffectedGemIdsTemplate(), Lists.newArrayList());

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
    addedDeSers.add(ast);
    return ast;
  }

  protected void addConstructor(ASTCDClass ast, String classname, String packageName, List<ASTCDAttribute> attrList) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), classname, packageName, attrList));
  }

  @Override
  public String getAttributeForMethodParameter(ASTCDParameter param) {
    if (isGem(param.getSymbol().getType())) {
      return "protected number " + param.getName() + "GemId";
    }
    if (isListOfGemIdentifiable(param.getSymbol().getType())) {
      return "protected number[] " + param.getName() + "GemId";
    }
    return "protected " + new TSUtilsPrinter().printType(param.getMCType()) + " " + param.getName();
  }

  @Override
  protected void addImports(ASTCDClass ast) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("Request"));
  }

  @Override
  protected void addImport(SymTypeExpression type, ASTCDClass ast) {
    if (!type.isObscureType() && GenService.isCDType(type)) {
      ASTCDCompilationUnit typeCD = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());
      getCD4C().addImport(ast, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print());
      if (!GenService.isCDEnum(type)) {
        getCD4C().addImport(ast, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print()
                                     + AccessPolicyDecorator.SUFFIX);
      }
    }
    else if (!type.isObscureType() && GenService.isCDEnum(type)) {
      ASTCDCompilationUnit typeCD = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());
      //   getCD4C().addImport(ast, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print());
    }
    else if (!type.isObscureType() && GenService.isJavaType(type)) {
      addJavaImport(ast, type);
    }
  }

  @Override
  protected void addJavaImport(ASTCDType ast, SymTypeExpression type) {
    // getCD4C().addImport(ast, box(type.printFullName()));
  }

  @Override
  protected ASTMCObjectType extendedClass(ASTCDMethod command, ASTCDClass ast) {
    ASTMCReturnType retType = command.getMCReturnType();
    SymTypeExpression returnTypeSymbol = command.getSymbol().getType();
    if (returnTypeSymbol.isVoidType()) {
      return buildExtendedCommandType("void");
    }
    if (retType.isPresentMCType()) {
      ASTMCType mcType = retType.getMCType();
      if (returnTypeSymbol.isGenericType()) {
        SymTypeExpression innerType = ((SymTypeOfGenerics) returnTypeSymbol).getArgumentList().get(0);
        addImport(innerType, ast);
        return buildExtendedCommandType(new TSUtilsPrinter().printType(
            GenService.getInnerTypeOfGenerics(mcType).orElse(mcType)));
      }
    }
    addImport(returnTypeSymbol, ast);
    return buildExtendedCommandType(new TSUtilsPrinter().printType(retType));
  }

  @Override
  public ASTMCObjectType buildReturnType(ASTCDMethod command, ASTCDClass ast) {
    ASTMCReturnType retType = command.getMCReturnType();
    if (retType.isPresentMCVoidType()) {
      getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath(STATUS_RESULT));
      return buildPromise(MCTypeFacade.getInstance().createQualifiedType(STATUS_RESULT));
    }
    else if (retType.isPresentMCType()) {
      ASTMCType mcType = retType.getMCType();
      if (GenService.isList(mcType)) {
        return buildPromise(buildListResult(GenService.getInnerType(mcType), ast));
      }
      if (GenService.isOptional(mcType)) {
        return buildPromise(buildSimpleResult(GenService.getInnerType(mcType), ast));
      }
      return buildPromise(buildSimpleResult(UMLPMill.prettyPrint(mcType, false), ast));
    }
    return buildPromise(buildSimpleResult("Object", ast));
  }

  protected ASTMCObjectType buildPromise(ASTMCObjectType type) {
    return buildGeneric("Promise", type);
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.own.TSConstructor";
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.GetEffectedGemIds";
  }

  @Override
  protected String getDoActionTemplate() {
    return "umlp.gui2ts.commands.own.TSDoRun";
  }

  @Override
  protected String getCheckAccessPolicyTemplate() {
    return "umlp.gui2ts.commands.own.TSCheckAccessPolicy";
  }

  @Override
  protected ASTCDClass createCommandDeSer(ASTCDCompilationUnit origCD, ASTCDMethod meth) {
    String cmdName = StringTransformations.capitalize(meth.getName());

    ASTCDClass ast = UMLPMill.cDClassBuilder()
                         .setModifier(PUBLIC.build())
                         .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + cmdName + ">"))
                         .setName(cmdName + DE_SER)
                         .build();

    String baseClassDecorator = origCD.getCDDefinition().getName() + BaseClassObjectMapperDecorator.SUFFIX;
    getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." + cmdName);

    List<String> paramNameList = new ArrayList<>();
    List<SymTypeExpression> typeList = new ArrayList<>();
    List<Boolean> userDeserList = new ArrayList<>();

    boolean useDeSer = true;
    boolean jsonEmpty = true;

    addDeSerImports(ast);

    for (ASTCDParameter p : meth.getCDParameterList()) {
      SymTypeExpression type = p.getSymbol().getType();
      String paramName = p.getName();
      if (isGem(type)) {
        type = GenService.getSymType("number");
        paramName += "GemId";
      }
      paramNameList.add(paramName);
      typeList.add(type);
      if (!type.isObscureType() && GenService.isJavaType(type)) {
        addJavaImport(ast, type);
      }

      String typeName = GenService.printTypeFullName(type);
      userDeserList.add(supportedDeSers.contains(typeName));

      if (!type.isObscureType()) {
        jsonEmpty = jsonEmpty && GenService.calculateJsonInclGenerics(type, paramName).isEmpty();
        useDeSer = useDeSer && supportedDeSers.contains(typeName);

        addDeSerImport(ast, origCD, supportedDeSers.contains(typeName), GenService.isCDOrInnerCDEnum(type),
            baseClassDecorator, typeName);
      }

      if (!type.isObscureType() && GenService.isList(type)) {
        getCD4C().addMethod(ast, getOwnCommand_FromJsonObjectParametersListTemplate(), p.getName());
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

    addedDeSers.add(ast);
    return ast;
  }

  @Override
  protected void addDeSerImports(ASTCDClass ast) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
  }

  @Override
  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD, boolean useDeSer,
                                boolean isCDEnum, String baseClassDecorator, String typeName) {
    if (isCDEnum) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + Names.getSimpleName(typeName));
    }
    if (useDeSer) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + baseClassDecorator);
    }
  }

  @Override
  protected String getOwnCommand_BuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.TSOwnCommand_BuildJson";
  }

  @Override
  protected String getOwnCommand_FromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.TSOwnCommand_FromJsonObject";
  }

  @Override
  protected String getOwnCommand_FromJsonObjectParametersListTemplate() {
    return "umlp.gui2ts.commands.deser.TSOwnCommand_FromJsonObjectParametersList";
  }

  @Override
  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD,
                                ASTCDType domainClass, SymTypeExpression type, SymTypeExpression innerType, String cmdName) {
    getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." + cmdName);
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainClass.getName());
    if (!innerType.isObscureType() && GenService.isCDEnum(innerType)) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + ((SymTypeOfGenerics) type).getArgument(0).print());
    }
    else if (!innerType.isObscureType() && GenService.isJavaType(innerType)) {
      getCD4C().addImport(ast, innerType.printFullName());
    }
  }

}
