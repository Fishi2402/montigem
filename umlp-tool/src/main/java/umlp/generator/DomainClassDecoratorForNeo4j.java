package umlp.generator.decorator;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

public class DomainClassDecoratorForNeo4j extends DomainClassDecorator {

  public DomainClassDecoratorForNeo4j(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected void setClassAnnotationTemplate(ASTCDClass clazz) {
    glex.replaceTemplate("cd2java.Annotations", clazz,
        new TemplateHookPoint("umlp.neo4jannotations.Neo4jClassAnnotation", clazz.getName()));
  }

  @Override
  protected void setAttributeAnnotationTemplate(ASTCDAttribute attribute) {
    glex.replaceTemplate("cd2java.Annotations", attribute,
        new TemplateHookPoint("umlp.neo4jannotations.AttributeAnnotations", attribute.getName()));
  }

  @Override
  public void setAssocAnnotations(ASTCDClass domainClass, ASTCDAttribute attribute) {
    glex.replaceTemplate("cd2java.Annotations", attribute,
        new TemplateHookPoint("umlp.neo4jannotations.AssocAnnotations",
            domainClass.getName(), attribute.getName()));
  }

  @Override
  protected void setGemAnnotationTemplate(ASTCDAttribute gemAttr) {
    glex.replaceTemplate("cd2java.Annotations", gemAttr,
        new TemplateHookPoint("umlp.neo4jannotations.IDAnnotation"));
  }

  @Override
  protected void addDBAnnotationInfo(ASTCDType clazz) {
    getCD4C().addImport(clazz, "import org.springframework.data.neo4j.core.schema.*");
  }
}
