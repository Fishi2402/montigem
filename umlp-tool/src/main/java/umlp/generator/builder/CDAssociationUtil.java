package umlp.generator.builder;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import umlp.UMLPMill;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

// TODO: remove duplicate methods
public class CDAssociationUtil {

    private static boolean leftToRight = true;

    public static ASTMCObjectType getAssociationType(CDRoleSymbol symbol) {
        ASTMCObjectType type;
        String targetType = symbol.getType().print();

        if (isQualified(symbol) && !isOrdered(symbol)) {
            if (isOne(symbol) || isOptional(symbol)) {
                String valueType = isOptional(symbol) ? "Optional<" + targetType + ">" : targetType;
                type = getMCTypeFacade().createBasicGenericTypeOf("Map",
                        getMCTypeFacade().createBasicTypeArgumentOf(getQualifierType(symbol).get()),
                        getMCTypeFacade().createBasicTypeArgumentOf(valueType));
            } else {
                String valueType = isOptional(symbol) ? "Optional<" + targetType + ">" : targetType;
                type = getMCTypeFacade().createBasicGenericTypeOf("Multimap",
                        getMCTypeFacade().createBasicTypeArgumentOf(getQualifierType(symbol).get()),
                        getMCTypeFacade().createBasicTypeArgumentOf(valueType));
            }

        } else if (isQualified(symbol) && isOrdered(symbol)) {
            if (isOne(symbol) || isOptional(symbol)) {
                String valueType = isOptional(symbol) ? "Optional<" + targetType + ">" : targetType;
                type = getMCTypeFacade().createBasicGenericTypeOf("Map",
                        getMCTypeFacade().createBasicTypeArgumentOf(getQualifierType(symbol).get()),
                        getMCTypeFacade().createBasicTypeArgumentOf(valueType));
            } else {
                String valueType = isOptional(symbol) ? "Optional<" + targetType + ">" : targetType;
                type = getMCTypeFacade().createBasicGenericTypeOf("LinkedListMultimap",
                        getMCTypeFacade().createBasicTypeArgumentOf(getQualifierType(symbol).get()),
                        getMCTypeFacade().createBasicTypeArgumentOf(valueType));
            }

        } else if (isMultiple(symbol)) {
            type = getMCTypeFacade().createListTypeOf(targetType);

//            mCType = {ASTMCListType@3805}
//            names = {ArrayList@3814}  size = 1
//            typeArguments = {ArrayList@3815}  size = 1
//            mCTypeArgument = {ASTMCBasicTypeArgument@3816}
//            mCQualifiedType = {ASTMCQualifiedType@3823}
//            mCQualifiedName = {ASTMCQualifiedName@3830} "String"

        } else if (isOptional(symbol)) {
            type = getMCTypeFacade().createOptionalTypeOf(targetType);

        } else {
            type = getMCTypeFacade().createQualifiedType(targetType);
        }
        return type;
    }

    public static String getAssociationName(CDRoleSymbol symbol) {
        return leftToRight ? getAssociationNameInternal(symbol) :
                getAssociationNameInternal(symbol.getOtherSide());
    }

    private static String getAssociationNameInternal(CDRoleSymbol symbol) {
        StringBuilder name = new StringBuilder();
        if (!Strings.isNullOrEmpty(symbol.getName())) {
            name.append(symbol.getName());
        } else if (!Strings.isNullOrEmpty(symbol.getOtherSide().getName())) {
            name.append(symbol.getOtherSide().getName());
        } else if (symbol.isPresentAssoc()) {
            name.append(symbol.getAssoc().getAssociation().getName());
        } else {
            name.append(TransformationUtils.uncapitalize(symbol.getOtherSide().getType().getTypeInfo().getName()));
        }
        return name.toString();
    }

    public static List<CDRoleSymbol> getAllAssociations(ASTCDClass clazz) {
        List<CDRoleSymbol> localAssociations = getLocalAssociations(clazz);
        localAssociations.addAll(getInheritedAssociations(clazz));
        return localAssociations;
    }

