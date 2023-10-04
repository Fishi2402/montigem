/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.commands;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
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
import de.monticore.umlstereotype._ast.ASTStereotypeBuilder;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.AccessPolicyDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

public class CmdDecorator extends AbstractDeSerDecorator {

  public static final String COMMAND = "command";

  protected Set<String> supportedDeSers;

  public CmdDecorator(GlobalExtensionManagement glex, Set<String> supportedDeSers) {
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
            .forEach(m -> {
              astcdPackage.addCDElement(withOrigin(createClass(origCD, m)));
              //astcdPackage.addCDElement(withOrigin(createDeSer(origCD, m)));
            }));


    origCD.getCDDefinition().getCDInterfacesList().stream()
        .filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c -> c.getCDMethodList()
            .stream()
            .filter(m -> GenService.hasStereotype(m.getModifier(), COMMAND))
            .forEach(m -> {
              astcdPackage.addCDElement(withOrigin(createClass(origCD, m)));
              //astcdPackage.addCDElement(withOrigin(createDeSer(origCD, m)));
            }));

  }


  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDMethod meth) {
    String classname = StringTransformations.capitalize(meth.getName());

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(classname)
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(extendedClass()))
        .build();
    ASTStereotypeBuilder stereotype = UMLPMill.stereotypeBuilder();
   /* stereotype.addValues(
        TopDecorator.NEEDS_TOP_STEREO_BUILDER.apply(
            String.format("Custom implementation for class: %s required. Contains do_run", classname)));*/

    ast.getModifier().setStereotype(stereotype.build());
    List<ASTCDAttribute> attrList = Lists.newArrayList();
    meth.getCDParameterList().forEach(p -> attrList.add(getCD4C().addAttribute(ast,
        "protected " + UMLPMill.prettyPrint(p.getMCType(), false) + " " + p.getName())));

    getCD4C().addConstructor(ast, getConstructorTemplate(), classname, getPackageName(origCD, COMMAND_PACKAGE), attrList);

    getCD4C().addMethod(ast, getDoRunTemplate());

    // TODO: generate CheckAccessPolicy if domain attributes are available
    // getCD4C().addMethod(ast, "umlp.commands.GeneralCheckAccessPolicy", classname);
    getCD4C().addMethod(ast, getGetEffectedGemIdsTemplate(), Lists.newArrayList());

    AccessorDecorator accessor = new AccessorDecorator(glex);
    attrList.forEach(a -> ast.addAllCDMembers(accessor.decorate(a)));

    addImports(origCD, ast);

    List<SymTypeExpression> domainParameters = new ArrayList<>();

    meth.getCDParameterList().forEach(p -> {
      SymTypeExpression type = p.getSymbol().getType();
      if (GenService.isCDType(type)) {
        ASTCDCompilationUnit typeCD = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());
        getCD4C().addImport(ast, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print());
        getCD4C().addImport(ast, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print()
            + AccessPolicyDecorator.SUFFIX);
        domainParameters.add(type);
      } else if (GenService.isCDEnum(type)) {
        ASTCDCompilationUnit typeCD = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());
        getCD4C().addImport(ast, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print());
      } else if (GenService.isJavaType(type)) {
        addJavaImport(ast, type);
      }
    });

    getCD4C().addMethod(ast, getCheckAccessPolicyTemplate(), domainParameters);

    return ast;
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass ast) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("StatusResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, PathUtil.getBackendRTEUtilPath("LogUtil"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GeneralErrorCollector"));
    getCD4C().addImport(ast, PathUtil.getBackendRTEErrorPath("NotImplementedException"));
    getCD4C().addImport(ast, "com.google.common.collect.Sets");
    getCD4C().addImport(ast, "java.util.*");

  }

  protected void addJavaImport(ASTCDClass ast, SymTypeExpression type) {
    getCD4C().addImport(ast, type.printFullName());
  }

  protected String extendedClass() {
    return "Command<Void>";
  }

  protected String getConstructorTemplate() {
    return "umlp.commands.cmd.Constructor";
  }

  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.commands.GetEffectedGemIds";
  }

  protected String getDoRunTemplate() {
    return "umlp.commands.cmd.DoRun";
  }

  protected String getCheckAccessPolicyTemplate() {
    return "umlp.commands.cmd.CheckAccessPolicy";
  }

  protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, ASTCDMethod meth) {
    String cmdName = StringTransformations.capitalize(meth.getName());

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + cmdName + ">"))
        .setName(cmdName + DE_SER)
        .build();

 /*
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
    boolean makeAbstract = jsonStr.isEmpty() && !useDeSer;
    getCD4C().addMethod(ast, getAdd_FromJsonObjectTemplate(), cmdName, name, typeName,
        GenService.isInnerCDEnum(type), jsonStr, useDeSer, baseClassDecorator, makeAbstract);
    getCD4C().addMethod(ast, getAdd_BuildJsonTemplate(), cmdName, name, typeName,
        GenService.isInnerCDEnum(type), getter, useDeSer,
        baseClassDecorator, makeAbstract);

    addDeSerImport(ast, origCD, domainClass, type, innerType, cmdName);
    if (makeAbstract) {
      ast.setModifier(PUBLIC_ABSTRACT.build());
    }
    addedDeSers.add(ast);

  */
    return ast;
  }

  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD,
                                ASTCDType domainClass, SymTypeExpression type, SymTypeExpression innerType, String cmdName) {
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainClass.getName());
    if (GenService.isCDEnum(innerType)) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + ((SymTypeOfGenerics) type).getArgument(0).print());
    } else if (GenService.isJavaType(innerType)) {
      getCD4C().addImport(ast, innerType.printFullName());
    }
  }

  protected String getAdd_BuildJsonTemplate() {
    return "umlp.commands.deser.Add_BuildJson";
  }

  protected String getAdd_FromJsonObjectTemplate() {
    return "umlp.commands.deser.Add_FromJsonObject";
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
