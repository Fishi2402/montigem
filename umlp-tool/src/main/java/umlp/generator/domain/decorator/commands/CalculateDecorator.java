package umlp.generator.domain.decorator.commands;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypePrimitive;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.util.PathUtil;

import java.util.Optional;
import java.util.Set;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;


// Setzt eine der Möglichkeiten um, derived attributes berechnen zu lassen:
// Der Client sendet einen calculate Command zum Server und erhält nur das aktuelle Ergebnis.
public class CalculateDecorator extends AbstractDeSerDecorator {

  public static final String SUFFIX = "_calculate";
  private final Set<String> supportedDeSers;


  public CalculateDecorator(GlobalExtensionManagement glex, Set<String> supportedDeSers) {
    super(glex);
    this.supportedDeSers = supportedDeSers;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    addedDeSers.clear();
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c -> {
          CDSymbolTables.getAttributesInHierarchy(c)
              .stream()
              .filter(a -> a.getModifier().isDerived())
              .forEach(a -> {
                astcdPackage.addCDElement(withOrigin(createClass(origCD, a.getName(), a.getSymbol().getType(), c, GenService.isCDEnum(a.getSymbol().getType()))));
                astcdPackage.addCDElement(withOrigin(createDeSer(origCD, a.getName(), a.getSymbol().getType(), c)));
              });
          CDSymbolTables.getAssociationsInHierarchy(c)
              .stream()
              .filter(a -> a.getModifier().isDerived())
              .filter(a -> !GenService.isBidirectional(a))
              .forEach(a -> {
                astcdPackage.addCDElement(withOrigin(createClass(origCD, a.getName(), a.getSymbol().getType(), c, true)));
                astcdPackage.addCDElement(withOrigin(createDeSer(origCD, a.getName(), getSymType("long"), c)));
              });
        });
  }


  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, String name, SymTypeExpression type, ASTCDClass domainClass, boolean needsImport) {
    String classname = domainClass.getName()+SUFFIX
        + StringTransformations.capitalize(name);

    String t = needsImport ? getShortType(type) : getType(type);

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(classname)
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<" + t + ">"))
        .build();

    ASTCDAttribute gemAttr = getCD4C().addAttribute(ast, "protected long gemId");

    addConstructor(ast, classname, getPackageName(origCD, COMMAND_PACKAGE));

    String originalClassname = domainClass.getName();

    if(GenService.isList(type)) {
      getCD4C().addMethod(ast, getDoRunListTemplate(), classname, originalClassname, t, getCorrectManager(domainClass), name);
    }else{
      getCD4C().addMethod(ast, getDoRunSimpleTemplate(), classname, originalClassname, t, getCorrectManager(domainClass), name);
    }
    getCD4C().addMethod(ast, getGetEffectedGemIdsTemplate(), Lists.newArrayList("getGemId()"));

    AccessorDecorator accessor = new AccessorDecorator(glex);
    ast.addAllCDMembers(accessor.decorate(gemAttr));

    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("LoadingState"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("ListResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("SimpleResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+domainClass.getName());
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+getCorrectManager(domainClass));
    getCD4C().addImport(ast, "com.google.common.collect.Sets");
    if (GenService.isJavaType(type)) {
      addJavaImport(ast, type);
    } else if (needsImport) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+ t);
    }

    return ast;
  }

  protected void addConstructor(ASTCDClass ast, String classname, String packageName) {
    getCD4C().addConstructor(ast, getConstructorTemplate(),
        classname, "", "" , packageName);
  }

  protected String getType(SymTypeExpression type) {
    String t = null;
    if(type.isPrimitive()){
      t = SymTypePrimitive.box(type.print());
    }else if(type.isGenericType()){
      var genType = (SymTypeOfGenerics)type;

      if(GenService.isList(type)){
        SymTypeExpression inner = genType.getArgument(0);
        if(inner.isPrimitive()){
          t = SymTypePrimitive.box(inner.print());
        }else{
          t = inner.printFullName();
        }
      }else{
        Log.error("0x3F9F77 Can not generate DeSer for generic type " + type.print());
      }

    }else{
      t = type.printFullName();
    }
    return t;
  }

  protected String getShortType(SymTypeExpression type) {
    String t = null;
    if(type.isPrimitive()){
      t = SymTypePrimitive.box(type.print());
    }else if(type.isGenericType()){
      var genType = (SymTypeOfGenerics)type;

      if(GenService.isList(type)){
        SymTypeExpression inner = genType.getArgument(0);
        if(inner.isPrimitive()){
          t = SymTypePrimitive.box(inner.print());
        }else{
          t = inner.print();
        }
      }else{
        Log.error("0x3F9F77 Can not generate DeSer for generic type " + type.print());
      }

    }else{
      t = type.print();
    }
    return t;
  }

  protected void addJavaImport(ASTCDClass ast, SymTypeExpression type) {
    getCD4C().addImport(ast, type.printFullName());
  }

  protected String getConstructorTemplate() {
    return "umlp.commands.get.Constructor";
  }

  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.commands.GetEffectedGemIds";
  }

  protected String getDoRunSimpleTemplate() {
    return "umlp.commands.calculate.DoRunSimple";
  }

  protected String getDoRunListTemplate() {
    return "umlp.commands.calculate.DoRunList";
  }

  protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, String name, SymTypeExpression type, ASTCDClass domainClass) {
    String cmdName = domainClass.getName()+SUFFIX
        + StringTransformations.capitalize(name);

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + cmdName + ">"))
        .setName(cmdName+DE_SER)
        .build();

    String jsonStr;
    String typeName;
    if (GenService.isOptional(type)) {
      jsonStr = GenService.calculateJSon(((SymTypeOfGenerics)type).getArgument(0));
      typeName = ((SymTypeOfGenerics)type).getArgument(0).printFullName();
    } else {
      jsonStr = GenService.calculateJSon(type);
      typeName = type.printFullName();
    }
    String baseClassDecorator = origCD.getCDDefinition().getName() + BaseClassObjectMapperDecorator.SUFFIX;
    boolean useDeSer = supportedDeSers.contains(typeName);
    boolean makeAbstract = jsonStr.equals("") && !useDeSer;

    getCD4C().addMethod(ast, getGet_FromJsonObjectTemplate(), cmdName);
    getCD4C().addMethod(ast, getGet_BuildJsonTemplate(), cmdName);

    addDeSerImport(ast, origCD, useDeSer, false, baseClassDecorator, "", cmdName);
    if (makeAbstract) {
      ast.setModifier(PUBLIC_ABSTRACT.build());
    }
    addedDeSers.add(ast);
    return ast;
  }

  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD, boolean useDeSer,
      boolean isCDEnum, String baseClassDecorator, String typeName, String cmdName) {
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    if (useDeSer) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + baseClassDecorator);
    }
  }

  protected String getGet_BuildJsonTemplate() {
    return "umlp.commands.deser.Get_BuildJson";
  }

  protected String getGet_FromJsonObjectTemplate() {
    return "umlp.commands.deser.Get_FromJsonObject";
  }

  // TODO (MB): Bessere Lösung?
  protected SymTypeExpression getSymType(String type) {
    Optional<TypeSymbol> primitive = BasicSymbolsMill.globalScope().resolveType(type);
    if(primitive.isPresent()){
      return SymTypeExpressionFactory.createPrimitive(primitive.get());
    }else{
      return SymTypeExpressionFactory.createObscureType();
    }
  }

}
