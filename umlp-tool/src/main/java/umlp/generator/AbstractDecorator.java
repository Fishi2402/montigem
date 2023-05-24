/* (c) https://github.com/MontiCore/monticore */
package umlp.generator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDInterfaceUsageFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd.facade.CDStereotypeFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import umlp.generator.domain.decorator.GenService;

import java.util.Arrays;
import java.util.Optional;

// TODO refactor this class
public abstract class AbstractDecorator {

  public AbstractDecorator(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  public static final String DEFAULT_PACKAGE = "";

  public static final String COMMAND_PACKAGE = DEFAULT_PACKAGE+"commands";

  public static final String GEMLIST_PACKAGE = DEFAULT_PACKAGE+"gemlists";

  public static final String REQUESTS_PACKAGE = DEFAULT_PACKAGE+"requests";

  public static final String CLIENT_PACKAGE = DEFAULT_PACKAGE+"client";

  public static final String SHELLAPI_PACKAGE = DEFAULT_PACKAGE+"shellapi";

  protected final GlobalExtensionManagement glex;

  protected final CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter(), false);

  protected TypeCheck typeCheck;

  protected GlobalExtensionManagement getGlex() {
    return glex;
  }

  protected MCTypeFacade getMCTypeFacade() {
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

  protected CDInterfaceUsageFacade getInterfaceUsageFacade() {
    return CDInterfaceUsageFacade.getInstance();
  }

  protected CDStereotypeFacade getStereotypeFacade() {
    return CDStereotypeFacade.getInstance();
  }

  protected CD4C getCD4C() {
    return CD4C.getInstance();
  }

  protected CD4CodeFullPrettyPrinter getCDPrinter (){
    return printer;
  }

  public TypeCheck getTypeCheck() {
    return typeCheck;
  }

  public void setTypeCheck(TypeCheck typeCheck) {
    this.typeCheck = typeCheck;
  }

  protected ASTCDPackage getPackage(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD, String packageName) {
    String completeName = getPackageName(origCD, packageName);
    Optional<ASTCDPackage> astCDPackage = decoratedCD.getCDDefinition().getCDPackagesList().stream()
            .filter(p -> p.getName().equals(completeName)).findFirst();
    if (astCDPackage.isPresent()) {
      return astCDPackage.get();
    } else {
      ASTCDPackage p = CD4CodeMill.cDPackageBuilder()
              .setMCQualifiedName(getMCTypeFacade().createQualifiedName(completeName)).build();
      decoratedCD.getCDDefinition().addCDElement(p);
      return p;
    }
  }

  protected String getPackageName(ASTCDCompilationUnit origCD, String subPackage) {
    String origPackage = Names.constructQualifiedName(origCD.getCDPackageList(), origCD.getCDDefinition().getName());
    return (subPackage.isEmpty()?origPackage:Joiners.DOT.join(origPackage, subPackage)).toLowerCase();
  }

  protected Optional<String> getPackageFromFQNString(String fQN) {
    String[] subNames = fQN.split("\\.");
    if(subNames.length>1) {
      String[] packageNamesOnly = Arrays.copyOf(subNames, subNames.length - 1);
      return Optional.of(Joiners.DOT.join(packageNamesOnly));
    } else {
      return Optional.empty();
    }
  }

  protected String getTypeFromFQNString(String fQN) {
    String[] subNames = fQN.split("\\.");
    return subNames[subNames.length-1];
  }

  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    domainType.getCDAttributeList().forEach(a -> {
      if (GenService.isJavaType(a.getSymbol().getType())) {
        getCD4C().addImport(cdType, a.getSymbol().getType().printFullName());
      } else if (a.getSymbol().getType().isGenericType()) {
        if (GenService.isJavaType(((SymTypeOfGenerics) a.getSymbol().getType()).getArgument(0))) {
          String imp = ((SymTypeOfGenerics) a.getSymbol().getType()).getArgument(0).printFullName();
          getCD4C().addImport(cdType, imp);
        }
      }
    });
    CDSymbolTables.getAssociationsInHierarchy(domainType).forEach(assocSide -> {
      getPackageFromFQNString(assocSide.getSymbol().getType().printFullName())
          .ifPresent(cdPackage ->
          getCD4C().addImport(cdType, Joiners.DOT.join(cdPackage.toLowerCase(),
              getTypeFromFQNString(assocSide.getSymbol().getType().printFullName()))));
    });
    domainType.getInterfaceList().forEach(cdInterface ->
        cdInterface.getDefiningSymbol().ifPresent(definingSymbol ->
            getCD4C().addImport(cdType, Joiners.DOT.join(definingSymbol.getPackageName().toLowerCase(),
                definingSymbol.getName()))));
    domainType.getSuperclassList().forEach(cdExtension ->
        cdExtension.getDefiningSymbol().ifPresent(definingSymbol ->
            getCD4C().addImport(cdType, Joiners.DOT.join(definingSymbol.getPackageName().toLowerCase(),
                definingSymbol.getName()))));
  }

  abstract public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD);

}
