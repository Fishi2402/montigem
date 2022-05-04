package umlp.generator.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.builder.CDAssociationUtil.getAssociationName;
import static umlp.generator.builder.CDAssociationUtil.getAssociationType;

public class DomainClassDecorator extends AbstractDecorator {

    public static final String UMLP_ID_NAME = "umlpId";

    public DomainClassDecorator(GlobalExtensionManagement glex) {
        super(glex);
    }

    public final void transform(ASTCDCompilationUnit topCD) {
        checkNotNull(topCD);
        checkNotNull(topCD.getCDDefinition());

        ASTMCQualifiedName mcQualifiedName = topCD.getMCPackageDeclaration().getMCQualifiedName(); // TODO check case without package declaration
        List<ASTCDClass> cdClassesList = topCD.getCDDefinition().getCDClassesList();
        for (ASTCDClass clazz : cdClassesList) {
            if (!clazz.isPresentCDExtendUsage()) {
                ASTCDAttribute umlpIdAttribute = createUMLPIdAttribute();
                clazz.addCDMember(umlpIdAttribute);
                clazz.addAllCDMembers(createAccessMethodsForAssociations(Lists.newArrayList(umlpIdAttribute)));
            }

            Collection<ASTCDAttribute> attributesForAssociations =
                    createAttributesForAssociations(clazz.getSymbol().getCDRoleList(), clazz, mcQualifiedName);
            // TODO bug: clazz.getCDRoleList() gives empty list, although clazz.getSymbol().getCDRoleList() does not
            clazz.addAllCDMembers(attributesForAssociations);
            clazz.addAllCDMembers(createAccessMethodsForAssociations(attributesForAssociations));
            addImports(clazz, attributesForAssociations);
            clazz.addCDMember(createNoArgumentConstructor(clazz));
            clazz.addCDMember(createAllArgumentsConstructor(clazz));
            // clazz.setCDExtendUsage(addExtendObservable());
        }
    }

    private ASTCDConstructor createNoArgumentConstructor(ASTCDClass clazz) {
        return getCDConstructorFacade().createConstructor(PUBLIC.build(), clazz.getName());
    }

    private ASTCDConstructor createAllArgumentsConstructor(ASTCDClass clazz) {
        List<ASTCDParameter> constructorParameters = getConstructorParameters(clazz, false);
        return getCDConstructorFacade().createConstructor(PUBLIC.build(), clazz.getName(),constructorParameters);
    }

    private Collection<ASTCDAttribute> createAttributesForAssociations(List<CDRoleSymbol> cdRoles, ASTCDClass clazz, ASTMCQualifiedName rootPackage) {
        List<ASTCDAttribute> attributes = Lists.newArrayList();
        for (CDRoleSymbol cdRole : cdRoles) {
            CDTypeSymbol typeInfo = (CDTypeSymbol) cdRole.getType().getTypeInfo();
            String packageName = typeInfo.getPackageName();
            ASTCDClass astNode = (ASTCDClass) typeInfo.getAstNode();
            attributes.add(getCDAttributeFacade().createAttribute(PROTECTED.build(), getAssociationType(cdRole),
                    getAssociationName(cdRole)));
            addImport(clazz, typeInfo.getName(), rootPackage);
        }
        return attributes;
    }

    private Collection<ASTCDMethod> createAccessMethodsForAssociations(Collection<ASTCDAttribute> attributes) {
        List<ASTCDMethod> methods = Lists.newArrayList();
        for (ASTCDAttribute attribute : attributes) {
            methods.add(createGetterMethod(attribute));
            methods.add(createSetterMethod(attribute));
        }
        return methods;
    }

    private ASTCDAttribute createUMLPIdAttribute() {
        return getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
                "Long", UMLP_ID_NAME);
    }

    private void addImports(ASTCDClass clazz, Collection<ASTCDAttribute> attributes) {
        CD4C instance = CD4C.getInstance();
        for (ASTCDAttribute attribute : attributes) {

        }
        // instance.addImport(clazz, "java.util.List");
        // getGlex().replaceTemplate("cd2java.Imports", clazz, new StringHookPoint(""));
    }

    private void addImport(ASTCDClass clazz, String name, ASTMCQualifiedName rootPackage) {
        CD4C instance = CD4C.getInstance();
        String qName = rootPackage.getQName();
        instance.addImport(clazz, qName.concat(".").concat(name));
    }

    private ASTCDExtendUsage addExtendObservable() {
        return getExtendUsageFacade().createCDExtendUsage("java.util.Observable");
    }
}
