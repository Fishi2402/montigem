/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.Joiners;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.util.UMLPSymbolTables;

import java.util.Optional;

public class TSObserverDecorator extends ObserverDecorator {

  protected String astcdPackage;

  public TSObserverDecorator(final GlobalExtensionManagement glex) {
    super(glex);
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
        getCD4C().addImport(observerType, Joiners.DOT.join(astcdPackage, ste.get().print()));
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
    super.addImports(domainType, cdType);

    if (domainType.isPresentSymbol()) {
      CDTypeSymbol symbol = domainType.getSymbol();
      getCD4C().addImport(cdType,
          Joiners.DOT.join(symbol.getPackageName().toLowerCase(), symbol.getName()));

      for (var superType : symbol.getSuperTypesList()) {
        ASTCDCompilationUnit scCD = UMLPSymbolTables.getDiagramFor((ASTCDType) superType.getTypeInfo().getAstNode());
        getCD4C().addImport(cdType, Joiners.DOT.join(getPackageName(scCD, DEFAULT_PACKAGE), superType.print() + SUFFIX));
      }
    }
  }

}
