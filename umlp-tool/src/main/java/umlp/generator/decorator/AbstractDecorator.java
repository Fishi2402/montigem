package umlp.generator.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.facade.*;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import umlp.generator.builder.TransformationUtils;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.builder.CDAssociationUtil.*;
import static umlp.generator.builder.CDAssociationUtil.associationToCDParameter;

// TODO refactor this class
public abstract class AbstractDecorator {

    protected static final ASTMCQualifiedName DEFAULT_PACKAGE = createQualifiedName("umlp");

    public final static String GET_PREFIX = "get";

    public final static String GET_PREFIX_BOOLEAN = "is";

    public final static String SET_PREFIX = "set";

    protected final GlobalExtensionManagement glex;

    public AbstractDecorator(GlobalExtensionManagement glex) {
        this.glex = glex;
    }

    protected GlobalExtensionManagement getGlex() {
        return glex;
    }

    private MCTypeFacade getMCTypeFacade() {
        return MCTypeFacade.getInstance();
    }

    protected CDAttributeFacade getCDAttributeFacade() {
        return CDAttributeFacade.getInstance();
    }

    protected CDConstructorFacade getCDConstructorFacade() {
        return CDConstructorFacade.getInstance();
    }

    protected CDMethodFacade getCDMethodFacade() {
        return CDMethodFacade.getInstance();
    }

    protected CDParameterFacade getCDParameterFacade() {
        return CDParameterFacade.getInstance();
    }

    protected CDExtendUsageFacade getExtendUsageFacade() {
        return CDExtendUsageFacade.getInstance();
    }

    protected static ASTMCQualifiedName createQualifiedName(String... parts) {
        return MCBasicTypesMill.mCQualifiedNameBuilder().setPartsList(Lists.newArrayList(parts)).build();
    }

    protected ASTCDMethod createGetterMethod(ASTCDAttribute attribute) {
        ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), attribute.getMCType(), getPlainGetter(attribute));
        getGlex().replaceTemplate(CD2JavaTemplates.EMPTY_BODY, method,
                new StringHookPoint("return this." + attribute.getName() + ";"));
        return method;
    }

    protected ASTCDMethod createSetterMethod(ASTCDAttribute attribute) {
        ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getPlainSetter(attribute),
                getCDParameterFacade().createParameter(attribute.getMCType(), attribute.getName()));
        getGlex().replaceTemplate(CD2JavaTemplates.EMPTY_BODY, method,
                new StringHookPoint("this." + attribute.getName() + " = " + attribute.getName() + ";"));
        return method;
    }

    private static final String getPlainGetter(ASTCDAttribute attribute) {
        return TransformationUtils.makeCamelCase(getGetNamePrefix(attribute), getNameSuffix(attribute));
    }

    private static final String getPlainSetter(ASTCDAttribute attribute) {
        return TransformationUtils.makeCamelCase(SET_PREFIX, getNameSuffix(attribute));
    }

    private static final String getNameSuffix(ASTCDAttribute attribute) {
        if (attribute.printType().equals("boolean")) {
            return attribute.getName().replace("is", "").replace("IS", "")
                    .replace("Is", "").replace("iS", "");
        }
        return attribute.getName();
    }

    private static final String getGetNamePrefix(ASTCDAttribute attribute) {
        if (attribute.printType().equals("boolean")) {
            return GET_PREFIX_BOOLEAN;
        }
        else {
            return GET_PREFIX;
        }
    }

    public List<ASTCDParameter> getConstructorParameters(ASTCDClass clazz, boolean withLocalAssociations) {
        List<ASTCDParameter> paramList = new ArrayList<>();
        List<ASTCDAttribute> localAttributes = getVisibleAttributes(clazz);
        List<ASTCDAttribute> superAttributes = getInheritedVisibleAttributesInHierarchy(clazz);
        List<CDRoleSymbol> superAssociations = getInheritedAssociations(clazz);

        localAttributes.stream().map(astcdAttribute -> getCDParameterFacade()
                .createParameter(astcdAttribute.getMCType(), astcdAttribute.getName())).forEach(paramList::add);
        superAttributes.stream().map(astcdAttribute -> getCDParameterFacade()
                .createParameter(astcdAttribute.getMCType(), astcdAttribute.getName())).forEach(paramList::add);
        superAssociations.stream().map(associationSymbol -> associationToCDParameter(associationSymbol)).forEach(paramList::add);
        if (withLocalAssociations) {
            List<CDRoleSymbol> localAssociations = getLocalAssociations(clazz);
            localAssociations.stream().map(associationSymbol -> associationToCDParameter(associationSymbol)).forEach(paramList::add);
        }
        return paramList;
    }
}
