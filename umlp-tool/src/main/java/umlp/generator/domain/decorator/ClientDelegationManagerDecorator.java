package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._ast.ASTCDTypeTOP;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ClientDelegationManagerDecorator extends ManagerDecorator {

  public static final String CLIENT_IMPL_SUFFIX = "ClientImpl";

  protected String origCDName;

  public ClientDelegationManagerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    origCDName = origCD.getCDDefinition().getName();
    createClasses(origCD, decoratedCD);
  }

  protected void createClasses(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {

    var extensionDiamond = UMLPSymbolTables.getExtensionDiamond(origCD.getCDDefinition().getSymbol());
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

    String delegationManagerName = parentCD.getName() +"4"+this.origCDName+ SUFFIX+CLIENT_IMPL_SUFFIX;

    ASTCDClass delegationManagerClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(delegationManagerName)
        .setCDExtendUsage(getExtendUsageFacade()
            .createCDExtendUsage(parentCD.getName()+ManagerDecorator.SUFFIX+CLIENT_IMPL_SUFFIX))
        .build();

    addManagerInit(delegationManagerClass, parentCD.getName(), delegationManagerName);

    LinkedHashMap<ASTCDType, LinkedHashSet<ASTCDType>> superTypesToTypes =
        getSuperToTypes(parentCD, extendingCDs);

    addDelegationGetters(delegationManagerClass, superTypesToTypes);

    addImportsForDelegationManager(delegationManagerClass);
    addImportsForGivenTypes(delegationManagerClass, superTypesToTypes.values().stream()
        .flatMap(Collection::stream).collect(Collectors.toSet()));

    addImportsForGivenTypes(delegationManagerClass, superTypesToTypes.keySet());
    getCD4C().addImport(delegationManagerClass, Joiners.DOT.join(parentCD.getName().toLowerCase(),
        parentCD.getName()+SUFFIX));
    getCD4C().addImport(delegationManagerClass, Joiners.DOT.join(parentCD.getName().toLowerCase(),
        parentCD.getName()+SUFFIX+CLIENT_IMPL_SUFFIX));

    extendingCDs.forEach(extendingCD -> {
      String importingManager = getPackageName(extendingCD, "").isEmpty()?
          extendingCD.getName()+SUFFIX :
          Joiners.DOT.join(getPackageName(extendingCD, ""), extendingCD.getName()+SUFFIX );
      getCD4C().addImport(delegationManagerClass, importingManager);
    });


    return delegationManagerClass;
  }

  protected void addManagerInit(ASTCDClass delegationManagerClass, String extendedCDName, String delegationManagerName) {
    getCD4C().addMethod(delegationManagerClass, getInitTemplate(), extendedCDName, delegationManagerName);
  }

  protected void addDelegationGetters(ASTCDClass delegationManagerClass,
                                      Map<ASTCDType, LinkedHashSet<ASTCDType>> superTypeToTypes) {
    for (Map.Entry<ASTCDType, LinkedHashSet<ASTCDType>> superTypeToExtensions :
        superTypeToTypes.entrySet()){
      LinkedHashMap<String, String> cdTypeToCorrectManager = new LinkedHashMap<>();
      superTypeToExtensions.getValue().forEach(cdClass -> cdTypeToCorrectManager
          .put(cdClass.getName(), getCorrectManager(cdClass)));
      getCD4C().addMethod(delegationManagerClass, getDelegatedOptTemplate(),
          superTypeToExtensions.getKey().getName(),
          superTypeToExtensions.getValue().stream().map(ASTCDTypeTOP::getName).collect(Collectors.toList()),
          cdTypeToCorrectManager);
      getCD4C().addMethod(delegationManagerClass, getDelegatedGetListTemplate(),
          superTypeToExtensions.getKey().getName(),
          superTypeToExtensions.getValue().stream().map(ASTCDTypeTOP::getName).collect(Collectors.toList()),
          cdTypeToCorrectManager);
    }
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

  protected void addImportsForGivenTypes(ASTCDType clazz, Collection<ASTCDType> types) {
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

  protected String getInitTemplate() {
    return "umlp.gui2ts.managerclientimpl.TSInit";
  }

  protected String getDelegatedOptTemplate() {
    return "umlp.gui2ts.delegationmanager.DelegatedOpt";
  }

  protected String getDelegatedGetListTemplate() {
    return "umlp.gui2ts.delegationmanager.DelegatedGetList";
  }

}
