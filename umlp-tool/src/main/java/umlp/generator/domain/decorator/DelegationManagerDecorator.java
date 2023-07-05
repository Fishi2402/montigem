package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._ast.ASTCDTypeTOP;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp._symboltable.UMLPArtifactScope;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PRIVATE;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DelegationManagerDecorator extends ManagerDecorator {


  public DelegationManagerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    createClasses(origCD, decoratedCD);
  }

  protected void createClasses(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {

    if(!origCD.getCDDefinition().getModifier().isPresentStereotype()) {
      return;
    }
    List<ASTStereoValue> valueList = origCD.getCDDefinition().getModifier().getStereotype().getValuesList();

    // This checks a CD 'extends' another one, i.e. a UMLP Project extends/ is composed of another one
    valueList.forEach(value -> {
      if(!value.getName().equals("extends")) {
        return;
      }
      String extendedCDName = value.getValue();

      Optional<ASTMCImportStatement> importStatement =
          origCD.getMCImportStatementList().stream().filter(stmt -> (
              // Assume star import or direct CD import
              stmt.getMCQualifiedName().getPartsList().get(stmt.getMCQualifiedName().getPartsList().size()-1).equals(extendedCDName))
          ).findFirst();

      List<String> parts = new ArrayList<>();
      if(importStatement.isPresent()) {
       parts = importStatement.get().getMCQualifiedName().getPartsList();
      }

      // TODO recursively look into extended CDs, how? -> work with CDPackageSymbol?
      Optional<DiagramSymbol> extendedCDSymbol;
      Optional<String> packageName;
      if(parts.size()>=3) {
        packageName =  Optional.of(Joiners.DOT.join(parts.subList(0, parts.size()-3)));
        extendedCDSymbol
            = CD4AnalysisMill.globalScope().resolveDiagram(Joiners.DOT.join(parts.subList(0, parts.size()-2)));
      } else {
        packageName =  Optional.empty();
        extendedCDSymbol
            = CD4AnalysisMill.globalScope().resolveDiagram(extendedCDName);
      }

      if(extendedCDSymbol.isEmpty()) {
        Log.error("Could not resolve diagram symbol for super class diagram: '"+ extendedCDName +"'.");
      }
      String packageNameWithCD = packageName.map(s -> Joiners.DOT.join(s, extendedCDName)).orElseGet(() -> extendedCDName);
      ASTCDPackage astcdPackage = getCorrectPackage(packageNameWithCD.toLowerCase(), decoratedCD);
      astcdPackage.addCDElement(createManagerClass((UMLPArtifactScope) extendedCDSymbol.get().getEnclosingScope(), extendedCDName, origCD));
    });
  }

  protected ASTCDClass createManagerClass(UMLPArtifactScope extendedCDScope, String extendedCDName, ASTCDCompilationUnit origCD) {

    String delegationManagerName = extendedCDName +"4"+origCD.getCDDefinition().getName()+ SUFFIX+"Impl";

    ASTCDClass delegationManagerClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(delegationManagerName)
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(extendedCDName+ManagerDecorator.SUFFIX+"Impl"))
        .build();
    addOriginalManagerAttribute(delegationManagerClass, extendedCDName);

    String extendingManagerName = origCD.getCDDefinition().getName()+SUFFIX;

    addManagerInit(delegationManagerClass, extendedCDName, delegationManagerName);

    Map<ASTCDType, Set<ASTCDType>> typesToSuperTypesFromExtendedCD = getEverySuperTypesFromExtendedCDToExtensions(extendedCDScope, origCD);

    addDelegationGetters(delegationManagerClass, extendingManagerName,  extendedCDName+ManagerDecorator.SUFFIX+"Impl", typesToSuperTypesFromExtendedCD);

    addImportsForDelegationManager(delegationManagerClass);
    addImportsFromCDForDelegationManager(delegationManagerClass, typesToSuperTypesFromExtendedCD.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));

    String importingManager = Joiners.DOT.join(getPackageName(origCD, ""),origCD.getCDDefinition().getName()+SUFFIX);
    getCD4C().addImport(delegationManagerClass, importingManager);

    return delegationManagerClass;
  }

  protected void addManagerInit(ASTCDClass delegationManagerClass, String extendedCDName, String delegationManagerName) {
    getCD4C().addMethod(delegationManagerClass, "umlp.managerimpl.Init", extendedCDName, delegationManagerName);
  }

  protected void addDelegationGetters(ASTCDClass delegationManagerClass, String extendingManagerName, String delegatedManagerName,
                                      Map<ASTCDType, Set<ASTCDType>> typesToSuperTypesFromExtendedCD) {
    for (Map.Entry<ASTCDType, Set<ASTCDType>> superTypeToExtensions :
        typesToSuperTypesFromExtendedCD.entrySet()){
      getCD4C().addMethod(delegationManagerClass, "umlp.delegationmanager.DelegatedOpt",
          superTypeToExtensions.getKey().getName(), superTypeToExtensions.getValue().stream().map(
              ASTCDTypeTOP::getName).collect(Collectors.toList()), extendingManagerName, delegatedManagerName);
      getCD4C().addMethod(delegationManagerClass, "umlp.delegationmanager.DelegatedGetList",
          superTypeToExtensions.getKey().getName(), superTypeToExtensions.getValue().stream().map(
              ASTCDTypeTOP::getName).collect(Collectors.toList()), extendingManagerName, delegatedManagerName);
    }
  }

  protected void addOriginalManagerAttribute(ASTCDClass delegationManagerClass, String extendedCDName) {
    String delegatedManager = extendedCDName+ManagerDecorator.SUFFIX+"Impl";

    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(PRIVATE.build(),
        getMCTypeFacade().createQualifiedType(delegatedManager), StringTransformations.uncapitalize(delegatedManager));

    getGlex().replaceTemplate("cd2java.Value", attr, new StringHookPoint(" = new " + delegatedManager + "()"));
    delegationManagerClass.addCDMember(attr);
  }

  protected Map<ASTCDType, Set<ASTCDType>> getEverySuperTypesFromExtendedCDToExtensions(UMLPArtifactScope extendedCDScope, ASTCDCompilationUnit origCD) {

    Map<ASTCDType, Set<ASTCDType>> typesToSuperTypeFromExtendedCD = new HashMap<>();

    origCD.getCDDefinition().getCDClassesList().forEach(astcdClass -> {
      CDSymbolTables.getTransitiveSuperTypes(astcdClass).forEach(superType -> {
        if(typeIsInExtendedCD(extendedCDScope, superType)) {
          if(typesToSuperTypeFromExtendedCD.containsKey(superType)) {
            typesToSuperTypeFromExtendedCD.get(superType).add(astcdClass);
          } else {
            HashSet<ASTCDType> set = new HashSet<>();
            set.add(astcdClass);
            typesToSuperTypeFromExtendedCD.put(superType, set);
          }
        }
      });
    });

    // TODO @CBu @AHe find out if we need interfaces
    /*origCD.getCDDefinition().getCDInterfacesList().forEach(astcdInterface -> {
      CDSymbolTables.getTransitiveSuperTypes(astcdInterface).forEach(superType -> {
        if(typeIsInExtendedCD(extendedCD, superType)) {
          typesToSuperTypeFromExtendedCD.put(astcdInterface, superType);
        }
      });
    });*/

    return typesToSuperTypeFromExtendedCD;
  }

  protected boolean typeIsInExtendedCD(UMLPArtifactScope extendedCDScope, ASTCDType type) {
    // Check if the typesymbol exists in this scope, if not assume the super type is not in this extended CD
    return extendedCDScope.resolveCDTypeDown(type.getName()).isPresent();
  }

  protected void addImportsForDelegationManager(ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemList"));
  }

  protected void addImportsFromCDForDelegationManager(ASTCDType clazz, Collection<ASTCDType> types) {
    for (ASTCDType type : types) {
      getCD4C().addImport(clazz, Joiners.DOT.join(type.getSymbol().getPackageName().toLowerCase(), type.getSymbol().getName()));
    }
  }

  protected ASTCDPackage getCorrectPackage(String packageName, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage p = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(getMCTypeFacade().createQualifiedName(packageName)).build();
    decoratedCD.getCDDefinition().addCDElement(p);
    return p;
  }

}