    public static List<CDRoleSymbol> getInheritedAssociations(ASTCDClass clazz) {
        return getInheritedAssociations2(clazz).stream()/* TODO .filter(x -> !x.isDerived())*/.collect(Collectors.toList());
    }

    public static List<CDRoleSymbol> getInheritedAssociations2(ASTCDClass clazz) {
        List<CDRoleSymbol> localAssociations = getLocalAssociations(clazz);
        return getAllAssociationsForType(clazz).stream()
                .filter(association -> !localAssociations.contains(association)).collect(Collectors.toList());
    }

    public static List<CDRoleSymbol> getAllAssociationsForType(ASTCDClass clazz) {
        return getAllSuperTypes(clazz).stream().map(CDTypeSymbol::getCDRoleList)
                .flatMap(Collection::stream)
                .distinct().collect(Collectors.toList());
    }

    public static List<CDRoleSymbol> getLocalAssociations(ASTCDClass clazz) {
        List<CDRoleSymbol> cdRoleList = clazz.getSymbol().getCDRoleList();
        if (cdRoleList.isEmpty()) {
            return Lists.newArrayList();
        }
        return cdRoleList.stream().collect(Collectors.toList());
    }

    public static List<ASTCDAttribute> getInheritedVisibleAttributesInHierarchy(ASTCDClass clazz) {
        CDTypeSymbol cdType = clazz.getSymbol();
        List<ASTCDAttribute> visibleAttr = new ArrayList<>();
        if (cdType.isPresentSuperClass()) {
            List<ASTMCObjectType> superclassList = clazz.getSuperclassList();
            ASTCDExtendUsage cdExtendUsage = clazz.getCDExtendUsage();
            List<ASTMCObjectType> superclassList1 = cdExtendUsage.getSuperclassList();
            ASTCDClass superClass = (ASTCDClass) cdType.getSuperClass().getTypeInfo().getAstNode();
            visibleAttr.addAll(getVisibleNotDerivedAttributesInHierarchy(superClass));
        }
        return visibleAttr;
    }

    public static Optional<String> getQualifierType(CDRoleSymbol symbol) {
        if (symbol.isPresentTypeQualifier()) {
            TypeSymbol typeInfo = symbol.getTypeQualifier().getTypeInfo();
            return Optional.ofNullable(typeInfo.getName());
        }
        return Optional.empty();
    }

    public static boolean isMultiple(CDRoleSymbol symbol) {
        if (!symbol.isPresentCardinality()) return false;
        return symbol.getCardinality().isMult();
    }

    public static boolean isQualified(CDRoleSymbol symbol) {
        return symbol.isPresentTypeQualifier();
    }

    public static boolean isOppositeQualified(CDRoleSymbol symbol) {
        return symbol.getOtherSide().isPresentTypeQualifier();
    }

    public static boolean isOne(CDRoleSymbol symbol) {
        if (!symbol.isPresentCardinality()) return false;
        return symbol.getCardinality().isOne();
    }

    public static boolean isSourceOne(CDRoleSymbol symbol) {
        return isOne(symbol.getOtherSide());
    }

    public static boolean isSourceOptional(CDRoleSymbol symbol) {
        return isOptional(symbol.getOtherSide());
    }

    public static boolean isSourceMultiple(CDRoleSymbol symbol) {
        return isMultiple(symbol.getOtherSide());
    }

    public static boolean isOptional(CDRoleSymbol symbol) {
        if (!symbol.isPresentCardinality()) return false;
        return symbol.getCardinality().isOpt();
    }

    public static boolean isOrdered(CDRoleSymbol symbol) {
        return symbol.isIsOrdered();
    }

    public static List<ASTCDAttribute> getVisibleAttributes(ASTCDClass astcdClass) {
        List<ASTCDAttribute> collect = astcdClass.getCDAttributeList().stream().filter(field ->
                        !field.getModifier().isDerived() && !field.getModifier().isFinal()
                                && !field.getModifier().isStatic())
                .collect(Collectors.toList());
        return collect;
    }

    public static List<CDTypeSymbol> getAllSuperTypes(ASTCDClass clazz) {
        List<CDTypeSymbol> superTypes = getAllSuperClasses(clazz);
        superTypes.addAll(getAllSuperInterfaces(clazz));
        return superTypes;
    }

