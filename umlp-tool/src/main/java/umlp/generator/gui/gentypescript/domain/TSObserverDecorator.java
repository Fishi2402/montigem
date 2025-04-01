/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.Joiners;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.util.UMLPSymbolTables;

import java.nio.file.Path;
import java.util.Optional;

public class TSObserverDecorator extends ObserverDecorator {

  protected String astcdPackage;

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSObserverDecorator(final GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    super(glex);
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    this.astcdPackage = astcdPackage.getName();
    super.transform(origCD, decoratedCD);
  }

  @Override
  protected void addImport(ASTCDType observerType, ASTCDAttribute attr) {
    if (attr.isPresentSymbol()) {
      Optional<SymTypeExpression> ste = getImportedType(attr.getSymbol().getType());
      if (ste.isPresent()) {
        TSImportHelper importHelper = new TSImportHelper(glex, observerType, hwPath, outPath);
        String importerQName = this.astcdPackage + "." + observerType.getName();

        importHelper.addImport(Joiners.DOT.join(astcdPackage, ste.get().print()), importerQName);
      }
    }
  }

  protected Optional<SymTypeExpression> getImportedType(SymTypeExpression ste) {
    if (ste.isGenericType()) {
      SymTypeOfGenerics genericType = ste.asGenericType();
      if (genericType.sizeArguments() == 1) {
        return getImportedType(genericType.getArgument(0));
      }
    }
    if (ste.isObjectType() && GenService.isCDType(ste)) {
      return Optional.of(ste);
    }
    return Optional.empty();
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    TSImportHelper importHelper = new TSImportHelper(glex, cdType, hwPath, outPath);
    String importerQName = this.astcdPackage + "." + cdType.getName();

    CDSymbolTables.getAttributesInHierarchy(domainType).forEach(a -> addJavaImport(cdType, a.getSymbol().getType()));
    CDSymbolTables.getAssociationsInHierarchy(domainType).forEach(assocSide -> {
      getPackageFromFQNString(assocSide.getSymbol().getType().printFullName())
          .ifPresent(cdPackage ->
              importHelper.addImport(Joiners.DOT.join(cdPackage.toLowerCase(),
                  getTypeFromFQNString(assocSide.getSymbol().getType().printFullName())), importerQName));
    });
    domainType.getInterfaceList().forEach(cdInterface ->
        cdInterface.getDefiningSymbol().ifPresent(definingSymbol ->
            importHelper.addImport(Joiners.DOT.join(definingSymbol.getPackageName().toLowerCase(),
                definingSymbol.getName()), importerQName)));
    domainType.getSuperclassList().forEach(cdExtension ->
        cdExtension.getDefiningSymbol().ifPresent(definingSymbol ->
            importHelper.addImport(Joiners.DOT.join(definingSymbol.getPackageName().toLowerCase(),
                definingSymbol.getName()), importerQName)));

    if (domainType.isPresentSymbol()) {
      CDTypeSymbol symbol = domainType.getSymbol();
      importHelper.addImport(
          Joiners.DOT.join(symbol.getPackageName().toLowerCase(), symbol.getName()), importerQName);

      for (var superType : symbol.getSuperTypesList()) {
        ASTCDCompilationUnit scCD = UMLPSymbolTables.getDiagramFor((ASTCDType) superType.getTypeInfo().getAstNode());
        importHelper.addImport(Joiners.DOT.join(getPackageName(scCD, DEFAULT_PACKAGE), superType.print() + SUFFIX), importerQName);
      }
    }
  }

}
