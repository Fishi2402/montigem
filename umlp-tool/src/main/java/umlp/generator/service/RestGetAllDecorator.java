/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.service;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class RestGetAllDecorator extends AbstractDecorator {

  private static final String NAME = "GetAllRestService";

  public RestGetAllDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE+"service");
    astcdPackage.addCDElement(createClass(origCD));
  }

  private ASTCDElement createClass(ASTCDCompilationUnit origCD) {
    ASTCDClass clazz = UMLPMill.cDClassBuilder()
        .setName(NAME)
        .setModifier(PUBLIC.build())
        .build();
    glex.replaceTemplate("cd2java.Annotations", clazz, new StringHookPoint("@RestController"));

    addImports(origCD, clazz);

    // Methods
    ASTCDMethodSignature getAll = getCD4C().addMethod(clazz, "umlp.service4teaching.getAll",
        origCD.getCDDefinition().getName(), origCD.getCDDefinition().getCDClassesList());
    String annotation = "@GetMapping(value = \"/getall/{type}\") @CrossOrigin";
    glex.replaceTemplate("cd2java.Annotations", getAll, new StringHookPoint(annotation));
    addAnnotationToParameters(getAll, Arrays.asList("@PathVariable"));
    return clazz;
  }

  private void addImports(ASTCDCompilationUnit origCD, ASTCDClass clazz) {
    getCD4C().addImport(clazz, "org.springframework.http.ResponseEntity");
    getCD4C().addImport(clazz, "org.springframework.web.bind.annotation.CrossOrigin");
    getCD4C().addImport(clazz, "org.springframework.web.bind.annotation.GetMapping");
    getCD4C().addImport(clazz, "org.springframework.web.bind.annotation.PathVariable");
    getCD4C().addImport(clazz, "org.springframework.web.bind.annotation.RestController");
    getCD4C().addImport(clazz, "org.springframework.web.bind.annotation.RestController");
    getCD4C().addImport(clazz, "java.util.stream.Collectors");
    getCD4C().addImport(clazz, String.format("%s.%s",getPackageName(origCD, ""), origCD.getCDDefinition().getName() + ManagerDecorator.SUFFIX));
    origCD.getCDDefinition().getCDClassesList().forEach(c -> {
      getCD4C().addImport(clazz, String.format("%s.%sDeSer", getPackageName(origCD, ""), c.getName()));
    });
  }

  private void addAnnotationToParameters(ASTCDMethodSignature m, List<String> annotations) {
    if (annotations.size() != m.getCDParameterList().size()) {
      return;
    }
    for (int i = 0; i < m.getCDParameterList().size(); i++) {
      m.getCDParameter(i).setAnnotation(Optional.of(annotations.get(i)));
    }
  }

}
