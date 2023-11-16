package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PROTECTED_STATIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DAOFactoryDecorator extends AbstractDecorator {

  public static final String SUFFIX = "DAOFactory";

  protected List<String> addedAssocAttributeNames = new ArrayList<>();

  public DAOFactoryDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(withOrigin(createClass(origCD)));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass daoFactory = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(name + SUFFIX)
        .addCDMember(getCDAttributeFacade().createAttribute(PROTECTED_STATIC.build(),
            getMCTypeFacade().createQualifiedType(name+SUFFIX),
            StringTransformations.uncapitalize("instance")))
        .build();

    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
        .forEach(c -> addClassElements(c, daoFactory));

    // TODO remove this cancerous check for multiple associations with the same name introduced by CDMerge
    origCD.getCDDefinition().getCDAssociationsList().stream()
        .filter(a -> !a.getModifier().isDerived())
        .collect(Collectors.groupingBy(ASTCDAssociation::getName))
        .entrySet().stream().map(entry -> entry.getValue().get(0))
        .forEach(assoc -> addAssocDAOElements(assoc, daoFactory));

    // methods
    List<ASTCDClass> concreteDomainClasses = origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getModifier().isAbstract() && !c.getModifier().isDerived())
        .collect(Collectors.toList());

    getCD4C().addMethod(daoFactory, "umlp.daofactory.Init", name + DAOFactoryDecorator.SUFFIX,
        concreteDomainClasses, origCD.getCDDefinition().getCDAssociationsList().stream()
            .filter(a -> !a.getModifier().isDerived()).map(a -> a.getName()).collect(Collectors.toList()));
    getCD4C().addMethod(daoFactory, "umlp.daofactory.InitNeo4J", name + DAOFactoryDecorator.SUFFIX,
        concreteDomainClasses, origCD.getCDDefinition().getCDAssociationsList().stream()
            .filter(a -> !a.getModifier().isDerived()).map(a -> a.getName()).collect(Collectors.toList()));

    return daoFactory;
  }

  protected void addAssocDAOElements(ASTCDAssociation association, ASTCDClass daoFactory) {
    // Associations
    String assocDAOType = "Assoc"+StringTransformations.capitalize(association.getName());

    daoFactory.addCDMember(getDaoAttribute(assocDAOType));
    getCD4C().addMethod(daoFactory, "umlp.daofactory.StaticGetDAOInstance", assocDAOType);
    getCD4C().addMethod(daoFactory, "umlp.daofactory.GetDAOInstance", assocDAOType);
  }

  protected void addClassElements(ASTCDClass domainClass,
                                  ASTCDClass daoFactory) {
    String name = domainClass.getName();
    if (!domainClass.getModifier().isAbstract()) {
      daoFactory.addCDMember(getDaoAttribute(domainClass));
      getCD4C().addMethod(daoFactory, "umlp.daofactory.StaticGetDAOInstance", name);
      getCD4C().addMethod(daoFactory, "umlp.daofactory.GetDAOInstance", name);
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
