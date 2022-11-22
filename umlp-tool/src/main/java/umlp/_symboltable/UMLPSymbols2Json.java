/* (c) https://github.com/MontiCore/monticore */
package umlp._symboltable;

import de.monticore.cd4code._symboltable.ICD4CodeScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.symboltable.serialization.JsonPrinter;
import umlp._visitor.UMLPHandler;
import umlp._visitor.UMLPTraverser;

public class UMLPSymbols2Json extends UMLPSymbols2JsonTOP implements UMLPHandler {
  public UMLPSymbols2Json() {
    getTraverser().setUMLPHandler(this);
  }

  public UMLPSymbols2Json(UMLPTraverser traverser, JsonPrinter printer) {
    super(traverser, printer);
    getTraverser().setUMLPHandler(this);
  }

  @Override
  public void traverse(IUMLPScope node) {
    UMLPHandler.super.traverse(node);

    for (de.monticore.cdbasis._symboltable.CDPackageSymbol s : node.getLocalCDPackageSymbols()) {
      getTraverser().traverse((ICD4CodeScope) s.getSpannedScope());
    }
  }
}
