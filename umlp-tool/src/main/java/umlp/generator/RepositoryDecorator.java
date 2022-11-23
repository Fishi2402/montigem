package umlp.generator.decorator;

import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceBuilder;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlmodifier._ast.ASTModifier;
import umlp.UMLPMill;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class RepositoryDecorator extends AbstractDecorator {

  public RepositoryDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String name = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c -> astcdPackage.addCDElement(createClass(c, name)));
  }

  // TODO remove artifactinternal
  protected ASTCDInterface createClass(ASTCDClass domainClass, String name) {
    ASTCDExtendUsage extendUsage = getExtendUsageFacade().createCDExtendUsage("Neo4jRepository<"+domainClass.getName()+", Long>");
    ASTModifier modifier = PUBLIC.build();
    ASTCDInterfaceBuilder builder = UMLPMill.cDInterfaceBuilder()
        .setModifier(modifier)
        .setCDExtendUsage(extendUsage)
        .setName(domainClass.getName()+"Repository");
    ASTCDInterface interfaze = builder.build();
    addImports(interfaze);
    return interfaze;
  }

  protected void addImports(ASTCDInterface interfaze) {
    getCD4C().addImport(interfaze, "org.springframework.data.neo4j.repository.Neo4jRepository");
  }

}
