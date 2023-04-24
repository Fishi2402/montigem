package umlp.generator.util;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import umlp.UMLPMill;
import umlp._visitor.UMLPTraverser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UMLPSymbolTables {
  public static Set<ASTCDType> collectConcreteSubclassesInclSelf(ASTCDCompilationUnit origCD, ASTCDType domainType) {
    Set<ASTCDType> concreteSubclasses = new HashSet<>();
    CDTypeSymbol symbol = domainType.getSymbol();
    if(symbol.isIsClass() && !symbol.isIsAbstract()) {
      concreteSubclasses.add(domainType);
    }

    CDBasisVisitor2 visitor = new CDBasisVisitor2() {
      @Override
      public void visit(ASTCDClass node) {
        if(node.getModifier().isAbstract()){
          return;
        }

        List<ASTCDType> transitiveSuperTypes = CDSymbolTables.getTransitiveSuperTypes(node);
        if(transitiveSuperTypes.contains(domainType)) {
          concreteSubclasses.add(node);
        }
      }
    };
    UMLPTraverser traverser = UMLPMill.traverser();
    traverser.add4CDBasis(visitor);
    origCD.accept(traverser);
    return concreteSubclasses;
  }
}
