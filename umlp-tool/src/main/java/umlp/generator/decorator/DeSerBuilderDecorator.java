/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import umlp.UMLPMill;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.decorator.GenService.createInitialValue;

public class DeSerBuilderDecorator extends DomainClassDecorator {

  public static final String DESER_BUILDER_SUFFIX = "DeSerBuilder";

  public DeSerBuilderDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String name = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(c, name)));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass, String name) {
    String className = domainClass.getName() + DESER_BUILDER_SUFFIX;

    ASTCDClass deserBuilderClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(domainClass.getName() + BuilderDecorator.BUILDER_SUFFIX))
            .setName(className).build();

    // Constructors
    getCD4C().addConstructor(deserBuilderClass, "umlp.deser_builder.Constructor", domainClass.getName());

    // Attributes
    List<ASTCDAttribute> attributes = getAllAttributes(domainClass).attrList;

    // Methods
    for (ASTCDAttribute a : attributes) {
      if (GenService.isGemList(a)) {
        getCD4C().addMethod(
            deserBuilderClass,
            "umlp.deser_builder.setGemList",
            deserBuilderClass.getName(),
            a.printType(),
            a.getName()
        );
      }else if(GenService.isGem(a)){
        getCD4C().addMethod(
            deserBuilderClass,
            "umlp.deser_builder.setGem",
            deserBuilderClass.getName(),
            a.printType(),
            a.getName()
        );
      }else{
        ASTCDMethodSignature m = getCD4C().addMethod(
            deserBuilderClass,
            "umlp.deser_builder.setSimple",
            deserBuilderClass.getName(),
            a.printType(),
            a.getName()
        );
        glex.replaceTemplate("cd2java.Annotations", m, new StringHookPoint("@Override"));
      }
    }


    // Imports
    addImports(deserBuilderClass);

    return deserBuilderClass;
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "java.util.List");
    getCD4C().addImport(clazz, "java.util.Optional");
    getCD4C().addImport(clazz, "umlp.rte.*");
  }

}
