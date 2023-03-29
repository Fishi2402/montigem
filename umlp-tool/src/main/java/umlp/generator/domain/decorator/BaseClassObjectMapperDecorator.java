/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class BaseClassObjectMapperDecorator extends AbstractDecorator {

  public static final String SUFFIX = "BaseClassObjectMapper";

  public BaseClassObjectMapperDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(createClass(origCD));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass mapperClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(name+SUFFIX)
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("BaseClassObjectMapper"))
            .build();

    List<String> classesList = origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getSymbol().isIsDerived())
        .map(c -> Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE), c.getName()))
        .collect(Collectors.toList());

    // constructor
    getCD4C().addConstructor(mapperClass, "umlp.baseclassobjectmapper.Constructor",
        new ArrayList(), name+SUFFIX);

    // add methods
    getCD4C().addMethod(mapperClass, "umlp.baseclassobjectmapper.Init", name);

    addImports(mapperClass);

    return mapperClass;
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("BaseClassObjectMapper"));
  }


}
