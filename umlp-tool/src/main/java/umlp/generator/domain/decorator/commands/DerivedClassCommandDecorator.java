/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.commands;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.util.PathUtil;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

/**
 * Commands to request data from derived classes
 */
//Derived classes are no longer supported
@Deprecated
public class DerivedClassCommandDecorator extends AbstractDeSerDecorator {

  public static final String COMMAND_DERIVED_CLASS_PACKAGE = COMMAND_PACKAGE + ".derivedclass";
  public static final String GET_SUFFIX = "_get";

  public final static List<String> nativeTypes = List.of("String", "Void", "Boolean", "Char", "Byte", "Short", "Integer", "Long", "Float", "Double");


  public DerivedClassCommandDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    addedDeSers.clear();
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, COMMAND_DERIVED_CLASS_PACKAGE);
    getDerivedDomainClasses(origCD)
            .forEach(a -> astcdPackage.addAllCDElements(withOrigin(createClass(origCD, a, astcdPackage.getName()))));
  }

  protected Collection<ASTCDClass> createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass, String packageName) {
    List<ASTCDClass> retList = Lists.newArrayList();
    domainClass.getCDMemberList().stream()
        .filter(member -> member instanceof ASTCDAttribute )
        .map(x -> (ASTCDAttribute)x)
        .filter(attr -> !attr.getMCType().printType().equals("void"))
        .map(domainAttr -> createGetMethod(origCD, domainClass,
            domainAttr.getName(), domainAttr.getSymbol().getType().printFullName(), packageName, false))
        .forEach(x -> {
          retList.add(x);
          retList.add(createDeSer(origCD, x));
        });

    CDSymbolTables.getAssociationsInHierarchy(domainClass).forEach(assoc -> {
      boolean isList = GenService.isList(assoc);
      ASTCDClass getMethod = createGetMethod(origCD, domainClass, assoc.getName(), assoc.getSymbol().getType().printFullName(), packageName, isList);
      retList.add(getMethod);
      retList.add(createDeSer(origCD, getMethod));
    });

    return retList;
  }

  protected String cleanupType(ASTCDCompilationUnit origCD, String type) {
    String pckName = getPackageName(origCD, DEFAULT_PACKAGE);
    // Lower-case the package name, if from Domain
    if (type.toLowerCase().startsWith(pckName.toLowerCase())) {
      return pckName + type.substring(pckName.length());
    }
    return StringTransformations.uncapitalize(type);
  }

  protected ASTCDClass createGetMethod(ASTCDCompilationUnit origCD, ASTCDClass domainClass,
                                      String attrName,
                                      String type,
                                      String packageName,
                                      boolean isList) {
    String classname = StringTransformations.capitalize(domainClass.getName())
        + "_"
        + StringTransformations.capitalize(attrName)
        + GET_SUFFIX;

    type = cleanupType(origCD, type);


    String fqnType = type;
    if (nativeTypes.contains(fqnType) || nativeTypes.contains(boxType(fqnType)))
      fqnType = "java.lang." + boxType(type);


    // box the type, if needed
    type = this.boxType(type);


    // TODO: nativeType => assoc


    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(classname)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(extendedClass(type)))
            .build();

    // attributes and getter
    AccessorDecorator accessor = new AccessorDecorator(glex);

    // constructor
    addConstructor(ast, getConstructorTemplate(), classname, packageName, domainClass.getName(), attrName);

    // methods
    getCD4C().addMethod(ast, getAdd_DoRunTemplate(), domainClass.getName(),
        attrName,
            type,
            getCorrectManager(domainClass),
            fqnType,
            isList);
    getCD4C().addMethod(ast, getAssocsCheckAccessPolicyTemplate());

    // Importe
    addImports(origCD, ast);

    return ast;
  }

  protected String boxAsList(String type) {
    return "List<" + type + ">";
  }

  protected String boxType(String type) {
    if (type.equals("int"))
      type = "Integer";
    else if (type.equals("boolean"))
      type = "Boolean";
    else if (type.equals("char"))
      type = "Character";
    else if (type.equals("byte"))
      type = "Byte";
    else if (type.equals("short"))
      type = "Short";
    else if (type.equals("long"))
      type = "Long";
    else if (type.equals("float"))
      type = "Float";
    else if (type.equals("double"))
      type = "Double";
    return type;
  }

  protected String getAssocsCheckAccessPolicyTemplate() {
    return "umlp.commands.derivedclass.AssocsCheckAccessPolicy";
  }

  protected String getAdd_DoRunTemplate() {
    return "umlp.commands.derivedclass.Get_DoAction";
  }


  protected String getConstructorTemplate() {
    return "umlp.commands.derivedclass.Constructor";
  }



  protected void addConstructor(
      ASTCDClass ast, String template, String classname, String packageName, String domainClass, String attributeName
  ) {
    getCD4C().addConstructor(ast, template, classname, packageName, domainClass, attributeName);
  }

  protected String extendedClass(String type) {
    return "Request<"+type+">";
  }


  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.commands.GetEffectedGemIds";
  }

  protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, ASTCDClass cmdClass) {
    String cmdName = cmdClass.getName();

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + cmdName + ">"))
            .setName(cmdName+DE_SER)
            .build();

    getCD4C().addMethod(ast, getFromJsonObjectTemplate(), cmdName,
            cmdClass.getCDAttributeList().stream().map(a -> a.getName()).collect(Collectors.toList()));
    getCD4C().addMethod(ast, getBuildJsonTemplate(), cmdName,
            cmdClass.getCDAttributeList().stream().map(a -> a.getName()).collect(Collectors.toList()));

    addDeSerImports(origCD, ast, cmdName);

    addedDeSers.add(ast);
    return ast;
  }

  protected void addDeSerImports(ASTCDCompilationUnit origCD, ASTCDClass ast, String cmdName) {
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
  }

  protected String getBuildJsonTemplate() {
    return "umlp.commands.assocs.BuildJson";
  }

  protected String getFromJsonObjectTemplate() {
    return "umlp.commands.assocs.FromJsonObject";
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDClass ast) {
    // Importe
    getCD4C().addImport(ast, "com.google.common.collect.Lists");
    getCD4C().addImport(ast, "com.google.common.collect.Sets");
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("SimpleResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("ListResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, PathUtil.getBackendRTERequestPath("Request"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandManager"));
    getCD4C().addImport(ast, PathUtil.getBackendRTEUtilPath("LogUtil"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GeneralErrorCollector"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("LoadingState"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+origCD.getCDDefinition().getName()+ ManagerDecorator.SUFFIX);
  }
}
