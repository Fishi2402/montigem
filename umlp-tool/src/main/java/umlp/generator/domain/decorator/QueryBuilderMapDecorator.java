/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp._visitor.UMLPTraverser;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class QueryBuilderMapDecorator extends AbstractDecorator {

  public static final String SUFFIX = "QueryBuilderMap";
  public static final String SUPER_CLASS = "QueryBuilderMap";
  private final List<ASTCDClass> queryBuilderList;

  public QueryBuilderMapDecorator(GlobalExtensionManagement glex, List<ASTCDClass> queryBuilderList) {
    super(glex);
    this.queryBuilderList = queryBuilderList;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(withOrigin(createMap(origCD, decoratedCD)));
  }

  protected ASTCDClass createMap(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    String className = origCD.getCDDefinition().getName() + SUFFIX;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(SUPER_CLASS))
        .setName(className)
        .build();
    getCD4C().addImport(ast, PathUtil.getBackendRTEOCLPath(SUPER_CLASS));

    List<String> mappedDomainClassList = new ArrayList<>();

    // No Symbol table available
    UMLPTraverser t = UMLPMill.inheritanceTraverser();
    t.add4CDBasis(new CDBasisVisitor2() {
      public void visit(ASTCDClass node) {
        if (queryBuilderList.contains(node)) {
          String baseName = node.getName().replaceAll("QueryBuilder$", "");
          mappedDomainClassList.add(baseName);
        }
      }
    });
    decoratedCD.accept(t);

    getCD4C().addConstructor(ast, getConstructorTemplate(), mappedDomainClassList, className);
    getCD4C().addMethod(ast, getInitTemplate(), className);

    return ast;
  }

  protected String getConstructorTemplate() {
    return "umlp.querybuilder.QueryBuilderMapConstructor";
  }

  protected String getInitTemplate() {
    return "umlp.querybuilder.QueryBuilderMapInit";
  }

  private void addStaticImports(ASTCDType clazz) {
    // UMLP RTE
  }
}
