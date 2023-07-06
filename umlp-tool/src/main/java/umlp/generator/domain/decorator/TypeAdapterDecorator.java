/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TypeAdapterDecorator extends AbstractDecorator {

  public static final String SUFFIX = "TypeAdapter";

  public TypeAdapterDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
            .forEach(c -> astcdPackage.addAllCDElements(withOrigin(createClasses(c, origCD.getCDDefinition().getName()))));
  }

  protected List<ASTCDClass> createClasses(ASTCDClass domainClass, String name) {
    String className = domainClass.getName();
    List<ASTCDClass> classList = Lists.newArrayList();

    for (ASTCDAssocSide assoc: CDSymbolTables.getAssociations(domainClass).stream().filter(a -> GenService.isList(a)).collect(Collectors.toList())) {
      ASTCDClass ast = UMLPMill.cDClassBuilder()
              .setModifier(PUBLIC.build())
              .setName(className + StringTransformations.capitalize(assoc.getName()) + SUFFIX).build();
      classList.add(ast);
      getCD4C().addMethod(ast, "umlp.typeadapter.Deserialize",
              name, assoc.getName(), assoc.getSymbol().getType().print());
      getCD4C().addMethod(ast, "umlp.typeadapter.Serialize",
              assoc.getSymbol().getType().print());

      getCD4C().addImport(ast,"com.google.gson.*");
      getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("*"));
      getCD4C().addImport(ast, "java.lang.reflect.Type");

    }
    return classList;
  }

}
