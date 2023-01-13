package umlp.generator.domain.decorator;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PRIVATE;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ManagerDbObserverDecorator extends AbstractDecorator {

  public static final String SUFFIX = "DbObserver";

  public ManagerDbObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(createDBObserver(origCD));
  }

  protected ASTCDClass createDBObserver(ASTCDCompilationUnit origCD) {
    String cDName = origCD.getCDDefinition().getName();

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(cDName +SUFFIX)
        .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(cDName + "Observer"))
        .build();
    // Methods
    origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getModifier().isDerived())
        .forEach(c -> {
          getCD4C().addMethod(ast, "umlp.dbobserver.manager.NotifyAdd", cDName, c.getName());
          getCD4C().addMethod(ast, "umlp.dbobserver.manager.NotifyDelete", c.getName());
        });

    // Attributes
    origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getModifier().isDerived())
        .forEach(c -> {
          ast.addCDMember(getDAOAttribute(c));
        });

    return ast;
  }

  private ASTCDAttribute getDAOAttribute(ASTCDClass domainClass){
    ASTCDAttribute domainClassDaoAttr = getCDAttributeFacade().createAttribute(PRIVATE.build(),
        getMCTypeFacade().createQualifiedType(domainClass.getName()+"DAO"),
        StringTransformations.uncapitalize(domainClass.getName())+"DAO");
    getGlex().replaceTemplate("cd2java.Value", domainClassDaoAttr, new StringHookPoint(" = new "+domainClass.getName()+"DAO"+"()"));
    return domainClassDaoAttr;
  }
}
