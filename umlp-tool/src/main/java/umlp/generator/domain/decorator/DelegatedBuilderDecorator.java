/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DelegatedBuilderDecorator extends AbstractDecorator {

  public static final String DELEGATED_BUILDER_SUFFIX = "DelegatedBuilder";

  private ASTCDCompilationUnit origCD;

  public DelegatedBuilderDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.origCD = origCD;
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String name = origCD.getCDDefinition().getName();
    getConcreteDomainClasses(origCD).forEach(c -> {
      if (!c.getModifier().isPresentStereotype()) {
        return;
      }
      if (c.getModifier().getStereotype().contains("ConcretizeModule")) {
        if (!c.isPresentCDExtendUsage()) {
          Log.warn("<<ConcretizeModule>> used without extends", c.get_SourcePositionStart());
          return;
        }
        if (c.getCDExtendUsage().getSuperclassList().size() != 1) {
          Log.warn("<<ConcretizeModule>> extends has more than 1 extends", c.get_SourcePositionStart());
          return;
        }
        astcdPackage.addCDElement(withOrigin(createClass(c, name, origCD)));
      }
    });

  }

  protected ASTCDClass createClass(ASTCDClass domainClass, String name, ASTCDCompilationUnit origCD) {
    String __modulesClassName = domainClass.getCDExtendUsage().getSuperclass(0).printType();

    var modulesClass = domainClass.getEnclosingScope().resolveCDType(__modulesClassName).get();


    String className = getClassName(domainClass.getName(), modulesClass.getName());
    String builderClassName = domainClass.getName()  + BuilderDecorator.BUILDER_SUFFIX;
    String managerName = name + ManagerDecorator.SUFFIX;

    ASTCDClass delegatedBuilderClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(modulesClass.getName() + "Builder"))
        .setName(className).build();


    List<String> pck = new ArrayList<>();
    pck.add(modulesClass.getPackageName().toLowerCase());
    pck.add(modulesClass.getName());
    CD4C.getInstance().addImport(delegatedBuilderClass, String.join(".", pck) + "Builder");
    CD4C.getInstance().addImport(delegatedBuilderClass, String.join(".", pck) + "Observer");

    getCD4C().addAttribute(delegatedBuilderClass, true, false, String.format("protected final %s delegate;", builderClassName));

    getCD4C().addConstructor(delegatedBuilderClass, "umlp.builder.delegated.Constructor", delegatedBuilderClass.getName(), builderClassName);
    getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.ReturnGetter", "isBuildFinished", "boolean");
    getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.VoidSetter", "setBuildFinished", "boolean");

    getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.VoidSetter", "addObserver", "LoginModuleUserObserver");
    getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.VoidSetter", "removeObserver", "LoginModuleUserObserver");

    getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.ReturnGetter", "isINTERNAL_deser_mode", "boolean");
    getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.VoidSetter", "setINTERNAL_deser_mode", "boolean");

    getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.ReturnGetter", "getGemId", "long");
    getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.ReturnGetter", "isBuildable", "boolean");

    getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.build", modulesClass.getName());

    List<ASTCDAttribute> attrFields = CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
        .filter(GenService::isVisibleNotDerived)
        .collect(Collectors.toList());
    List<ASTCDAssocSide> attrAssocs = CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
        .filter(GenService::isVisibleNotDerived)
        .filter(Predicate.not(GenService::isSymbolDerived))
        .collect(Collectors.toList());
    List<ASTCDAssocSide> attrAssocLists = attrAssocs.stream()
        .filter(GenService::isList)
        .collect(Collectors.toList());


    for (var attr : attrFields) {
      String capName = StringTransformations.capitalize(attr.getName());
      getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.ReturnGetter", String.format("check%sPresent", capName), "boolean");
      getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.ReturnGetter", String.format("check%s", capName), "boolean");
      getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.ReturnGetter", String.format("get%s", capName), attr.getMCType().printType());
      getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.VoidSetter", String.format("set%s", capName), attr.getMCType().printType());
      getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.VoidSetter2", String.format("set%s", capName), attr.getMCType().printType());
      getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.VoidSetter", String.format("set%sPresent", capName), "boolean");
      getCD4C().addMethod(delegatedBuilderClass, "umlp.builder.delegated.ChainSetter", String.format("%s", attr.getName()), attr.getMCType().printType(),
          delegatedBuilderClass.getName());

    }
    // TODO: attrAssocs and attrAssocLists

    addImports(domainClass, delegatedBuilderClass);

    return delegatedBuilderClass;

  }


  public static String getClassName(String domainClassName, String moduleClassName) {
    return moduleClassName + "4" + domainClassName + DELEGATED_BUILDER_SUFFIX;
  }

  protected void addImports(ASTCDType domainType, ASTCDType clazz) {
    super.addImports(domainType, clazz);
    getCD4C().addImport(clazz, "java.util.List");
    getCD4C().addImport(clazz, "java.util.Optional");
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
  }
}
