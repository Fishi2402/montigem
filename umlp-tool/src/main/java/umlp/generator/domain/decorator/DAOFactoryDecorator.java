package umlp.generator.domain.decorator;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDAssociationTOP;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PROTECTED_STATIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DAOFactoryDecorator extends AbstractDecorator {

  public static final String SUFFIX = "DAOFactory";
  private static final String CUSTOM_TEMPLATE_PATH = "umlp.daofactory.";

  public DAOFactoryDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(withOrigin(createClass(origCD)));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    ASTCDDefinition cdDefinition = origCD.getCDDefinition();
    String name = cdDefinition.getName();

    ASTCDClass daoFactory = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(name + SUFFIX)
        .addCDMember(getCDAttributeFacade().createAttribute(PROTECTED_STATIC.build(),
            getMCTypeFacade().createQualifiedType(name+SUFFIX),
            "instance"))
        .build();

    CD4C cd4C = getCD4C();

    //listener attribute
    ASTCDAttribute accessListenersAttribute = cd4C.addAttribute(daoFactory,
                                                                false,
                                                                false,
                                                                "protected final List<Runnable> accessListeners;");
    getGlex().replaceTemplate("cd2java.Value",
                         accessListenersAttribute,
                         new StringHookPoint(" = new ArrayList<>()"));

    cdDefinition.getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
                .forEach(c -> addClassElements(c, daoFactory));

    // TODO remove this cancerous check for multiple associations with the same name introduced by CDMerge
    cdDefinition.getCDAssociationsList()
                .stream()
                .filter(a -> !a.getModifier().isDerived())
                .collect(Collectors.toMap(ASTCDAssociationTOP::getName, a -> a))
                .values()
                .forEach(assoc -> addAssocDAOElements(assoc, daoFactory));

    // methods
    cd4C.addMethod(daoFactory,
                   CUSTOM_TEMPLATE_PATH + "AddAccessListener",
                   daoFactory.getName(),
                   accessListenersAttribute.getName());
    cd4C.addMethod(daoFactory, CUSTOM_TEMPLATE_PATH + "StaticAddAccessListener");
    List<ASTCDClass> concreteDomainClasses = cdDefinition.getCDClassesList().stream()
                                                         .filter(c -> !c.getModifier().isAbstract() && !c.getModifier().isDerived())
                                                         .collect(Collectors.toList());

    List<String> associationNames = cdDefinition.getCDAssociationsList()
                                       .stream()
                                       .filter(a -> !a.getModifier().isDerived())
                                       .map(ASTCDAssociationTOP::getName)
                                       .collect(Collectors.toList());
    cd4C.addMethod(daoFactory,
                   CUSTOM_TEMPLATE_PATH + "Init",
                   name + DAOFactoryDecorator.SUFFIX,
                   concreteDomainClasses,
                   associationNames);
    cd4C.addMethod(daoFactory,
                   CUSTOM_TEMPLATE_PATH + "InitNeo4J",
                   name + DAOFactoryDecorator.SUFFIX,
                   concreteDomainClasses,
                   associationNames);

    return daoFactory;
  }

  protected void addAssocDAOElements(ASTCDAssociation association, ASTCDClass daoFactory) {
    // Associations
    String assocDAOType = "Assoc"+StringTransformations.capitalize(association.getName());

    daoFactory.addCDMember(getDaoAttribute(assocDAOType));
    getCD4C().addMethod(daoFactory, CUSTOM_TEMPLATE_PATH + "StaticGetDAOInstance", assocDAOType);
    getCD4C().addMethod(daoFactory, CUSTOM_TEMPLATE_PATH + "GetDAOInstance", assocDAOType);
  }

  protected void addClassElements(ASTCDClass domainClass,
                                  ASTCDClass daoFactory) {
    String name = domainClass.getName();
    if (!domainClass.getModifier().isAbstract()) {
      daoFactory.addCDMember(getDaoAttribute(domainClass));
      getCD4C().addMethod(daoFactory, CUSTOM_TEMPLATE_PATH + "StaticGetDAOInstance", name);
      getCD4C().addMethod(daoFactory, CUSTOM_TEMPLATE_PATH + "GetDAOInstance", name);
    }
  }

  protected ASTCDAttribute getDaoAttribute(ASTCDType domainClass) {
    String type = domainClass.getName() + "DAO";
    return getCDAttributeFacade().createAttribute(PROTECTED.build(),
        getMCTypeFacade().createQualifiedType(type+ DAOInterfaceDecorator.INTERFACE_SUFFIX),
        StringTransformations.uncapitalize(type));
  }

  protected ASTCDAttribute getDaoAttribute(String associationName) {
    String concreteType = associationName + "DAO";
    return getCDAttributeFacade().createAttribute(PROTECTED.build(),
        getMCTypeFacade().createQualifiedType(concreteType+ DAOInterfaceDecorator.INTERFACE_SUFFIX),
        StringTransformations.uncapitalize(concreteType));
  }
}
