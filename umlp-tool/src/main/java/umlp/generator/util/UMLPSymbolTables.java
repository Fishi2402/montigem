package umlp.generator.util;

import de.monticore.ast.ASTNode;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.symbols.basicsymbols._ast.ASTType;
import de.monticore.symboltable.IScope;
import de.monticore.types.check.SymTypeExpression;
import umlp.UMLPMill;
import umlp._symboltable.IUMLPArtifactScope;
import umlp._visitor.UMLPTraverser;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class UMLPSymbolTables {

  public static Set<ASTCDType> collectConcreteSubclassesInclSelf(ASTCDCompilationUnit origCD, ASTCDType domainType){
    return collectConcreteSubclassesInclSelfWithDist(origCD, domainType).keySet();
  }

  public static Map<ASTCDType, Integer> collectConcreteSubclassesInclSelfWithDist(ASTCDCompilationUnit origCD, ASTCDType domainType) {
    CDTypeSymbol symbol = domainType.getSymbol();
    Map<ASTCDType, Integer> concreteSubclasses = new LinkedHashMap<>();
    if(symbol.isIsClass() && !symbol.isIsAbstract()) {
      concreteSubclasses.put(domainType, 0);
    }

    CDBasisVisitor2 visitor = new CDBasisVisitor2() {
      @Override
      public void visit(ASTCDClass node) {
        if(node.getModifier().isAbstract()){
          return;
        }

        Map<ASTCDType, Integer> transitiveSuperTypes = getTransitiveSuperTypesWithDist(node, 1);
        if(transitiveSuperTypes.containsKey(domainType)) {
          concreteSubclasses.put(node, transitiveSuperTypes.get(domainType));
        }
      }
    };
    UMLPTraverser traverser = UMLPMill.traverser();
    traverser.add4CDBasis(visitor);
    origCD.accept(traverser);

    return concreteSubclasses;
  }

  public static Map<ASTCDInterface, Integer> getTransitiveSuperInterfacesWithDistance(ASTCDType ast, int curDist) {
    Map<ASTCDInterface, Integer> interfaces = new LinkedHashMap<>();
    for (SymTypeExpression s : ast.getSymbol().getInterfaceList()) {
      ASTType node = s.getTypeInfo().getAstNode();
      if (node instanceof ASTCDInterface) {
        ASTCDInterface interf = (ASTCDInterface) node;
        int dist = Math.min(curDist, interfaces.getOrDefault(interf, Integer.MAX_VALUE));
        interfaces.put(interf, dist);
        getTransitiveSuperInterfacesWithDistance(interf, curDist + 1).forEach((t, d) -> {
          int tmp = Math.min(d, interfaces.getOrDefault(t, Integer.MAX_VALUE));
          interfaces.put(t, tmp);
        });
      }
    }
    return interfaces;
  }

  public static Map<ASTCDType, Integer> getTransitiveSuperTypesWithDist(ASTCDType ast, int curDist) {
    Map<ASTCDType, Integer> types = new LinkedHashMap<>();
    if (ast instanceof ASTCDClass) {
      if (ast.getSymbol().isPresentSuperClass()) {
        ASTType superClass = ast.getSymbol().getSuperClass().getTypeInfo().getAstNode();
        if (superClass instanceof ASTCDType) {
          int dist = Math.min(curDist, types.getOrDefault(superClass, Integer.MAX_VALUE));
          types.put((ASTCDType) superClass, dist);
          getTransitiveSuperTypesWithDist((ASTCDType) superClass, curDist + 1).forEach((t, d) ->{
            int tmp = Math.min(d, types.getOrDefault(t, Integer.MAX_VALUE));
            types.put(t, tmp);
          });
        }
      }
    }
    getTransitiveSuperInterfacesWithDistance(ast, curDist + 1).forEach((t, d) -> {
      int tmp = Math.min(d, types.getOrDefault(t, Integer.MAX_VALUE));
      types.put(t, tmp);
    });
    return types;
  }

  public static Map<ASTCDAttribute, ASTCDType> getAttributesInHierarchyWithDefiningType(ASTCDType type){
    Map<ASTCDAttribute, ASTCDType> res = new LinkedHashMap<>();
    for (ASTCDAttribute a : type.getCDAttributeList()) {
      res.put(a, type);
    }

    for (Map.Entry<ASTCDType, Integer> e : getTransitiveSuperTypesWithDist(type, 1).entrySet()) {
      for(ASTCDAttribute a : e.getKey().getCDAttributeList()){
        res.put(a, e.getKey());
      }
    }

    return res;
  }

  public static ASTCDCompilationUnit getDiagramFor(ASTCDType type){
    return getDiagramFor((ASTNode) type);
  }


  public static ASTCDCompilationUnit getDiagramFor(ASTCDAssocSide a) {
    return getDiagramFor((ASTNode) a);
  }

  public static ASTCDCompilationUnit getDiagramFor(ASTCDAssociation a) {
    return getDiagramFor((ASTNode) a);
  }

  private static ASTCDCompilationUnit getDiagramFor(ASTNode node){
    IScope cur = node.getEnclosingScope();
    while(!(cur instanceof IUMLPArtifactScope)){
      cur = cur.getEnclosingScope();
    }
    IUMLPArtifactScope asArtifactScope = (IUMLPArtifactScope) cur;
    return (ASTCDCompilationUnit) asArtifactScope.getAstNode();
  }
}