    public static List<CDTypeSymbol> getAllSuperClasses(ASTCDClass clazz) {
        checkArgument(clazz.isPresentSymbol());
        CDTypeSymbol cdTypeSymbol = clazz.getSymbol();
        return getSuperClassesRecursively(cdTypeSymbol, Lists.newArrayList());
    }

    public static List<CDTypeSymbol> getAllSuperInterfaces(ASTCDClass clazz) {
        checkArgument(clazz.isPresentSymbol());
        return getAllSuperClasses(clazz)
                .stream()
                .map(CDTypeSymbol::getInterfaceList)
                .flatMap(Collection::stream)
                .map(SymTypeExpression::getTypeInfo)
                .flatMap(superInterface -> getSuperInterfacesRecursively((CDTypeSymbol) superInterface, new ArrayList<>())
                        .stream())
                .collect(Collectors.toList());
    }

    private static List<CDTypeSymbol> getSuperClassesRecursively(CDTypeSymbol cdTypeSymbol, final List<CDTypeSymbol> symbols) {
        checkNotNull(symbols);
        symbols.add(cdTypeSymbol);
        if (cdTypeSymbol.isPresentSuperClass()) {
            SymTypeExpression superClass = cdTypeSymbol.getSuperClass();
            TypeSymbol typeInfo = superClass.getTypeInfo();
            getSuperClassesRecursively((CDTypeSymbol) typeInfo, symbols);
        }
        return symbols;
    }

    private static List<CDTypeSymbol> getSuperInterfacesRecursively(CDTypeSymbol cdTypeSymbol, final List<CDTypeSymbol> symbols) {
        checkNotNull(symbols);
        symbols.add(cdTypeSymbol);
        cdTypeSymbol.getInterfaceList().stream().map(SymTypeExpression::getTypeInfo).forEach(interfaceSymbol
                -> getSuperInterfacesRecursively((CDTypeSymbol) interfaceSymbol, symbols));
        return symbols;
    }

    public static List<CDRoleSymbol> getAllNonDerivedAssociationsForClass(ASTCDClass clazz) {
        List<CDRoleSymbol> superClassCDRoles = getAllSuperTypes(clazz).stream().map(CDTypeSymbol::getCDRoleList)
               .flatMap(Collection::stream).collect(Collectors.toList());
        return superClassCDRoles;
//    return getAllSuperTypes(clazz).stream().map(CDTypeSymbol::getCDRoleList).flatMap(Collection::stream);
//            .distinct()/* TODO .filter(x -> !x.isDerived()) */.collect(Collectors.toList());
    }

    public static List<ASTCDAttribute> getVisibleNotDerivedAttributesInHierarchy(ASTCDClass astcdClass) {
        List<ASTCDAttribute> visibleAttr = astcdClass.getCDAttributeList().stream().filter(field ->
                !field.getModifier().isDerived() && !field.getModifier().isFinal()
                        && !field.getModifier().isStatic()).collect(Collectors.toList());

        if (astcdClass.isPresentCDExtendUsage()) {
            // visibleAttr.addAll(getVisibleNotDerivedAttributesInHierarchy());
        }
        return visibleAttr;
    }

    private static MCTypeFacade getMCTypeFacade() {
        return MCTypeFacade.getInstance();
    }

    public static List<ASTCDAttribute> getInheritedAttributes(ASTCDClass clazz) {
        List<ASTCDAttribute> localAttributes = getVisibleAttributes(clazz);
        List<ASTCDAttribute> inheritedAttributes = getVisibleNotDerivedAttributesInHierarchy(clazz);
        inheritedAttributes.removeAll(localAttributes);
        return inheritedAttributes;
    }

    public static String getOppositeAssociationName(CDRoleSymbol symbol) {
        CDRoleSymbol s = symbol.getOtherSide();
        return getAssociationName(s);
    }

    public static ASTCDParameter associationToCDParameter(CDRoleSymbol symbol) {
        return UMLPMill.cDParameterBuilder().setName(getAssociationName(symbol))
                .setMCType(getAssociationType(symbol)).build();
    }
}
