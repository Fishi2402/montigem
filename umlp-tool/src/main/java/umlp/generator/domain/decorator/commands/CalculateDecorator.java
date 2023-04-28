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
import umlp.generator.domain.decorator.ManagerDecorator;
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
                astcdPackage.addCDElement(createClass(origCD, a.getName(), a.getSymbol().getType(), c, false));
                astcdPackage.addCDElement(createDeSer(origCD, a.getName(), a.getSymbol().getType(), c));
              });
          CDSymbolTables.getAssociationsInHierarchy(c)
              .stream()
              .filter(a -> a.getModifier().isDerived())
              .filter(a -> !GenService.isBidirectional(a))
              .forEach(a -> {
                astcdPackage.addCDElement(createClass(origCD, a.getName(), a.getSymbol().getType(), c, true));
                astcdPackage.addCDElement(createDeSer(origCD, a.getName(), getSymType("long"), c));
              });
        });
  }


  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, String name, SymTypeExpression type, ASTCDClass domainClass, boolean isGem) {
    String classname = domainClass.getName()+SUFFIX
        + StringTransformations.capitalize(name);

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

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(classname)
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Command<" + t + ">"))
        .build();

    ASTCDAttribute gemAttr = getCD4C().addAttribute(ast, "protected long gemId");

    getCD4C().addConstructor(ast, "umlp.commands.get.Constructor", classname, "", "" ,getPackageName(origCD, COMMAND_PACKAGE));

    String originalClassname = domainClass.getName();

    if(GenService.isList(type)) {
      getCD4C().addMethod(ast, "umlp.commands.calculate.DoRunList", classname, originalClassname, t, origCD.getCDDefinition().getName(), name);
    }else{
      getCD4C().addMethod(ast, "umlp.commands.calculate.DoRunSimple", classname, originalClassname, t, origCD.getCDDefinition().getName(), name);
    }
    getCD4C().addMethod(ast, "umlp.commands.GetEffectedGemIds", Lists.newArrayList("getGemId()"));

    AccessorDecorator accessor = new AccessorDecorator(glex);
    ast.addAllCDMembers(accessor.decorate(gemAttr));

    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("LoadingState"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("ListResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("SimpleResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+domainClass.getName());
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+origCD.getCDDefinition().getName()+ ManagerDecorator.SUFFIX);
    if (GenService.isJavaType(type)) {
      getCD4C().addImport(ast, type.printFullName());
    } else if (isGem) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+ t);
    }

    return ast;
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

    getCD4C().addMethod(ast, "umlp.commands.deser.Get_FromJsonObject", cmdName);
    getCD4C().addMethod(ast, "umlp.commands.deser.Get_BuildJson", cmdName);

    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    if (useDeSer) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + baseClassDecorator);
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
    if(primitive.isPresent()){
      return SymTypeExpressionFactory.createPrimitive(primitive.get());
    }else{
      return SymTypeExpressionFactory.createObscureType();
    }
  }

}
