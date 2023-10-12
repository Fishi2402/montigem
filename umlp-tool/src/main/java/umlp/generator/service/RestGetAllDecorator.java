/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.service;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.check.SymTypeExpression;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class RestGetAllDecorator extends AbstractDecorator {

  private static final String NAME = "GetAllRestService";

  public RestGetAllDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE + "service");
    astcdPackage.addCDElement(withOrigin(createClass(origCD)));
  }

  private ASTCDElement createClass(ASTCDCompilationUnit origCD) {
    ASTCDClass clazz = UMLPMill.cDClassBuilder()
        .setName(NAME)
        .setModifier(PUBLIC.build())
        .build();
    glex.replaceTemplate("cd2java.Annotations", clazz, new StringHookPoint("@RestController"));

    addImports(origCD, clazz);

    // Methods
    addGetAll(origCD, clazz);
    addGraph(origCD, clazz);

    return clazz;
  }

  private void addGraph(ASTCDCompilationUnit origCD, ASTCDClass clazz) {
    ASTCDMethodSignature graph = getCD4C().addMethod(clazz, "umlp.service4teaching.graph",
        origCD.getCDDefinition().getName(),
        origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isAbstract()).collect(Collectors.toList()),
        origCD.getCDDefinition().getCDAssociationsList().stream().flatMap(a -> {
          List<Map.Entry<SymTypeExpression, ASTCDAssocSide>> sides = new ArrayList<>();
          if (a.getLeft().getSymbol().isIsDefinitiveNavigable()) {
            sides.add(Map.entry(a.getRight().getSymbol().getType(), a.getLeft()));
          }
          if (a.getRight().getSymbol().isIsDefinitiveNavigable()) {
            sides.add(Map.entry(a.getLeft().getSymbol().getType(), a.getRight()));
          }
          return sides.stream();
        }).collect(Collectors.groupingBy(e -> e.getKey().print()))
    );

    String annotation = "@GetMapping(value = \"/graph\") @CrossOrigin";
    glex.replaceTemplate("cd2java.Annotations", graph, new StringHookPoint(annotation));
    addAnnotationToParameters(graph, Arrays.asList("@PathVariable"));
  }

  private void addGetAll(ASTCDCompilationUnit origCD, ASTCDClass clazz) {
    List<ASTCDClass> classes = origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getModifier().isAbstract())
        .collect(Collectors.toList());
    ASTCDMethodSignature getAll = getCD4C().addMethod(clazz, "umlp.service4teaching.getAll",
        origCD.getCDDefinition().getName(), classes);
    String annotation = "@GetMapping(value = \"/getall/{type}\") @CrossOrigin";
    glex.replaceTemplate("cd2java.Annotations", getAll, new StringHookPoint(annotation));
    addAnnotationToParameters(getAll, Arrays.asList("@PathVariable"));
  }

  private void addImports(ASTCDCompilationUnit origCD, ASTCDClass clazz) {
    getCD4C().addImport(clazz, "org.springframework.http.ResponseEntity");
    getCD4C().addImport(clazz, "org.springframework.web.bind.annotation.CrossOrigin");
    getCD4C().addImport(clazz, "org.springframework.web.bind.annotation.GetMapping");
    getCD4C().addImport(clazz, "org.springframework.web.bind.annotation.PathVariable");
    getCD4C().addImport(clazz, "org.springframework.web.bind.annotation.RestController");
    getCD4C().addImport(clazz, "org.springframework.web.bind.annotation.RestController");
    getCD4C().addImport(clazz, "java.util.stream.Collectors");
    getCD4C().addImport(clazz, String.format("%s.%s", getPackageName(origCD, ""), origCD.getCDDefinition().getName() + ManagerDecorator.SUFFIX));
    origCD.getCDDefinition().getCDClassesList()
        .stream().filter(c -> !c.getModifier().isAbstract())
        .forEach(c -> {
          getCD4C().addImport(clazz, String.format("%s.%sDeSer", getPackageName(origCD, ""), c.getName()));
          getCD4C().addImport(clazz, String.format("%s.%s", getPackageName(origCD, ""), c.getName()));
          getCD4C().addImport(clazz, String.format("%s.%sBuilder", getPackageName(origCD, ""), c.getName()));
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
