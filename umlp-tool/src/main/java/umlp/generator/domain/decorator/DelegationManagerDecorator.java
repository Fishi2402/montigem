package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._ast.ASTCDTypeTOP;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import jdk.jshell.Diag;
import umlp.UMLPMill;
import umlp._symboltable.UMLPArtifactScope;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.cd.facade.CDModifier.PRIVATE;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DelegationManagerDecorator extends ManagerDecorator {

  protected String origCDName;

  public DelegationManagerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    origCDName = origCD.getCDDefinition().getName();
    createClasses(origCD, decoratedCD);
  }

  protected void createClasses(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {

    LinkedHashMap<DiagramSymbol, LinkedHashSet<DiagramSymbol>> extensionDiamond = new LinkedHashMap<>();
    getExtensionDiamond(origCD.getCDDefinition().getSymbol(), extensionDiamond);

    LinkedHashMap<DiagramSymbol, LinkedHashSet<DiagramSymbol>> parentToChildDiamond = new LinkedHashMap<>();
    extensionDiamond.values().stream().flatMap(Collection::stream).distinct().collect(Collectors.toList())
        .forEach(symbol -> parentToChildDiamond.put(symbol, new LinkedHashSet<>(
            extensionDiamond.keySet().stream().filter(possibleChild ->
                extensionDiamond.get(possibleChild).contains(symbol)).collect(Collectors.toList()
        ))));


    parentToChildDiamond.keySet().forEach(diagramSymbol -> {

      String extendedCDName = diagramSymbol.getName();
      Optional<String> packageName = diagramSymbol.getPackageName().equals("")? Optional.empty() :
          Optional.ofNullable(diagramSymbol.getPackageName());

      String packageNameWithCD = packageName.map(s -> Joiners.DOT.join(s, extendedCDName))
          .orElseGet(() -> extendedCDName);
      ASTCDPackage astcdPackage = getCorrectPackage(packageNameWithCD.toLowerCase(), decoratedCD);
      astcdPackage.addCDElement(withOrigin(createManagerClasses(diagramSymbol, parentToChildDiamond.get(diagramSymbol))));
    });
  }

  protected ASTCDClass createManagerClasses(DiagramSymbol parentCD, LinkedHashSet<DiagramSymbol> extendingCDs) {

    String delegationManagerName = parentCD.getName() +"4"+this.origCDName+ SUFFIX+"Impl";

    ASTCDClass delegationManagerClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(delegationManagerName)
        .setCDExtendUsage(getExtendUsageFacade()
            .createCDExtendUsage(parentCD.getName()+ManagerDecorator.SUFFIX+"Impl"))
        .build();

    addManagerInit(delegationManagerClass, parentCD.getName(), delegationManagerName);
    addManagerInitNeo4J(delegationManagerClass, parentCD.getName(), delegationManagerName);

    LinkedHashMap<ASTCDType, LinkedHashSet<ASTCDType>> superTypesToTypes =
        getSuperToTypes(parentCD, extendingCDs);

    addDelegationGetters(delegationManagerClass, superTypesToTypes);

    addImportsForDelegationManager(delegationManagerClass);
    addImportsFromCDForDelegationManager(delegationManagerClass, superTypesToTypes.values().stream()
        .flatMap(Collection::stream).collect(Collectors.toSet()));

    extendingCDs.forEach(extendingCD -> {
      String importingManager = getPackageName(extendingCD, "").isEmpty()?
          extendingCD.getName()+SUFFIX :
          Joiners.DOT.join(getPackageName(extendingCD, ""), extendingCD.getName()+SUFFIX );
      getCD4C().addImport(delegationManagerClass, importingManager);
    });


    return delegationManagerClass;
  }

  protected void addManagerInit(ASTCDClass delegationManagerClass, String extendedCDName, String delegationManagerName) {
    getCD4C().addMethod(delegationManagerClass, "umlp.managerimpl.Init", extendedCDName, delegationManagerName);
  }

  protected void addManagerInitNeo4J(ASTCDClass delegationManagerClass, String extendedCDName, String delegationManagerName) {
    getCD4C().addMethod(delegationManagerClass, "umlp.managerimpl.InitNeo4J", extendedCDName, delegationManagerName);
  }

  protected void addDelegationGetters(ASTCDClass delegationManagerClass,
                                      Map<ASTCDType, LinkedHashSet<ASTCDType>> superTypeToTypes) {
    for (Map.Entry<ASTCDType, LinkedHashSet<ASTCDType>> superTypeToExtensions :
        superTypeToTypes.entrySet()){
      LinkedHashMap<String, String> cdTypeToCorrectManager = new LinkedHashMap<>();
      superTypeToExtensions.getValue().forEach(cdClass -> cdTypeToCorrectManager
          .put(cdClass.getName(), getCorrectManager(cdClass)));
      getCD4C().addMethod(delegationManagerClass, "umlp.delegationmanager.DelegatedOpt",
          superTypeToExtensions.getKey().getName(),
          superTypeToExtensions.getValue().stream().map(ASTCDTypeTOP::getName).collect(Collectors.toList()),
          cdTypeToCorrectManager);
      getCD4C().addMethod(delegationManagerClass, "umlp.delegationmanager.DelegatedGetList",
          superTypeToExtensions.getKey().getName(),
          superTypeToExtensions.getValue().stream().map(ASTCDTypeTOP::getName).collect(Collectors.toList()),
          cdTypeToCorrectManager);
    }
  }

  protected void getExtensionDiamond(DiagramSymbol currentDiagram, Map<DiagramSymbol, LinkedHashSet<DiagramSymbol>> extensionDiamond) {
    if(extensionDiamond.containsKey(currentDiagram)) {
      return;
    }
    ASTCDDefinition currentCdDefinition = (ASTCDDefinition) currentDiagram.getAstNode();

    LinkedHashSet<DiagramSymbol> currentExtensions = getExtendedCDs(currentCdDefinition);
    extensionDiamond.put(currentDiagram, currentExtensions);
    currentExtensions.forEach(diagramSymbol -> getExtensionDiamond(diagramSymbol, extensionDiamond));
  }

  protected LinkedHashSet<DiagramSymbol> getExtendedCDs(ASTCDDefinition astcdDefinition) {
    LinkedHashSet<DiagramSymbol> extendedCDs = new LinkedHashSet<>();
    if(!astcdDefinition.getModifier().isPresentStereotype()) {
      return extendedCDs;
    }
    astcdDefinition.getModifier().getStereotype().getValuesList().forEach(value -> {
      if (!value.getName().equals("extends")) {
        return;
      }
      String extendedCDName = value.getValue();
      Optional<DiagramSymbol> extendedCDSymbol = astcdDefinition.getSymbol().getEnclosingScope()
          .resolveDiagram(extendedCDName);
      if(extendedCDSymbol.isPresent()) {
        extendedCDs.add(extendedCDSymbol.get());
      } else {
        Log.error("0xABF643: Can not resolve extended diagram "+extendedCDName + " in "
            +astcdDefinition.getName());
      }
    });
    return extendedCDs;
  }

  protected LinkedHashMap<ASTCDType, LinkedHashSet<ASTCDType>> getSuperToTypes(DiagramSymbol superCD,
                  LinkedHashSet<DiagramSymbol> extendingCDs) {

    // TODO @CBu @AHe find out if we need interfaces -> probably not
    LinkedHashMap<ASTCDType, LinkedHashSet<ASTCDType>> superTypesToTypes = new LinkedHashMap<>();

    extendingCDs.stream().map(symbol -> (ASTCDDefinition)symbol.getAstNode()).forEach(cdDef ->
        cdDef.getCDClassesList().forEach(astCdClass -> {
          CDSymbolTables.getTransitiveSuperTypes(astCdClass).forEach(superType -> {
            if(superTypesToTypes.containsKey(superType)) {
              superTypesToTypes.get(superType).add(astCdClass);
            } else if(typeIsInExtendedCD(superCD, superType)) {
              superTypesToTypes.put(superType, new LinkedHashSet<>(List.of(astCdClass)));
            }
          });
        }));

    return superTypesToTypes;
  }

  protected boolean typeIsInExtendedCD(DiagramSymbol cd, ASTCDType type) {
    // Check if the typesymbol exists in this scope, if not assume the super type is not in this extended CD
    return ((ASTCDDefinition) cd.getAstNode()).getEnclosingScope()
        .resolveCDTypeDown(type.getName()).isPresent();
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
